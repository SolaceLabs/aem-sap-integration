package customer.cap_event_consumer.model.notifications;

import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@ToString
@Builder
public class NotificationItemType {
    private String item;
    private String defectClass;
    private String material;
    private String puchaseOrg;
}
