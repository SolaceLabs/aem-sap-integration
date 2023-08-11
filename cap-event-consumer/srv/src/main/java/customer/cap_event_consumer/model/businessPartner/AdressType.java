package customer.cap_event_consumer.model.businessPartner;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AdressType {


    private String nation;
    private String city;
    private String street;
    private String houseNumber;
    private String postalCode;
    private String country;

}
