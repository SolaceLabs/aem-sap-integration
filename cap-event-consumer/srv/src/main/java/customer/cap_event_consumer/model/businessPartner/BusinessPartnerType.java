package customer.cap_event_consumer.model.businessPartner;

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
    private List<AdressLinkType> adressLink;

}
