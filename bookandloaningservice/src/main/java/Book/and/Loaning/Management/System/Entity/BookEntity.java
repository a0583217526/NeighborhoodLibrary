
package Book.and.Loaning.Management.System.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "books")
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String author;
    @Enumerated(EnumType.STRING)
    private Category category;
    private String description;
    private String bookCondition;
    private int loanDurationDays;
    @Column(nullable = true)
    private boolean available;
    private UUID ownerId;
    @Enumerated(EnumType.STRING)
    private TargetAge targetAge;
    @ElementCollection
    private List<UUID> waitingList = new ArrayList<>();
}
