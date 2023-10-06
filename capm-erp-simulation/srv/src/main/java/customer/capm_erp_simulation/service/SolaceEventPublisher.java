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
import customer.capm_erp_simulation.models.chartOfAccount.AccountHeader;
import customer.capm_erp_simulation.models.config.SolaceConnectionParameters;
import customer.capm_erp_simulation.models.materialMaster.MaterialCreate;
import customer.capm_erp_simulation.models.materialMaster.MaterialUpdate;
import customer.capm_erp_simulation.models.notifications.NotificationHeaderType;
import customer.capm_erp_simulation.models.salesOrder.SalesOrderType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
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


    public boolean connectToBroker(final SolaceConnectionParameters solaceConnectionParameters) {
        try {
// 1. Set up the properties including username, password, vpnHostUrl and other
            // control parameters.
            final Properties properties = setupPropertiesForConnection(solaceConnectionParameters);

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
            return true;
        } catch (Exception exception) {
            log.error("Error encountered while connecting to the Solace broker, error :", exception.getMessage());
            return false;
        }

    }

    private static void setupConnectivityHandlingInMessagingService(final MessagingService messagingService) {
        messagingService.addServiceInterruptionListener(
                serviceEvent -> System.out.println("### SERVICE INTERRUPTION: " + serviceEvent.getCause()));
        messagingService.addReconnectionAttemptListener(
                serviceEvent -> System.out.println("### RECONNECTING ATTEMPT: " + serviceEvent));
        messagingService
                .addReconnectionListener(serviceEvent -> System.out.println("### RECONNECTED: " + serviceEvent));
    }

    private Properties setupPropertiesForConnection(final SolaceConnectionParameters solaceConnectionParameters) {
        final Properties properties = new Properties();
        properties.setProperty(SolaceProperties.TransportLayerProperties.HOST, solaceConnectionParameters.getHostUrl()); // host:port
        properties.setProperty(SolaceProperties.ServiceProperties.VPN_NAME, solaceConnectionParameters.getVpnName()); // message-vpn
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_USER_NAME,
                solaceConnectionParameters.getUserName());
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_PASSWORD,
                solaceConnectionParameters.getPassword());
        properties.setProperty(SolaceProperties.TransportLayerProperties.RECONNECTION_ATTEMPTS,
                configProperties.getReconnectionAttempts());
        properties.setProperty(SolaceProperties.TransportLayerProperties.CONNECTION_RETRIES_PER_HOST,
                configProperties.getConnectionRetriesPerHost());
        return properties;
    }

    public void publishSalesOrderEvent(final SalesOrderType salesOrderEvent, final String verb) {
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
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Error encountered while converting salesOrderEvent to JSON string, exception :", jsonProcessingException);
        }
    }

    public void publishBusinessPartnerEvent(final BusinessPartner businessPartnerEvent, final String verb) {
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
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Error encountered while converting BusinessPartner to JSON string, exception :", jsonProcessingException);
        }
    }

    public void publishMaterialMasterCreateEvents(final MaterialCreate materialCreate, final String verb) {
        try {
            String materialCreateEventJson = Obj.writeValueAsString(materialCreate);
            final OutboundMessage message = messageBuilder.build(materialCreateEventJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("verb", verb);
            params.put("materialClass", materialCreate.getMaterial().get(0).getMaterialClass());
            params.put("industrySector", materialCreate.getMaterial().get(0).getIndustrySector());
            params.put("materialType", materialCreate.getMaterial().get(0).getMaterialType());
            params.put("materialNumber", materialCreate.getMaterial().get(0).getMaterialNumber());
            params.put("maintenanceStatusGroup", materialCreate.getMaterial().get(0).getMaintenanceStatusGroup());
            params.put("maintenanceStatusMaterial", materialCreate.getMaterial().get(0).getMaintenanceStatusMaterial());
            params.put("deletionIndicator", materialCreate.getMaterial().get(0).getDeletionIndicator());
            String topicString = StringSubstitutor.replace(configProperties.getMaterialMasterTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published materialMasterCreateEvent event :{} on topic : {}", materialCreateEventJson, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Error encountered while converting MaterialCreate to JSON string, exception :", jsonProcessingException);
        }
    }

    public void publishMaterialMasterUpdateEvents(final MaterialUpdate materialUpdate, final String verb) {
        try {
            String materialUpdateEventJson = Obj.writeValueAsString(materialUpdate);
            final OutboundMessage message = messageBuilder.build(materialUpdateEventJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("verb", verb);
            params.put("materialClass", materialUpdate.getMaterial().get(0).getMaterialClass());
            params.put("industrySector", materialUpdate.getMaterial().get(0).getIndustrySector());
            params.put("materialType", materialUpdate.getMaterial().get(0).getMaterialType());
            params.put("materialNumber", materialUpdate.getMaterial().get(0).getMaterialNumber());
            params.put("maintenanceStatusGroup", materialUpdate.getMaterial().get(0).getMaintenanceStatusGroup());
            params.put("maintenanceStatusMaterial", materialUpdate.getMaterial().get(0).getMaintenanceStatusMaterial());
            params.put("deletionIndicator", materialUpdate.getMaterial().get(0).getDeletionIndicator());
            String topicString = StringSubstitutor.replace(configProperties.getMaterialMasterTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published materialMasterUpdateEvent event :{} on topic : {}", materialUpdateEventJson, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Error encountered while converting MaterialUpdate to JSON string, exception :", jsonProcessingException);
        }
    }

    public void publishChartOfAccountsEvents(final AccountHeader chartOfAccountEvent, final String verb) {
        try {
            String chartOfAccountEventJson = Obj.writeValueAsString(chartOfAccountEvent);
            final OutboundMessage message = messageBuilder.build(chartOfAccountEventJson);
            final Map<String, Object> params = new HashMap<>();
            params.put("verb", verb);
            chartOfAccountEvent.getAccountHeader().get(0).getChartOfAccounts();
            params.put("chartOfAccounts", chartOfAccountEvent.getAccountHeader().get(0).getChartOfAccounts());
            params.put("accountNumber", chartOfAccountEvent.getAccountHeader().get(0).getAccountNumber());
            String topicString = StringSubstitutor.replace(configProperties.getChartOfAccountsTopic(), params, "{", "}");
            publisher.publish(message, Topic.of(topicString));
            log.info("Published chartOfAccountEvent event :{} on topic : {}", chartOfAccountEventJson, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Error encountered while converting AccountHeaderType to JSON string, exception :", jsonProcessingException);
        }
    }

    public void publishNotificationEvents(final NotificationHeaderType notificationEvent, final String verb) {
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
            log.info("Published notificationEvent event :{} on topic : {}", notificationEventJson, topicString);
        } catch (final RuntimeException runtimeException) {
            log.error("Error encountered while publishing event, exception :", runtimeException);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Error encountered while converting NotificationHeaderType to JSON string, exception :", jsonProcessingException);
        }
    }

    @PreDestroy
    public void houseKeepingOnBeanDestroy() {
        log.info("The bean is getting destroyed, doing housekeeping activities");
        publisher.terminate(1000);
        messagingService.disconnect();
    }

}
