package customer.cap_event_consumer.model.salesOrder;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class SalesOrderType {

    private List<OrderHeaderType> orderHeader;

}