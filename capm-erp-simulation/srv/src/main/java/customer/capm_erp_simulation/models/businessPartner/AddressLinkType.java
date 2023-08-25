package customer.capm_erp_simulation.models.businessPartner;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AddressLinkType {

    private String addressNumber;
    private Date dateFrom;
    private List<AddressType> address;


}
