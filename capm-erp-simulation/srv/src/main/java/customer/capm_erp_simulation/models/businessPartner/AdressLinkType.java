package customer.capm_erp_simulation.models.businessPartner;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@Builder
public class AdressLinkType {

    private String adressNumber;
    private Date dateFrom;
    private List<AdressType> adress;


}
