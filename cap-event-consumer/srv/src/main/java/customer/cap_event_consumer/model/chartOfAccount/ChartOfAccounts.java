package customer.cap_event_consumer.model.chartOfAccount;

import lombok.*;

import java.util.List;
@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChartOfAccounts {
    private List<AccountHeaderType> accountHeader;
}
