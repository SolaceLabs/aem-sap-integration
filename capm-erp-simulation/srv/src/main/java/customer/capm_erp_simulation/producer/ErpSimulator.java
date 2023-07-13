package customer.capm_erp_simulation.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import customer.capm_erp_simulation.models.businessPartner.BusinessPartnerType;
import customer.capm_erp_simulation.models.chartOfAccount.AccountHeaderType;
import customer.capm_erp_simulation.models.materialMaster.MaterialMasterType;
import customer.capm_erp_simulation.models.notifications.NotificationHeaderType;
import customer.capm_erp_simulation.models.salesOrder.SalesOrderType;
import customer.capm_erp_simulation.service.SolaceEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ErpSimulator {

    @Autowired
    private SolaceEventPublisher solaceEventPublisher;

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    protected void simulateSalesOrderEvents() throws JsonProcessingException {
        final SalesOrderType salesOrderCreate = buildSalesOrderEvent();
        solaceEventPublisher.publishSalesOrderEvent(salesOrderCreate, "create");
        final SalesOrderType salesOrderUpdate = buildSalesOrderEvent();
        solaceEventPublisher.publishSalesOrderEvent(salesOrderUpdate, "change");
    }


    /*@Scheduled(fixedDelay = 10000, initialDelay = 20000)
    protected void simulateBusinessPartnerEvents() throws JsonProcessingException {
        final BusinessPartnerType businessPartnerCreateEvent = buildBusinessPartnerEvent();
        solaceEventPublisher.publishBusinessPartnerEvent(businessPartnerCreateEvent, "create");
        final BusinessPartnerType businessPartnerUpdateEvent = buildBusinessPartnerEvent();
        solaceEventPublisher.publishBusinessPartnerEvent(businessPartnerUpdateEvent, "change");
    }


    @Scheduled(fixedDelay = 10000, initialDelay = 20000)
    protected void simulateMaterialMasterEvents() throws JsonProcessingException {
        final MaterialMasterType materialMasterCreate = buildMaterialMasterEvent();
        solaceEventPublisher.publishMaterialMasterEvents(materialMasterCreate, "create");
        final MaterialMasterType materialMasterUpdate = buildMaterialMasterEvent();
        solaceEventPublisher.publishMaterialMasterEvents(materialMasterUpdate, "change");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 20000)
    protected void simulateChartOfAccountsEvents() throws JsonProcessingException {
        final AccountHeaderType chartOfAccountCreate = buildAccountHeaderEvent();
        solaceEventPublisher.publishChartOfAccountsEvents(chartOfAccountCreate, "create");
        final AccountHeaderType chartOfAccountUpdate = buildAccountHeaderEvent();
        solaceEventPublisher.publishChartOfAccountsEvents(chartOfAccountUpdate, "change");
    }
    @Scheduled(fixedDelay = 10000, initialDelay = 20000)
    protected void simulateNotificationEvents() throws JsonProcessingException {
        final NotificationHeaderType notificationCreate = buildNotificationEvent();
        solaceEventPublisher.publishNotificationEvents(notificationCreate, "create");
        final NotificationHeaderType notificationUpdate = buildNotificationEvent();
        solaceEventPublisher.publishNotificationEvents(notificationUpdate, "change");
    }*/

    private NotificationHeaderType buildNotificationEvent() {
        EasyRandom generator = new EasyRandom();
        NotificationHeaderType notificationHeaderEvent = generator.nextObject(NotificationHeaderType.class);
        return notificationHeaderEvent;
    }

    private AccountHeaderType buildAccountHeaderEvent() {
        EasyRandom generator = new EasyRandom();
        AccountHeaderType accountHeaderEvent = generator.nextObject(AccountHeaderType.class);
        return accountHeaderEvent;
    }

    private MaterialMasterType buildMaterialMasterEvent() {
        EasyRandom generator = new EasyRandom();
        MaterialMasterType materialMasterEvent = generator.nextObject(MaterialMasterType.class);
        return materialMasterEvent;
    }

    private BusinessPartnerType buildBusinessPartnerEvent() {
        EasyRandom generator = new EasyRandom();
        BusinessPartnerType businessPartnerEvent = generator.nextObject(BusinessPartnerType.class);
        return businessPartnerEvent;
    }

    private SalesOrderType buildSalesOrderEvent() {
        EasyRandom generator = new EasyRandom();
        SalesOrderType salesOrder = generator.nextObject(SalesOrderType.class);
        return salesOrder;
    }

}
