package customer.cap_event_consumer.model.notifications;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NotificationHeaderType {
    private String notificationId;
    private String type;
    private String creator;
    private String changer;
    private Date changedate;
    private String plant;
    private List<NotificationItemType> notificationItem;
}
