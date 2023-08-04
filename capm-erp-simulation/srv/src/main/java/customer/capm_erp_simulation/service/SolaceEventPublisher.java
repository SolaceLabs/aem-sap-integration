package customer.capm_erp_simulation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solace.messaging.MessagingService;
import com.solace.messaging.PubSubPlusClientException;
import com.solace.messaging.config.SolaceProperties;
import com.solace.messaging.config.profile.ConfigurationProfile;
import com.solace.messaging.publisher.OutboundMessage;
import com.solace.messaging.publisher.OutboundMessageBuilder;
import com.solace.messaging.publisher.PersistentMessagePublisher;
import com.solace.messaging.resources.Topic;
import customer.capm_erp_simulation.config.SolaceConfigProperties;
import customer.capm_erp_simulation.models.businessPartner.BusinessPartner;
import customer.capm_erp_simulation.models.chartOfAccount.AccountHeaderType;
import customer.capm_erp_simulation.models.materialMaster.MaterialCreate;
import customer.capm_erp_simulation.models.materialMaster.MaterialUpdate;
import customer.capm_erp_simulation.models.notifications.NotificationHeaderType;
import customer.capm_erp_simulation.models.salesOrder.SalesOrderType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class SolaceEventPublisher {

    @Autowired
    private SolaceConfigProperties configProperties;

    private PersistentMessagePublisher publisher;
    private OutboundMessageBuilder messageBuilder;
    private MessagingService messagingService;
    ObjectMapper Obj = new ObjectMapper();

    @EventListener
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {

        // 1. Set up the properties including username, password, vpnHostUrl and other
        // control parameters.
        final Properties properties = setupPropertiesForConnection();

        // 2. Create the MessagingService object and establishes the connection with the
        // Solace event broker
        messagingService = MessagingService.builder(ConfigurationProfile.V1).fromProperties(properties).build();
        messagingService.connect(); // This is a blocking connect action

        // 3. Register event handlers and callbacks for connection error handling.
        setupConnectivityHandlingInMessagingService(messagingService);

        // 4. Build and start the publisher object
        publisher = messagingService.createPersistentMessagePublisherBuilder()
                .onBackPressureWait(1)
                .build();
//        publisher.setPublishFailureListener(e -> System.out.println("### FAILED PUBLISH " + e));
        publisher.start();

        // 5. Build the messageBuilder instance
        messageBuilder = messagingService.messageBuilder();

        publisher.setMessagePublishReceiptListener(publishReceipt -> {
            final PubSubPlusClientException e = publishReceipt.getException();
            if (e == null) {  // no exception, ACK, broker has confirmed receipt
                OutboundMessage outboundMessage = publishReceipt.getMessage();
                log.info(String.format("ACK for Message %s", outboundMessage));  // good enough, the broker has it now
            } else {// not good, a NACK
                Object userContext = publishReceipt.getUserContext();  // optionally set at publish()
                if (userContext != null) {
                    log.warn(String.format("NACK for Message %s - %s", userContext, e));
                } else {
                    OutboundMessage outboundMessage = publishReceipt.getMessage();  // which message got NACKed?
                    log.warn(String.format("NACK for Message %s - %s", outboundMessage, e));
                }
            }
        });
    }

    private static void setupConnectivityHandlingInMessagingService(final MessagingService messagingService) {
        messagingService.addServiceInterruptionListener(
                serviceEvent -> System.out.println("### SERVICE INTERRUPTION: " + serviceEvent.getCause()));
        messagingService.addReconnectionAttemptListener(
                serviceEvent -> System.out.println("### RECONNECTING ATTEMPT: " + serviceEvent));
        messagingService
                .addReconnectionListener(serviceEvent -> System.out.println("### RECONNECTED: " + serviceEvent));
    }

    private Properties setupPropertiesForConnection() {
        final Properties properties = new Properties();
        properties.setProperty(SolaceProperties.TransportLayerProperties.HOST, configProperties.getHostUrl()); // host:port
        properties.setProperty(SolaceProperties.ServiceProperties.VPN_NAME, configProperties.getVpnName()); // message-vpn
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_USER_NAME,
                configProperties.getUserName()); // client-username
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_PASSWORD,
                configProperties.getPassword()); // client-password
        properties.setProperty(SolaceProperties.TransportLayerProperties.RECONNECTION_ATTEMPTS,
                configProperties.getReconnectionAttempts()); // recommended settings
        properties.setProperty(SolaceProperties.TransportLayerProperties.CONNECTION_RETRIES_PER_HOST,
                configProperties.getConnectionRetriesPerHost());
        return properties;
    }

    public void publishSalesOrderEvent(final SalesOrderType salesOrderEvent, final String verb) throws JsonProcessingException {
        try {
            String salesOrderCreateJson = Obj.writeValueAsString(salesOrderEvent);
            final OutboundMessage message = messageBuilder.build(salesOrderCreateJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("salesOrg", salesOrderEvent.getOrderHeader().get(0).getSalesOrg());
            params.put("distributionChannel", salesOrderEvent.getOrderHeader().get(0).getDistributionChannel());
            params.put("division", salesOrderEvent.getOrderHeader().get(0).getDivision());
            params.put("customerId", salesOrderEvent.getOrderHeader().get(0).getCustomer().get(0).getCustomerId());
            params.put("verb", verb);
            String topicString = StringSubstitutor.replace(configProperties.getSalesOrderTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published salesOrderEvent event :{} on topic : {}", salesOrderCreateJson, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        }
    }

    public void publishBusinessPartnerEvent(final BusinessPartner businessPartnerEvent, final String verb) throws JsonProcessingException {
        try {
            String businessPartnerEventJson = Obj.writeValueAsString(businessPartnerEvent);
            final OutboundMessage message = messageBuilder.build(businessPartnerEventJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("verb", verb);
            params.put("businessPartnerType", businessPartnerEvent.getBusinessPartner().get(0).getBusinessPartnerType());
            params.put("partnerId", businessPartnerEvent.getBusinessPartner().get(0).getPartnerId());
            String topicString = StringSubstitutor.replace(configProperties.getBusinessPartnerTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published businessPartnerEvent event :{} on topic : {}", businessPartnerEventJson, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        }
    }

    public void publishMaterialMasterCreateEvents(final MaterialCreate materialCreate, final String verb) throws JsonProcessingException {
        try {
            String materialCreateEventJson = Obj.writeValueAsString(materialCreate);
            final OutboundMessage message = messageBuilder.build(materialCreateEventJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("verb", verb);
            params.put("materialClass", materialCreate.getMaterialClass());
            params.put("industrySector", materialCreate.getIndustrySector());
            params.put("materialType", materialCreate.getMaterialType());
            params.put("materialNumber", materialCreate.getMaterialNumber());
            params.put("maintenanceStatusGroup", materialCreate.getMaintenanceStatusGroup());
            params.put("maintenanceStatusMaterial", materialCreate.getMaintenanceStatusMaterial());
            params.put("deletionIndicator", materialCreate.getDeletionIndicator());
            String topicString = StringSubstitutor.replace(configProperties.getMaterialMasterTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published materialMasterCreateEvent event :{} on topic : {}", materialCreateEventJson, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        }
    }

    public void publishMaterialMasterUpdateEvents(final MaterialUpdate materialUpdate, final String verb) throws JsonProcessingException {
        try {
            String materialUpdateEventJson = Obj.writeValueAsString(materialUpdate);
            final OutboundMessage message = messageBuilder.build(materialUpdateEventJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("verb", verb);
            params.put("materialClass", materialUpdate.getMaterialClass());
            params.put("industrySector", materialUpdate.getIndustrySector());
            params.put("materialType", materialUpdate.getMaterialType());
            params.put("materialNumber", materialUpdate.getMaterialNumber());
            params.put("maintenanceStatusGroup", materialUpdate.getMaintenanceStatusGroup());
            params.put("maintenanceStatusMaterial", materialUpdate.getMaintenanceStatusMaterial());
            params.put("deletionIndicator", materialUpdate.getDeletionIndicator());
            String topicString = StringSubstitutor.replace(configProperties.getMaterialMasterTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published materialMasterUpdateEvent event :{} on topic : {}", materialUpdateEventJson, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        }
    }

    public void publishChartOfAccountsEvents(final AccountHeaderType chartOfAccountEvent, final String verb) throws JsonProcessingException {
        try {
            String chartOfAccountEventJson = Obj.writeValueAsString(chartOfAccountEvent);
            final OutboundMessage message = messageBuilder.build(chartOfAccountEventJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("verb", verb);
            String topicString = StringSubstitutor.replace(configProperties.getChartOfAccountsTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published chartOfAccountEvent event :{} on topic : {}", chartOfAccountEvent, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        }
    }

    public void publishNotificationEvents(final NotificationHeaderType notificationEvent, final String verb) throws JsonProcessingException {
        try {
            String notificationEventJson = Obj.writeValueAsString(notificationEvent);
            final OutboundMessage message = messageBuilder.build(notificationEventJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("verb", verb);
            params.put("type", notificationEvent.getType());
            params.put("plant", notificationEvent.getPlant());
            params.put("notificationId", notificationEvent.getNotificationId());

            String topicString = StringSubstitutor.replace(configProperties.getNotificationTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published notificationEvent event :{} on topic : {}", notificationEvent, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        }
    }

    @PreDestroy
    public void houseKeepingOnBeanDestroy() {
        log.info("The bean is getting destroyed, doing housekeeping activities");
        publisher.terminate(1000);
        messagingService.disconnect();
    }

}
