package com.library.smart_library_ai.service;

import com.library.smart_library_ai.dto.BookDto;
import com.library.smart_library_ai.dto.BookRecommendationDto;
import com.library.smart_library_ai.dto.LoanHistoryDto;
import com.library.smart_library_ai.dto.UserDto;
import com.library.smart_library_ai.entity.BookRecommendation;
import com.library.smart_library_ai.repository.BookRecommendationRepository;
import com.library.smart_library_ai.service.clients.GroqAiService;
import com.library.smart_library_ai.service.clients.LoanClient;
import com.library.smart_library_ai.service.clients.UserClient;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookRecommendationService {
    private final GroqAiService aiService;
    private final UserClient userClient;
    private final LoanClient loanClient;
    private final BookRecommendationRepository repository;
    private final BookCacheService bookCacheService;
    public BookRecommendationService(BookRecommendationRepository repository,BookCacheService bookCacheService,UserClient userClient,LoanClient loanClient, GroqAiService aiService) {
        this.userClient = userClient;
        this.loanClient = loanClient;
        this.repository = repository;
        this.aiService=aiService;
        this.bookCacheService=bookCacheService;
    }


    // ----------------------------1-----------------------------------
    @Scheduled(cron = "0 0 3 * * SUN")
    public void processWeeklyRecommendations() {
        // 1. משכנו את כל המשתמשים
        List<UserDto> allUsers = userClient.getAllUsers();
        List<BookDto> books = bookCacheService.getBooks();
        for (UserDto user : allUsers) {
            try {
                // 2. ה-Enrichment: שליפת ההיסטוריה הספציפית לכל משתמש והוספה ל-DTO
                List<LoanHistoryDto> history = loanClient.getLoanHistory(user.getUserId());
                user.setLoanHistoryDto(history); // כעת המידע נמצא בתוך האובייקט!

                // 3. ממשיכים לעיבוד כרגיל
                getRecommendationsForUser(user);
            } catch (Exception e) {
                System.err.println("Error for user " + user.getUserId() + ": " + e.getMessage());
            }
        }
    }
    //----------------------------2----------------------------------------------
    //פונקציה 2- מקבלת את המשתמש והסטורית ההשאלות שלו ומחזירה את הנוסחא התמאימה
    public String CheckUserStrategy(UserDto user)
    {
        List<LoanHistoryDto> history=loanClient.getLoanHistory(user.getUserId());
        if (history == null || history.isEmpty()) {
            return "המשתמש גר בעיר: " + user.getCity() + ". " +
                    "מאחר ואין היסטוריית השאלות, השתמש בחישוב הבא: " +
                    "50% גיל + 30% פופולריות + 20% מרחק = Score. " +
                    "העדף ספרים מהעיר: " + user.getCity();
        }
        long booksFromUserCity=history.stream()
                .filter(loan->user.getCity().equals(loan.getBranchCity()))
                .count();
        double ratio = (double) booksFromUserCity / history.size();
        if(ratio>0.6)
        {
            // מעל 60% מהספרים מהעיר שלו - משקל גבוה למיקום
            return "המשתמש גר בעיר: " + user.getCity() + ". " +
                    "מאחר ומעל 60% מהספרים שהשאיל הם מהעיר שלו, השתמש בחישוב הבא: " +
                    "20% קטגוריה + 20% גיל + 20% פופולריות + 40% מרחק = Score. " +
                    "העדף ספרים מהעיר: " + user.getCity();
        }
        else {
            // פחות מ-60% מהספרים מהעיר שלו - משקל נמוך למיקום
            return "המשתמש גר בעיר: " + user.getCity() + ". " +
                    "מאחר ופחות מ-60% מהספרים שהשאיל הם מהעיר שלו, השתמש בחישוב הבא: " +
                    "50% קטגוריה + 20% גיל + 20% פופולריות + 10% מרחק = Score. " +
                    "העדף ספרים מהעיר: " + user.getCity();
        }
    }
    public BookRecommendationDto getRecommendationFromDB(int userId) {
        BookRecommendation entity = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No recommendations found for user: " + userId));

        // המרה מ-Entity ל-DTO
        return new BookRecommendationDto(
                entity.getReasonForChoice(),
                entity.getGeneratedDate(),
                entity.getBookIds()
        );
    }
    //--------------------------------------3-------------------------------
    //פונקציה 3-בונה את הפרומפט ל-AI מקבלת את הנוסחא שנבחרה
    public String BuildAiPrompt( String strategyPromptBlock) {

        List<BookDto> books=bookCacheService.getBooks();
        StringBuilder prompt = new StringBuilder();

        prompt.append(" חלק 1: אסטרטגיית המשקלים והנוסחה המוגדרת \n");
        prompt.append(strategyPromptBlock);
        prompt.append("\n\n");

        prompt.append(" חלק 2: רשימת הספרים להשאלה כרגע בספריה \n");
        for (BookDto book : books) {
            prompt.append("- מזהה: ").append(book.getBookId())
                    .append(", שם: ").append(book.getTitle())
                    .append(", קטגוריה: ").append(book.getCategory())
                    .append(", מיקום: ").append(book.getCity())
                    .append("\n");
        }
        // חלק 3 - הוראות הפורמט (JSON קשיח)
        prompt.append("\n=== חלק 3: משימה והוראות פורמט ===\n");
        prompt.append("בחר בדיוק 5 ספרים מהרשימה לעיל. הבחירה חייבת להתבצע בקפידה על פי אסטרטגיית המשקלים והנוסחה הנתונה תחת הכותרת 'חלק 1'.\n");
        prompt.append("החזר את התשובה בפורמט JSON תקני בלבד המורכב מאובייקט יחיד עם השדות הבאים:\n");
        prompt.append("bookIds - מערך של מזהי הספרים שנבחרו\n");
        prompt.append("reasonForChoice - נימוק קצר וכולל בעברית מדוע נבחרו ספרים אלו\n");
        prompt.append("דגש לנימוק: עליך להרחיב ולהסביר בפירוט מדוע נבחרו ספרים אלו, ולציין במפורש האם הבחירה התבססה בעיקר על מיקום מגורי המשתמש, על הקטגוריות המועדפות עליו, או על שילוב של השניים בהתאם לאחוזים שבנוסחה.\n");
        prompt.append("אל תכתוב שום טקסט חופשי, הסברים או הקדמות לפני או אחרי ה-JSON. החזר רק את ה-JSON עצמו.\n\n");

        prompt.append("מבנה ה-JSON הנדרש:\n");
        prompt.append("{\n");
        prompt.append("  \"bookIds\": [2, 4, 5, 6, 7],\n");
        prompt.append("  \"reasonForChoice\": \"נימוק מורחב ומפורט בעברית\"\n");
        prompt.append("}\n");
        return prompt.toString();
    }
    //--------------------------------------4-------------------------------

    public BookRecommendationDto executeAiRequest(String prompt) {

        if (prompt == null || prompt.trim().isEmpty()) {
            System.err.println("Error: Provided AI JSON string is empty.");
            return new com.library.smart_library_ai.dto.BookRecommendationDto();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // 1. פירוק ה-JSON שמילא את השדות שהגיעו מה-AI
            BookRecommendationDto parsedDto = objectMapper.readValue(
                    prompt,
                    BookRecommendationDto.class
            );

            parsedDto.setGeneratedDate(LocalDateTime.now());

            System.out.println("Successfully parsed AI JSON and enriched with current date.");
            return parsedDto;

        } catch (Exception e) {
            System.err.println("Failed to parse AI JSON string: " + e.getMessage());
            e.printStackTrace();
            return new BookRecommendationDto();
        }
    }
    //--------------------------------------5-------------------------------

    @Transactional
    public boolean saveRecommendations(int userId, BookRecommendationDto aiResponse) {
        // הגנה ראשונית - אם האובייקט שהתקבל פגום או ריק מרשימת ספרים
        if (aiResponse == null || aiResponse.getBookIds() == null || aiResponse.getBookIds().isEmpty()) {
            System.err.println("Error: Cannot save recommendations because AI response or book list is empty.");
            return false;
        }

        try {
            // אופציונלי: ניקוי המלצות שבועיות קודמות של המשתמש כדי למנוע כפילויות בטבלה
            repository.deleteByUserId(userId);

            // 1. יצירת אובייקט ה-Entity שלכן
            BookRecommendation entity = new BookRecommendation();

            // 2. מיפוי והזרקת הנתונים מתוך הפרמטרים וה-DTO
            entity.setUserId(userId);
            entity.setReasonForChoice(aiResponse.getReasonForChoice());
            entity.setBookIds(aiResponse.getBookIds());
            entity.setGeneratedDate(aiResponse.getGeneratedDate()); // התאריך שכבר מולא בפונקציה 4

            // 3. שמירה פיזית בבסיס הנתונים (מפעיל אוטומטית גם את שמירת ה-ElementCollection)
            repository.save(entity);

            System.out.println("Successfully saved weekly recommendations to DB for user: " + userId);
            return true;

        } catch (Exception e) {
            System.err.println("Failed to save recommendations to the database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    //--------------------------------------6-------------------------------
    public UserDto GetUserById(int userId) {
        UserDto user = userClient.getUserById(userId);
        return user;
    }

    public void getRecommendationsForUser(UserDto user) {
        List<BookDto> books=bookCacheService.getBooks();
        // שליחה לפונקציה 2
        String strategy = CheckUserStrategy(user);
        // שליחה לפונקציה 3
        String prompt = BuildAiPrompt(strategy);
        // שליחה לפונקציה 4

        // שליחה לפונקציה 5
        String response =aiService.generateAsync(prompt);
        // שליחה לפונקציה 6
        BookRecommendationDto aiJsonExecution = executeAiRequest(response);

        boolean isSaved = saveRecommendations(user.getUserId(), aiJsonExecution);

        if (!isSaved) {
            System.err.println("Failed to save recommendations to database for user: " + user.getUserId());
            throw new RuntimeException("Database save failed");
        }
    }




    // ---------------------------------------------------------

    // ADD – יצירת Recommendation בסיסי למשתמש חדש
    // ---------------------------------------------------------
    @Transactional
    public boolean add(int userId) {
        // 1. בדיקה חד-פעמית: אם כבר קיים, אל תעשה כלום או תזרוק שגיאה
        if (repository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Recommendation already exists for user " + userId);
        }

        // 2. יצירת רשומה חדשה וריקה
        BookRecommendation entity = new BookRecommendation();
        entity.setUserId(userId);
        // שאר השדות נשארים null כרגע, כי זה רק האתחול

        try {

            repository.save(entity);
            UserDto user=userClient.getUserById(userId);
            getRecommendationsForUser(user);
            System.out.println("Initialized recommendation record for user: " + userId);
            return true;
        } catch (Exception e) {
            System.err.println("Error initializing recommendation for user " + userId + ": " + e.getMessage());
            return false;
        }
    }
    // ---------------------------------------------------------
    // DELETE – מחיקת Recommendation של משתמש
    // ---------------------------------------------------------
    @Transactional
    public void delete(int userId) {
        repository.deleteByUserId(userId);
    }

    public List<LoanHistoryDto> loadHistory(){
        List<LoanHistoryDto> history = new ArrayList<>();

        LoanHistoryDto loan1 = new LoanHistoryDto();
        loan1.setBookId(101);
        loan1.setBookCategory("מדע");
        loan1.setBranchCity("תל אביב");
        history.add(loan1);
        LoanHistoryDto loan2 = new LoanHistoryDto();
        loan2.setBookId(102);
        loan2.setBookCategory("היסטוריה");
        loan2.setBranchCity("תל אביב");
        history.add(loan2);
        LoanHistoryDto loan3 = new LoanHistoryDto();
        loan3.setBookId(103);
        loan3.setBookCategory("מדע בדיוני");
        loan3.setBranchCity("תל אביב");
        history.add(loan3);
        LoanHistoryDto loan4 = new LoanHistoryDto();
        loan4.setBookId(104);
        loan4.setBookCategory("פנטזיה");
        loan4.setBranchCity("ירושלים");
        history.add(loan4);

        return history;
    }
    public List<BookDto>bookIntialzation(){
        List<BookDto> booksList = new ArrayList<>();

        booksList.add(new BookDto(1, "המדען הקטן", "מדע", "תל אביב"));
        booksList.add(new BookDto(2, "הארי פוטר", "פנטזיה", "ירושלים"));
        booksList.add(new BookDto(3, "הכדור בידיים שלנו", "ספורט", "ראשון לציון"));
        booksList.add(new BookDto(4, "עולם חדש מופלא", "מדע בדיוני", "חיפה"));
        booksList.add(new BookDto(5, "ילד השמות", "היסטוריה", "תל אביב"));
        booksList.add(new BookDto(6, "הפילוסוף הצעיר", "פילוסופיה", "ראשון לציון"));
        booksList.add(new BookDto(7, "ריצה אל הגבול", "ספורט", "באר שבע"));
        return booksList;
    }
}






