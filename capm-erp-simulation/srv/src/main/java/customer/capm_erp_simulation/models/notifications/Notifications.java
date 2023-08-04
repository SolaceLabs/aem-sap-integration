package customer.capm_erp_simulation.models.notifications;

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
