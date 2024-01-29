package customer.capm_erp_simulation.models.salesOrder;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderItemType {

    private String item;
    private String material;
    private String materialType;
    private String itemType;
    private String itemDescription;
    private List<OrderScheduleType> orderSchedule;

}
