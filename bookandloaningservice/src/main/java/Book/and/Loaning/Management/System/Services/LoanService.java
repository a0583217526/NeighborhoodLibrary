package Book.and.Loaning.Management.System.Services;

import Book.and.Loaning.Management.System.DTO.BookDTO;
import Book.and.Loaning.Management.System.DTO.LoanDTO;
import Book.and.Loaning.Management.System.Entity.BookEntity;
import Book.and.Loaning.Management.System.Entity.LoanEntity;
import Book.and.Loaning.Management.System.Entity.LoanStatus;
import Book.and.Loaning.Management.System.Exceptions.BookServiceException;
import Book.and.Loaning.Management.System.Exceptions.LoanServiceException;
import Book.and.Loaning.Management.System.Mapping.LoanMapper; // ייבוא המאפר
import Book.and.Loaning.Management.System.Repository.BookRepository;
import Book.and.Loaning.Management.System.Repository.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final LoanMapper loanMapper;

    public List<LoanDTO> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(loanMapper::toDTO)
                .collect(Collectors.toList());
    }

    public LoanDTO getLoanById(UUID id) {
        LoanEntity entity = loanRepository.findById(id)
                .orElseThrow(() -> new LoanServiceException("Loan with ID " + id + " not found."));
        return loanMapper.toDTO(entity);
    }

    @Transactional
    public LoanDTO createLoan(LoanDTO loanDto) {
        // המרה ל-Entity לצורך בדיקות עסקיות
        LoanEntity loan = loanMapper.toEntity(loanDto);

        BookEntity book = bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new LoanServiceException("Cannot create loan, Book ID " + loan.getBookId() + " does not exist."));

        if (!book.isAvailable()) {
            throw new LoanServiceException("Book '" + book.getTitle() + "' is not available.");
        }

        if (book.getOwnerId() == loan.getBorrowerID()) {
            throw new LoanServiceException("You cannot borrow your own book.");
        }

        loan.setLoanStatus(LoanStatus.PENDING_APPROVAL);
        bookService.updateAvailability(loan.getBookId(), false);
        LoanEntity savedLoan = loanRepository.save(loan);
        return loanMapper.toDTO(savedLoan);
    }

    public LoanDTO updateLoan(LoanDTO loanDto) {
        if (!loanRepository.existsById(loanDto.getId())) {
            throw new LoanServiceException("Cannot update, Loan ID " + loanDto.getId() + " not found.");
        }
        LoanEntity entity = loanMapper.toEntity(loanDto);
        LoanEntity updatedLoan = loanRepository.save(entity);
        return loanMapper.toDTO(updatedLoan);
    }

    @Transactional
    public void deleteLoan(UUID id) {
        LoanEntity loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanServiceException("Cannot delete, Loan ID " + id + " not found."));
        UUID bookId = loan.getBookId();
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new LoanServiceException("Cannot delete, Book ID " + bookId + " not found."));

        bookService.updateAvailability(bookId, true);
        loanRepository.deleteById(id);
    }

    public List<LoanDTO> findLoansByOwnerId(UUID ownerId) {
        if (ownerId == null || ownerId.equals(new UUID(0, 0))) {
            throw new LoanServiceException("Invalid User ID: " + ownerId);
        }
        List<LoanEntity> loans = loanRepository.findLoansByOwnerId(ownerId);
        return loans.stream().map(loanMapper::toDTO).toList();
    }
    @Transactional
    public LoanEntity approveLoan(UUID loanId, UUID currentUserId) {

        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanServiceException("ההשאלה לא נמצאה"));
        if (loan.getLoanStatus() != LoanStatus.PENDING_APPROVAL) {
            throw new LoanServiceException("רק בקשות במצב המתנה ניתנות לאישור");
        }
        BookEntity book = bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new BookServiceException("הספר לא נמצא"));
        if (book.getOwnerId() != currentUserId) {
            throw new LoanServiceException("אין לך הרשאה לאשר השאלה לספר זה!");
        }
        book.setAvailable(false);
        bookRepository.save(book);
        loan.setLoanStatus(LoanStatus.LOANED);
        loan.setLoanDate(LocalDate.now());
        loan.setRequiredDate(LocalDate.now().plusDays(30));

        return loanRepository.save(loan);
    }

    @Transactional
    public LoanEntity rejectLoan(UUID loanId, UUID currentUserId) {

        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanServiceException("ההשאלה לא נמצאה"));

        if (loan.getLoanStatus() != LoanStatus.PENDING_APPROVAL) {
            throw new LoanServiceException("ניתן לדחות רק בקשות במצב המתנה");
        }
        BookEntity book = bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new BookServiceException("הספר לא נמצא"));
        if (book.getOwnerId() != currentUserId) {
            throw new LoanServiceException("אין לך הרשאה לדחות השאלה לספר זה!");
        }


        loan.setLoanStatus(LoanStatus.REJECTED);
        book.setAvailable(true);

        bookRepository.save(book);
        return loanRepository.save(loan);
    }
    public List<LoanDTO> getPendingRequestsByOwnerId(UUID ownerId) {

        List<LoanEntity> pendingLoans = loanRepository.findPendingLoansByOwnerId(ownerId);
        return pendingLoans.stream()
                .map(loanMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public LoanDTO returnBook(UUID loanId) {
        // 1. מציאת ההשאלה והספר
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanServiceException("Loan not found."));

        BookEntity book = bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new BookServiceException("Book not found."));

        // 2. עדכון סטטוס ההשאלה
        loan.setLoanStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        // 3. טיפול בתור הממתינים
        UUID nextUserId = bookService.getNextInLine(book.getId());

        if (nextUserId != null) {
            // אם יש ממתין, הספר נשאר לא זמין עבור הקהל הרחב
            // ומחכה למשתמש הבא בתור
            bookService.popFromWaitingList(book.getId());

            // כאן אפשר להוסיף קריאה לשירות התראות (NotificationService)
            // שמעדכן את nextUserId שהספר זמין עבורו
        } else {
            // אם אין ממתינים, הספר חוזר להיות זמין לכולם
            book.setAvailable(true);
        }

        // 4. שמירת השינויים
        bookRepository.save(book);
        return loanMapper.toDTO(loanRepository.save(loan));
    }
    @Transactional
    public List<BookDTO> getBorrowedBooksByOwnerId(UUID ownerId) {
        List <LoanDTO> loanDTOList = findLoansByOwnerId(ownerId);
        return loanDTOList.stream().filter(loan ->loan.getLoanStatus() == LoanStatus.LOANED)
                .map(loan -> bookService.getBookById(loan.getBookId()))
                .collect(Collectors.toList());
    }
    //פונקציה להארכת זמן
    @Transactional
    public LoanDTO timeExtension(UUID loanId, int loanDurationDays) {

        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanServiceException("Loan not found with ID: " + loanId));

        if (loan.getLoanStatus() != LoanStatus.LOANED) {
            throw new LoanServiceException("Only active loans (LOANED) can be extended.");
        }
        if (loanDurationDays <= 0) {
            throw new LoanServiceException("Extension duration must be positive.");
        }
        if (loan.getRequiredDate() == null) {
            throw new LoanServiceException("Loan has no required date set.");
        }
        loan.setRequiredDate(loan.getRequiredDate().plusDays(loanDurationDays));

        BookEntity book = bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new BookServiceException("Book not found with ID: " + loan.getBookId()));

        book.setLoanDurationDays(book.getLoanDurationDays() + loanDurationDays);

        loanRepository.save(loan);
        bookRepository.save(book);

        return loanMapper.toDTO(loan);
    }

}
