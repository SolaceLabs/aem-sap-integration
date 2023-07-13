package customer.capm_erp_simulation.models.salesOrder;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@Builder
public class OrderScheduleType {

    private String scheduleNumber;
    private float quantity;
    private String uom;

}
