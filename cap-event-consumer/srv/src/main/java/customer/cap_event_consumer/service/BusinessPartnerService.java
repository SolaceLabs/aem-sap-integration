package customer.cap_event_consumer.service;

import cds.gen.sap.aem.integration.Adress;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cds.Result;
import com.sap.cds.Row;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Upsert;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpsert;
import com.sap.cds.services.persistence.PersistenceService;
import customer.cap_event_consumer.model.businessPartner.BusinessPartner;
import customer.cap_event_consumer.model.businessPartner.BusinessPartnerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class BusinessPartnerService {
    final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    PersistenceService persistenceService;

    public void processIncomingBusinessPartnerEvent(final String businessPartnerEventJson) {
        log.info("The incoming businessPartner event is :{}", businessPartnerEventJson);
        try {
            final BusinessPartner businessPartnerListEvent = mapper.readValue(businessPartnerEventJson, BusinessPartner.class);
            final BusinessPartnerType businessPartnerTypeFromEvent = businessPartnerListEvent.getBusinessPartner().get(0);
            final String partnerIdFromEvent = businessPartnerTypeFromEvent.getPartnerId();
            final Result businessPartnerSearchQueryResult = queryBusinessPartner(partnerIdFromEvent);
            businessPartnerSearchQueryResult.stream().findFirst().ifPresentOrElse(searchResultElement -> processForExistingSapBusinessPartnerEvent(searchResultElement, businessPartnerTypeFromEvent),
                    () -> insertSapBusinessPartnerEvent(businessPartnerTypeFromEvent));
        } catch (JsonMappingException jsonMappingException) {
            log.error("A mappingException was encountered while converting from the json payload:{} to pojo :{}", businessPartnerEventJson, jsonMappingException);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("A JsonProcessingException was encountered while converting from the json payload:{} to pojo :{}", businessPartnerEventJson, jsonProcessingException);
        }
    }

    private Result queryBusinessPartner(final String partnerIdFromEvent) {
        final CqnSelect businessPartnerSearchQuery = Select.from("sap.aem.integration.businessPartner").where(b -> b.get("partnerId").is(partnerIdFromEvent));
        return persistenceService.run(businessPartnerSearchQuery);
    }

    private Result queryAddressLink(final String businessPartnerPK) {
        final CqnSelect businessPartnerSearchQuery = Select.from("sap.aem.integration.adressLink").where(b -> b.get("businessPartner_id").is(businessPartnerPK));
        return persistenceService.run(businessPartnerSearchQuery);
    }

    private Result deleteAdress(final String sapAdressLinkPK) {
        final CqnDelete adressDelete = Delete.from("sap.aem.integration.adress").where(b -> b.get("adressLink_id").is(sapAdressLinkPK));
        return persistenceService.run(adressDelete);
    }

    private Result deleteAdressLink(final String sapAdressLinkPK) {
        final CqnDelete adressDelete = Delete.from("sap.aem.integration.adressLink").where(b -> b.get("id").is(sapAdressLinkPK));
        return persistenceService.run(adressDelete);
    }

    private Result deleteBusinessPartner(final String sapBusinessPartnerPK) {
        final CqnDelete businessPartnerDelete = Delete.from("sap.aem.integration.businessPartner").where(b -> b.get("id").is(sapBusinessPartnerPK));
        return persistenceService.run(businessPartnerDelete);
    }

    private void processForExistingSapBusinessPartnerEvent(final Row sapBusinessPartner, final BusinessPartnerType businessPartnerTypeFromEvent) {

        cds.gen.sap.aem.integration.BusinessPartner businessPartnerDbRow = sapBusinessPartner.as(cds.gen.sap.aem.integration.BusinessPartner.class);
        Result addressLinkResult = queryAddressLink(businessPartnerDbRow.getId());
        addressLinkResult.stream().forEach(
                adressLinkRow -> {
                    cds.gen.sap.aem.integration.AdressLink adressLink = adressLinkRow.as(cds.gen.sap.aem.integration.AdressLink.class);
                    Result deleteAdressResult = deleteAdress(adressLink.getId());
                    Result deleteAdressLinkResult = deleteAdressLink(adressLink.getId());
                }
        );
        Result deleteBusinessPartnerResult = deleteBusinessPartner(businessPartnerDbRow.getId());
        cds.gen.sap.aem.integration.BusinessPartner newSapBusinessPartnerEvent = transformToSAPModel(businessPartnerTypeFromEvent);
        saveSapBusinessPartnerModel(newSapBusinessPartnerEvent);

    }

    private void saveSapBusinessPartnerModel(final cds.gen.sap.aem.integration.BusinessPartner modelTobeSaved) {
        CqnUpsert upsert = Upsert.into("sap.aem.integration.businessPartner").entry(modelTobeSaved);
        Result result = persistenceService.run(upsert);
        log.info("The result of the persistenceService is :{}", result.toJson());
    }

    private void insertSapBusinessPartnerEvent(final BusinessPartnerType businessPartnerTypeFromEvent) {
        cds.gen.sap.aem.integration.BusinessPartner newSapBusinessPartnerEvent = transformToSAPModel(businessPartnerTypeFromEvent);
        saveSapBusinessPartnerModel(newSapBusinessPartnerEvent);
    }


    private cds.gen.sap.aem.integration.BusinessPartner transformToSAPModel(final BusinessPartnerType businessPartner) {

        cds.gen.sap.aem.integration.BusinessPartner sapBusinessPartner = cds.gen.sap.aem.integration.BusinessPartner.create();
        sapBusinessPartner.setId(UUID.randomUUID().toString());
        sapBusinessPartner.setPartnerId(businessPartner.getPartnerId());
        sapBusinessPartner.setFirstName(businessPartner.getFirstName());
        sapBusinessPartner.setLastName(businessPartner.getLastName());
        sapBusinessPartner.setBusinessPartnerType(businessPartner.getBusinessPartnerType());
        List<cds.gen.sap.aem.integration.AdressLink> sapAdressLinkList = new ArrayList<>();

        businessPartner.getAdressLink().forEach(adressLink -> {
                    final cds.gen.sap.aem.integration.AdressLink sapAdressLink = cds.gen.sap.aem.integration.AdressLink.create();
                    sapAdressLink.setId(UUID.randomUUID().toString());
                    sapAdressLink.setAdressNumber(adressLink.getAdressNumber());
                    sapAdressLink.setDateFrom(convertToLocalDateViaMilisecond(adressLink.getDateFrom()));
                    final List<Adress> sapAdresses = new ArrayList<>();
                    adressLink.getAdress().forEach(adress ->
                            {
                                final Adress sapAdress = Adress.create();
                                sapAdress.setId(UUID.randomUUID().toString());
                                sapAdress.setNation(adress.getNation());
                                sapAdress.setCity(adress.getCity());
                                sapAdress.setStreet(adress.getCity());
                                sapAdress.setHouseNumber(adress.getHouseNumber());
                                sapAdress.setPostalCode(adress.getPostalCode());
                                sapAdress.setCountry(adress.getCountry());
                                sapAdresses.add(sapAdress);
                            }
                    );
                    sapAdressLink.setAddress(sapAdresses);
                    sapAdressLinkList.add(sapAdressLink);
                }
        );
        sapBusinessPartner.setAdressLink(sapAdressLinkList);
        return sapBusinessPartner;
    }

    private LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
