package customer.cap_event_consumer.model.notifications;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NotificationItemType {
    private String item;
    private String defectClass;
    private String material;
    private String puchaseOrg;
}
