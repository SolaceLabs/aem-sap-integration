package customer.cap_event_consumer.model.chartOfAccount;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompanyCodeDataType {

    private String companyCode;
    private String financialBudgetItem;
    private String fieldStatusGroup;
    private String taxCode;
    private String currency;

}
