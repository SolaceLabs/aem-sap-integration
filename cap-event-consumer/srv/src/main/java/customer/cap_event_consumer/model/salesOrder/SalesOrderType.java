package customer.cap_event_consumer.model.salesOrder;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class SalesOrderType {

    private OrderHeaderType orderHeader;

}