package customer.cap_event_consumer.model.notifications;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Notifications {
    private List<NotificationHeaderType> notificationHeader;
}
