package Book.and.Loaning.Management.System.Controller;
import Book.and.Loaning.Management.System.DTO.BookDTO;
import Book.and.Loaning.Management.System.Entity.BookEntity;
import Book.and.Loaning.Management.System.Entity.Category;
import Book.and.Loaning.Management.System.Services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books") // הגדרת נתיב בסיס
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }
    @PutMapping("/updateBook")
    public ResponseEntity<BookDTO> updateBook(@RequestBody BookDTO book) {
        return ResponseEntity.ok(bookService.updateBook(book));
    }
    @DeleteMapping("/removeBook/{id}")
    public ResponseEntity<Void> removeBook(@PathVariable UUID id) {
        bookService.removeBook(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/addBook")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO book) {
        return ResponseEntity.ok(bookService.addBook(book));
    }
    @GetMapping("/getBooksBorrowedByUser/{borrowerId}")
    public ResponseEntity<List<BookDTO>> getBooksBorrowedByUser(@PathVariable UUID borrowerId) {
        List<BookDTO> borrowedBooks = bookService.getBooksBorrowedByUser(borrowerId);
        return ResponseEntity.ok(borrowedBooks);
    }
    @GetMapping("/getBooksByOwner/{ownerId}")
    public ResponseEntity<List<BookDTO>> getBooksByOwner(@PathVariable UUID ownerId) {
        List<BookDTO> books = bookService.getAllBooksByOwnerId(ownerId);
        return ResponseEntity.ok(books);
    }
    @PatchMapping("/updateAvailability/{id}")
    public ResponseEntity<Void> updateAvailability(@PathVariable UUID id, @RequestParam boolean isAvailable) {
        bookService.updateAvailability(id, isAvailable);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Category category) {
        List<BookDTO> books = bookService.searchBooks(title, author, category);
        return ResponseEntity.ok(books);
    }
}
