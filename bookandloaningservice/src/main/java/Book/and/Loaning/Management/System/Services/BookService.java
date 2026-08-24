package Book.and.Loaning.Management.System.Services;
import Book.and.Loaning.Management.System.DTO.BookDTO;
import Book.and.Loaning.Management.System.Entity.BookEntity;
import Book.and.Loaning.Management.System.Entity.Category;
import Book.and.Loaning.Management.System.Exceptions.BookServiceException;
import Book.and.Loaning.Management.System.Mapping.BookMapper;
import Book.and.Loaning.Management.System.Repository.BookRepository;
import Book.and.Loaning.Management.System.Repository.BookSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    //פעולות CRUD בסיסיות:
    @Transactional
    public List<BookDTO> getAllBooks() {
        List<BookEntity> bookEntities = bookRepository.findAll();
        return bookEntities.stream().
                map(bookMapper::toDTO).
                toList();
    }
    @Transactional
    public BookDTO getBookById(UUID id){
        BookEntity bookEntity = bookRepository.findById(id)
               .orElseThrow(()-> new BookServiceException("Book with ID " + id + " not found."));
        return bookMapper.toDTO(bookEntity);
    }
    @Transactional
    public BookDTO updateBook(BookDTO book){
        if (!bookRepository.existsById(book.getId())) {
            throw new BookServiceException("Cannot update, bookID: " + book.getId() + " not found.");
        }
        BookEntity entity = bookMapper.toEntity(book);
        return bookMapper.toDTO(bookRepository.save(entity));
    }
    @Transactional
    public void removeBook(UUID id){
        if (!bookRepository.existsById(id)) {
            throw new BookServiceException("Cannot delete, bookID: " + id + " not found.");
        }
        bookRepository.deleteById(id);
    }
    @Transactional
    public BookDTO addBook(BookDTO book){
        BookEntity entity = bookMapper.toEntity(book);
        entity.setAvailable(true);
        return bookMapper.toDTO(bookRepository.save(entity));
    }
    //פעולות נוספות:
    @Transactional
    public List<BookDTO> getBooksBorrowedByUser(UUID borrowerId) {
        List<BookEntity> books = bookRepository.findBorrowedBooksByBorrowerId(borrowerId);

        return books.stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public void updateAvailability(UUID bookId, boolean isAvailable) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookServiceException("Book with ID " + bookId + " not found."));
        book.setAvailable(isAvailable);
        bookRepository.save(book);
    }

    public List<BookDTO> getAllBooksByOwnerId(UUID ownerId) {
        List<BookEntity> books = bookRepository.findByOwnerId(ownerId);
        return books.stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }
    public List<BookDTO> searchBooks(String title, String author, Category category) {
        Specification<BookEntity> spec = BookSpecifications.filterBooks(title, author, category);
        return bookRepository.findAll(spec).stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addToWaitingList(UUID bookId, UUID userId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookServiceException("Book with ID " + bookId + " not found."));

        if (book.isAvailable()) {
            throw new BookServiceException("Book is available, no need to join waiting list.");
        }

        if (book.getWaitingList().contains(userId)) {
            throw new BookServiceException("User is already in the waiting list.");
        }

        book.getWaitingList().add(userId);
        bookRepository.save(book);
    }
    @Transactional
    public UUID getNextInLine(UUID bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookServiceException("Book with ID " + bookId + " not found."));

        if (book.getWaitingList().isEmpty()) {
            return null;
        }

        return book.getWaitingList().get(0);
    }
    @Transactional
    public void removeFromWaitingList(UUID bookId, UUID userId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookServiceException("Book with ID " + bookId + " not found."));

        book.getWaitingList().remove(userId);
        bookRepository.save(book);
    }
    @Transactional
    public void popFromWaitingList(UUID bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookServiceException("Book with ID " + bookId + " not found."));

        if (!book.getWaitingList().isEmpty()) {
            book.getWaitingList().remove(0);
            bookRepository.save(book);
        }
    }
}
