package Book.and.Loaning.Management.System.DTO;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserToUserNotificationDTO {

    private UUID fromUserId;

    private UUID toUserId;

    private List<AdditionalDetailDTO> additionalDetails;
}

