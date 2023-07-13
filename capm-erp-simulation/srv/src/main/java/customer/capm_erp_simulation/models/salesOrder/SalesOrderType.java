package customer.capm_erp_simulation.models.salesOrder;

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