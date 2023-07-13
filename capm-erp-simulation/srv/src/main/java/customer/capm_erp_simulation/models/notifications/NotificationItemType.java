package customer.capm_erp_simulation.models.notifications;

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
