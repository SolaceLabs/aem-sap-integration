package customer.cap_event_consumer.model.businessPartner;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AdressLinkType {

    private String adressNumber;
    private Date dateFrom;
    private List<AdressType> adress;


}
