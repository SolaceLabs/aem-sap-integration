package customer.capm_erp_simulation.producer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import customer.capm_erp_simulation.models.businessPartner.BusinessPartner;
import customer.capm_erp_simulation.models.chartOfAccount.AccountHeader;
import customer.capm_erp_simulation.models.materialMaster.MaterialType;
import customer.capm_erp_simulation.models.notifications.Notifications;
import customer.capm_erp_simulation.models.salesOrder.SalesOrderType;
import customer.capm_erp_simulation.service.SolaceEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class ScheduledTaskService {

    @Autowired
    private SolaceEventPublisher solaceEventPublisher;

    ObjectMapper objectMapper = new ObjectMapper();

    @Value("classpath:test-data/sales-order-create-test-data.json")
    Resource salesOrderCreateTestDataFile;
    @Value("classpath:test-data/sales-order-update-test-data.json")
    Resource salesOrderUpdateTestDataFile;

    @Value("classpath:test-data/business-partner-create-test-data.json")
    Resource businessPartnerCreateTestDataFile;
    @Value("classpath:test-data/business-partner-update-test-data.json")
    Resource businessPartnerUpdateTestDataFile;

    @Value("classpath:test-data/material-master-create-test-data.json")
    Resource materialMasterCreateTestDataFile;
    @Value("classpath:test-data/material-master-update-test-data.json")
    Resource materialMasterUpdateTestDataFile;

    @Value("classpath:test-data/chartOfAccounts-create-test-data.json")
    Resource chartOfAccountsCreateTestDataFile;
    @Value("classpath:test-data/chartOfAccounts-update-test-data.json")
    Resource chartOfAccountsUpdateTestDataFile;

    @Value("classpath:test-data/notificationEvent-create-test-data.json")
    Resource notificationCreateTestDataFile;

    @Value("classpath:test-data/notificationEvent-update-test-data.json")
    Resource notificationUpdateTestDataFile;

    private List<SalesOrderType> salesOrderCreateTestDataList;
    private List<SalesOrderType> salesOrderUpdateTestDataList;
    private List<BusinessPartner> businessPartnerCreateTestDataList;
    private List<BusinessPartner> businessPartnerUpdateTestDataList;
    private List<MaterialType> materialMasterCreateTestDataList;
    private List<MaterialType> materialMasterUpdateTestDataList;
    private List<AccountHeader> chartOfAccountsCreateTestDataList;
    private List<AccountHeader> chartOfAccountsUpdateTestDataList;
    private List<Notifications> notificationsCreateTestDataList;
    private List<Notifications> notificationsUpdateTestDataList;

    private Random random = new Random();

    protected void simulateSalesOrderCreateEvents() {
        int randomIndex = random.nextInt(salesOrderCreateTestDataList.size());
        SalesOrderType salesOrderCreate = salesOrderCreateTestDataList.get(randomIndex);
        solaceEventPublisher.publishSalesOrderEvent(salesOrderCreate, "create");
    }

    protected void simulateSalesOrderChangeEvents() {
        int randomIndex = random.nextInt(salesOrderUpdateTestDataList.size());
        SalesOrderType salesOrderCreate = salesOrderUpdateTestDataList.get(randomIndex);
        solaceEventPublisher.publishSalesOrderEvent(salesOrderCreate, "change");
    }

    protected void simulateBusinessPartnerCreateEvents() {
        int randomIndex = random.nextInt(businessPartnerCreateTestDataList.size());
        BusinessPartner businessPartnerCreateEvent = businessPartnerCreateTestDataList.get(randomIndex);
        solaceEventPublisher.publishBusinessPartnerEvent(businessPartnerCreateEvent, "create");
    }

    protected void simulateBusinessPartnerChangeEvents() {
        int randomIndex = random.nextInt(businessPartnerUpdateTestDataList.size());
        BusinessPartner businessPartnerUpdateEvent = businessPartnerUpdateTestDataList.get(randomIndex);
        solaceEventPublisher.publishBusinessPartnerEvent(businessPartnerUpdateEvent, "change");
    }

    protected void simulateMaterialMasterCreateEvents() {
        int randomIndex = random.nextInt(materialMasterCreateTestDataList.size());
        MaterialType material = materialMasterCreateTestDataList.get(randomIndex);
        solaceEventPublisher.publishMaterialMasterCreateEvents(material, "create");
    }

    protected void simulateMaterialMasterChangeEvents() {
        int randomIndex = random.nextInt(materialMasterUpdateTestDataList.size());
        MaterialType material = materialMasterUpdateTestDataList.get(randomIndex);
        solaceEventPublisher.publishMaterialMasterUpdateEvents(material, "change");
    }

    protected void simulateChartOfAccountsCreateEvents() {
        int randomIndex = random.nextInt(chartOfAccountsCreateTestDataList.size());
        AccountHeader accountHeader = chartOfAccountsCreateTestDataList.get(randomIndex);
        solaceEventPublisher.publishChartOfAccountsEvents(accountHeader, "create");
    }

    protected void simulateChartOfAccountsChangeEvents() {
        int randomIndex = random.nextInt(chartOfAccountsUpdateTestDataList.size());
        AccountHeader accountHeader = chartOfAccountsUpdateTestDataList.get(randomIndex);
        solaceEventPublisher.publishChartOfAccountsEvents(accountHeader, "change");
    }

    protected void simulateNotificationCreateEvents() {
        int randomIndex = random.nextInt(notificationsCreateTestDataList.size());
        Notifications notification = notificationsCreateTestDataList.get(randomIndex);
        solaceEventPublisher.publishNotificationEvents(notification, "create");
    }

    protected void simulateNotificationChangeEvents() {
        int randomIndex = random.nextInt(notificationsUpdateTestDataList.size());
        Notifications notification = notificationsUpdateTestDataList.get(randomIndex);
        solaceEventPublisher.publishNotificationEvents(notification, "change");
    }

    @EventListener
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {
        try {
            salesOrderCreateTestDataList = objectMapper.readValue(salesOrderCreateTestDataFile.getInputStream(), new TypeReference<List<SalesOrderType>>() {
            });
            salesOrderUpdateTestDataList = objectMapper.readValue(salesOrderUpdateTestDataFile.getInputStream(), new TypeReference<List<SalesOrderType>>() {
            });
            businessPartnerCreateTestDataList = objectMapper.readValue(businessPartnerCreateTestDataFile.getInputStream(), new TypeReference<List<BusinessPartner>>() {
            });
            businessPartnerUpdateTestDataList = objectMapper.readValue(businessPartnerUpdateTestDataFile.getInputStream(), new TypeReference<List<BusinessPartner>>() {
            });
            materialMasterCreateTestDataList = objectMapper.readValue(materialMasterCreateTestDataFile.getInputStream(), new TypeReference<List<MaterialType>>() {
            });
            materialMasterUpdateTestDataList = objectMapper.readValue(materialMasterUpdateTestDataFile.getInputStream(), new TypeReference<List<MaterialType>>() {
            });
            chartOfAccountsCreateTestDataList = objectMapper.readValue(chartOfAccountsCreateTestDataFile.getInputStream(), new TypeReference<List<AccountHeader>>() {
            });
            chartOfAccountsUpdateTestDataList = objectMapper.readValue(chartOfAccountsUpdateTestDataFile.getInputStream(), new TypeReference<List<AccountHeader>>() {
            });
            notificationsCreateTestDataList = objectMapper.readValue(notificationCreateTestDataFile.getInputStream(), new TypeReference<List<Notifications>>() {
            });
            notificationsUpdateTestDataList = objectMapper.readValue(notificationUpdateTestDataFile.getInputStream(), new TypeReference<List<Notifications>>() {
            });

            log.debug("materialMasterCreateTestDataList:{}", materialMasterCreateTestDataList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
