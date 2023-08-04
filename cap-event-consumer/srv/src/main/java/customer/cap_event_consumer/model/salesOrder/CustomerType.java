package customer.cap_event_consumer.model.salesOrder;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@Builder
public class CustomerType {
    private String customerId;
    private String customerName;
    private String zipCode;
    private String street;
    private String phone;
    private String country;
    private String city;
}
