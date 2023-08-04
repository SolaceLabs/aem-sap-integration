package customer.capm_erp_simulation.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import customer.capm_erp_simulation.models.businessPartner.BusinessPartner;
import customer.capm_erp_simulation.models.chartOfAccount.AccountHeaderType;
import customer.capm_erp_simulation.models.chartOfAccount.ChartOfAccounts;
import customer.capm_erp_simulation.models.materialMaster.MaterialCreate;
import customer.capm_erp_simulation.models.materialMaster.MaterialMasterCreate;
import customer.capm_erp_simulation.models.materialMaster.MaterialMasterUpdate;
import customer.capm_erp_simulation.models.materialMaster.MaterialUpdate;
import customer.capm_erp_simulation.models.notifications.NotificationHeaderType;
import customer.capm_erp_simulation.models.notifications.Notifications;
import customer.capm_erp_simulation.models.salesOrder.SalesOrderType;
import customer.capm_erp_simulation.service.SolaceEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class ErpSimulator {

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
    private MaterialMasterCreate materialMasterCreateTestDataList;
    private MaterialMasterUpdate materialMasterUpdateTestDataList;
    private ChartOfAccounts chartOfAccountsCreateTestDataList;
    private ChartOfAccounts chartOfAccountsUpdateTestDataList;
    private Notifications notificationsCreateTestDataList;
    private Notifications notificationsUpdateTestDataList;

    private Random random = new Random();

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    protected void simulateSalesOrderCreateEvents() throws JsonProcessingException {
        int randomIndex = random.nextInt(salesOrderCreateTestDataList.size());
        SalesOrderType salesOrderCreate = salesOrderCreateTestDataList.get(randomIndex);
        solaceEventPublisher.publishSalesOrderEvent(salesOrderCreate, "create");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 15000)
    protected void simulateSalesOrderUpdateEvents() throws JsonProcessingException {
        int randomIndex = random.nextInt(salesOrderUpdateTestDataList.size());
        SalesOrderType salesOrderCreate = salesOrderUpdateTestDataList.get(randomIndex);
        solaceEventPublisher.publishSalesOrderEvent(salesOrderCreate, "update");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    protected void simulateBusinessPartnerCreateEvents() throws JsonProcessingException {
        int randomIndex = random.nextInt(businessPartnerCreateTestDataList.size());
        BusinessPartner businessPartnerCreateEvent = businessPartnerCreateTestDataList.get(randomIndex);
        solaceEventPublisher.publishBusinessPartnerEvent(businessPartnerCreateEvent, "create");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 15000)
    protected void simulateBusinessPartnerUpdateEvents() throws JsonProcessingException {
        int randomIndex = random.nextInt(businessPartnerUpdateTestDataList.size());
        BusinessPartner businessPartnerUpdateEvent = businessPartnerUpdateTestDataList.get(randomIndex);
        solaceEventPublisher.publishBusinessPartnerEvent(businessPartnerUpdateEvent, "update");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    protected void simulateMaterialMasterCreateEvents() throws JsonProcessingException {
        int randomIndex = random.nextInt(materialMasterCreateTestDataList.getMaterial().length);
        MaterialCreate[] materials = materialMasterCreateTestDataList.getMaterial();
        final MaterialCreate material = materials[randomIndex];
        solaceEventPublisher.publishMaterialMasterCreateEvents(material, "create");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 15000)
    protected void simulateMaterialMasterUpdateEvents() throws JsonProcessingException {
        int randomIndex = random.nextInt(materialMasterUpdateTestDataList.getMaterial().length);
        MaterialUpdate[] materials = materialMasterUpdateTestDataList.getMaterial();
        final MaterialUpdate material = materials[randomIndex];
        solaceEventPublisher.publishMaterialMasterUpdateEvents(material, "update");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    protected void simulateChartOfAccountsCreateEvents() throws JsonProcessingException {
        int randomIndex = random.nextInt(chartOfAccountsCreateTestDataList.getAccountHeader().size());
        AccountHeaderType accountHeader = chartOfAccountsCreateTestDataList.getAccountHeader().get(randomIndex);
        solaceEventPublisher.publishChartOfAccountsEvents(accountHeader, "create");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 15000)
    protected void simulateChartOfAccountsUpdateEvents() throws JsonProcessingException {
        int randomIndex = random.nextInt(chartOfAccountsUpdateTestDataList.getAccountHeader().size());
        AccountHeaderType accountHeader = chartOfAccountsUpdateTestDataList.getAccountHeader().get(randomIndex);
        solaceEventPublisher.publishChartOfAccountsEvents(accountHeader, "update");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 20000)
    protected void simulateNotificationCreateEvents() throws JsonProcessingException {
        int randomIndex = random.nextInt(notificationsCreateTestDataList.getNotificationHeader().size());
        NotificationHeaderType notificationHeaderType = notificationsCreateTestDataList.getNotificationHeader().get(randomIndex);
        solaceEventPublisher.publishNotificationEvents(notificationHeaderType, "create");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 20000)
    protected void simulateNotificationUpdateEvents() throws JsonProcessingException {
        int randomIndex = random.nextInt(notificationsUpdateTestDataList.getNotificationHeader().size());
        NotificationHeaderType notificationHeaderType = notificationsUpdateTestDataList.getNotificationHeader().get(randomIndex);
        solaceEventPublisher.publishNotificationEvents(notificationHeaderType, "update");
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
            materialMasterCreateTestDataList = objectMapper.readValue(materialMasterCreateTestDataFile.getInputStream(), MaterialMasterCreate.class);
            materialMasterUpdateTestDataList = objectMapper.readValue(materialMasterUpdateTestDataFile.getInputStream(), MaterialMasterUpdate.class);
            chartOfAccountsCreateTestDataList = objectMapper.readValue(chartOfAccountsCreateTestDataFile.getInputStream(), ChartOfAccounts.class);
            chartOfAccountsUpdateTestDataList = objectMapper.readValue(chartOfAccountsUpdateTestDataFile.getInputStream(), ChartOfAccounts.class);
            notificationsCreateTestDataList = objectMapper.readValue(notificationCreateTestDataFile.getInputStream(), Notifications.class);
            notificationsUpdateTestDataList = objectMapper.readValue(notificationUpdateTestDataFile.getInputStream(), Notifications.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
