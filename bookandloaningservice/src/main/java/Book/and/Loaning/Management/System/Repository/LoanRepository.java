package Book.and.Loaning.Management.System.Repository;
import Book.and.Loaning.Management.System.DTO.BookDTO;
import Book.and.Loaning.Management.System.Entity.BookEntity;
import Book.and.Loaning.Management.System.Entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {

    @Query("SELECT l FROM LoanEntity l JOIN BookEntity b ON l.bookId = b.id WHERE b.ownerId = :ownerId")
    List<LoanEntity> findLoansByOwnerId(@Param("ownerId") UUID ownerId);
    @Query("SELECT l FROM LoanEntity l JOIN BookEntity b ON l.bookId = b.id " +
            "WHERE b.ownerId = :ownerId AND l.loanStatus = 'PENDING_APPROVAL'")
    List<LoanEntity> findPendingLoansByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT b FROM BookEntity b JOIN LoanEntity l ON b.id = l.bookId WHERE b.ownerId = :ownerId AND l.loanStatus = 'LOANED'")
    List<BookEntity> findBorrowedBooksByOwnerId(@Param("ownerId") UUID ownerId);
}