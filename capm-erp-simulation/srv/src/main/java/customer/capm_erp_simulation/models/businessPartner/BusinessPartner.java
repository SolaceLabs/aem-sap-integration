package customer.capm_erp_simulation.models.businessPartner;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BusinessPartner {
    private List<BusinessPartnerType> businessPartner;
}
