package customer.capm_erp_simulation.models.salesOrder;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class SalesOrderType {

    private List<OrderHeaderType> orderHeader;

}