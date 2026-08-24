package Book.and.Loaning.Management.System.Repository;
import Book.and.Loaning.Management.System.Entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, UUID> , JpaSpecificationExecutor<BookEntity> {

    @Query("SELECT b FROM BookEntity b JOIN LoanEntity l ON b.id = l.bookId WHERE l.borrowerID = :borrowerId")
    List<BookEntity> findBorrowedBooksByBorrowerId(@Param("borrowerId") UUID borrowerId);
    List<BookEntity> findByOwnerId(UUID ownerId);
}
