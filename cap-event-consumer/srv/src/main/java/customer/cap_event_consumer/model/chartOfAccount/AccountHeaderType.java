package customer.cap_event_consumer.model.chartOfAccount;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AccountHeaderType {

    private String chartOfAccounts;
    private String accountNumber;
    private String creator;
    private List<CompanyCodeDataType> companyCodeData;
}
