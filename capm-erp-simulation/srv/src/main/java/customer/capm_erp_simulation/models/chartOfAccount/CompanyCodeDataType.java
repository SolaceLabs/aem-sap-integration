package customer.capm_erp_simulation.models.chartOfAccount;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@Builder
public class CompanyCodeDataType {

    private String companyCode;
    private String financialBudgetItem;
    private String fieldStatusGroup;
    private String taxCode;
    private String currency;

}
