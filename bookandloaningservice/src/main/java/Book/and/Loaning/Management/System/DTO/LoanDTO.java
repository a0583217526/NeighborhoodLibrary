package Book.and.Loaning.Management.System.DTO;

import Book.and.Loaning.Management.System.Entity.LoanStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class LoanDTO {
    private UUID id;
    private UUID bookId;
    private UUID borrowerID;
    private LoanStatus loanStatus;
    private LocalDate loanDate;
    private LocalDate requiredDate;
    private LocalDate returnDate;
}