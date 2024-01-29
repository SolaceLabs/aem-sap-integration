package customer.capm_erp_simulation.models.salesOrder;

import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal netvalue;
    private String currency;
    private List<CustomerType> customer;
    private List<OrderItemType> orderItem;

}
