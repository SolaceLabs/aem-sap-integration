package customer.capm_erp_simulation.models.salesOrder;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CustomerType {
    private String customerId;
    private String customerName;
    private String zipCode;
    private String street;
    private String phone;
    private String country;
    private String city;
    private List<EmailAddress> emailAddress;
}
