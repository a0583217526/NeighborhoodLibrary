package Book.and.Loaning.Management.System.DTO;
import Book.and.Loaning.Management.System.Entity.Category;
import Book.and.Loaning.Management.System.Entity.TargetAge;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class BookDTO {
    private UUID id;
    private String title;
    private String author;
    private Category category;
    private String description;
    private String bookCondition;
    private int loanDurationDays;
    private boolean available;
    private UUID ownerId;
    private TargetAge targetAge;
    private int waitingListSize;
}
