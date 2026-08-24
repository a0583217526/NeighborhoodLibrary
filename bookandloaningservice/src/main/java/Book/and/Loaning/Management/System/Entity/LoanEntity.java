package Book.and.Loaning.Management.System.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Data
@Table(name = "loans")
public class LoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID bookId;
    private UUID borrowerID;
    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;
    private LocalDate loanDate;
    private LocalDate requiredDate;
    private LocalDate returnDate;
    //private double fine; נוסיף במידת הצורך

}
