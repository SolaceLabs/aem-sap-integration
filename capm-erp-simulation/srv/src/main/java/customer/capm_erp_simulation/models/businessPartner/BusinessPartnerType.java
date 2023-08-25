package customer.capm_erp_simulation.models.businessPartner;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BusinessPartnerType {

    private String partnerId;
    private String firstName;
    private String lastName;
    private String businessPartnerType;
    private List<AddressLinkType> addressLink;

}
