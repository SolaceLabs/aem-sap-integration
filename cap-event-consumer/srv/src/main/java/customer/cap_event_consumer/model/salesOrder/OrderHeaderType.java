package customer.cap_event_consumer.model.salesOrder;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderHeaderType {

    private String salesOrderNumber;
    private String creator;
    private Date date;
    private String salesType;
    private String ordertype;
    private String salesOrg;
    private String distributionChannel;
    private String division;
    private List<CustomerType> customer;
    private List<OrderItemType> orderItem;

}
