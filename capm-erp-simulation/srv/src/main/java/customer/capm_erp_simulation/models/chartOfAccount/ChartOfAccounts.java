package customer.capm_erp_simulation.models.chartOfAccount;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChartOfAccounts {
    private List<AccountHeader> accountHeaderList;
}
