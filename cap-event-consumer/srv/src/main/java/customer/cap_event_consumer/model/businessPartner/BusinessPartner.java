package customer.cap_event_consumer.model.businessPartner;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BusinessPartner {
    private List<BusinessPartnerType> businessPartner;
}
