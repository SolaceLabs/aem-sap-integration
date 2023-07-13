package customer.capm_erp_simulation.models.salesOrder;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@Builder
public class OrderItemType {

    private String item;
    private String material;
    private String materialType;
    private String itemType;
    private List<OrderScheduleType> orderSchedule;

}
