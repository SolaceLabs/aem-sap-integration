package customer.capm_erp_simulation.models.salesOrder;

import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@ToString
@Builder
public class OrderHeaderType {

    private String salesOrderNumber;
    private String creator;
    private Date date;
    private String salesType;
    private String ordertype;
    private String salesOrg;
    private String distributionChannel;
    private String division;
    private CustomerType customer;
    private OrderItemType orderItem;

}
