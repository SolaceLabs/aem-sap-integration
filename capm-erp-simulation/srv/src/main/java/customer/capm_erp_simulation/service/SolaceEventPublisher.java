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
import customer.capm_erp_simulation.models.businessPartner.BusinessPartnerType;
import customer.capm_erp_simulation.models.chartOfAccount.AccountHeaderType;
import customer.capm_erp_simulation.models.materialMaster.MaterialMasterType;
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
            params.put("salesOrg", salesOrderEvent.getOrderHeader().getSalesOrg());
            params.put("distributionChannel", salesOrderEvent.getOrderHeader().getDistributionChannel());
            params.put("division", salesOrderEvent.getOrderHeader().getDivision());
            params.put("customerId", salesOrderEvent.getOrderHeader().getCustomer().getCustomerId());
            params.put("verb", verb);
            String topicString = StringSubstitutor.replace(configProperties.getSalesOrderTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published salesOrderEvent event :{} on topic : {}", salesOrderCreateJson, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        }
    }

    public void publishBusinessPartnerEvent(final BusinessPartnerType businessPartnerEvent, final String verb) throws JsonProcessingException {
        try {
            String businessPartnerEventJson = Obj.writeValueAsString(businessPartnerEvent);
            final OutboundMessage message = messageBuilder.build(businessPartnerEventJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("verb", verb);
            params.put("businessPartnerType", businessPartnerEvent.getBusinessPartnerType());
            params.put("partnerId", businessPartnerEvent.getPartnerId());
            String topicString = StringSubstitutor.replace(configProperties.getBusinessPartnerTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published businessPartnerEvent event :{} on topic : {}", businessPartnerEventJson, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        }
    }

    public void publishMaterialMasterEvents(final MaterialMasterType materialMasterEvent, final String verb) throws JsonProcessingException {
        try {
            String materialMasterEventJson = Obj.writeValueAsString(materialMasterEvent);
            final OutboundMessage message = messageBuilder.build(materialMasterEventJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("verb", verb);
            params.put("materialClass", materialMasterEvent.getMaterialClass());
            params.put("industrySector", materialMasterEvent.getIndustrySector());
            params.put("materialType", materialMasterEvent.getMaterialType());
            params.put("materialNumber", materialMasterEvent.getMaterialNumber());
            params.put("maintenanceStatusGroup", materialMasterEvent.getMaintenanceStatusGroup());
            params.put("maintenanceStatusMaterial", materialMasterEvent.getMaintenanceStatusMaterial());
            params.put("deletionIndicator", materialMasterEvent.getDeletionIndicator());
            String topicString = StringSubstitutor.replace(configProperties.getMaterialMasterTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published materialMasterEvent event :{} on topic : {}", materialMasterEventJson, topicString);
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
