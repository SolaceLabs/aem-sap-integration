package customer.capm_erp_simulation.models.businessPartner;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AddressType {
    private String nation;
    private String city;
    private String street;
    private String houseNumber;
    private String postalCode;
    private String country;

}
