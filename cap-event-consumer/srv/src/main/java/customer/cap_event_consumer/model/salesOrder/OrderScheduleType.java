package customer.cap_event_consumer.model.salesOrder;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderScheduleType {

    private String scheduleNumber;
    private float quantity;
    private String uom;

}
