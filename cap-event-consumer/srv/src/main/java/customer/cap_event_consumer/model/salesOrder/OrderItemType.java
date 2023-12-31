package customer.cap_event_consumer.model.salesOrder;

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
    private List<OrderScheduleType> orderSchedule;

}
