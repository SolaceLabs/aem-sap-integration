package customer.cap_event_consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import customer.cap_event_consumer.model.businessPartner.BusinessPartner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessPartnerService {

    ObjectMapper mapper = new ObjectMapper();

    public void processIncomingBusinessPartnerEvent(final String businessPartnerEventJson) {

        log.info("The incoming businessPartner event is :{}", businessPartnerEventJson);
        try {
            BusinessPartner businessPartnerEvent =
                    mapper.readValue(businessPartnerEventJson, BusinessPartner.class);

            cds.gen.sap.aem.integration.BusinessPartner sapBusinessPartnerModel = transformToSAPModel(businessPartnerEvent);


            log.info("The parsed event is :{}", businessPartnerEvent);
        } catch (JsonMappingException jsonMappingException) {
            log.error("A mappingException was encountered while converting from the json payload:{} to pojo :{}", businessPartnerEventJson, jsonMappingException);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("A JsonProcessingException was encountered while converting from the json payload:{} to pojo :{}", businessPartnerEventJson, jsonProcessingException);
        }
    }


    private cds.gen.sap.aem.integration.BusinessPartner transformToSAPModel(final BusinessPartner businessPartner) {






        cds.gen.sap.aem.integration.BusinessPartner sapBusinessPartner = new cds.gen.sap.aem.integration.BusinessPartner();
        sapBusinessPartner.setPartnerId(businessPartner.getBusinessPartner().get(0).getPartnerId());
        sapBusinessPartner.setFirstName(businessPartner.getBusinessPartner().get(0).getFirstName());
        sapBusinessPartner.setLastName(businessPartner.getBusinessPartner().get(0).getLastName());
        sapBusinessPartner.setBusinessPartnerType(businessPartner.getBusinessPartner().get(0).getBusinessPartnerType());


        return sapBusinessPartner;

    }
}
