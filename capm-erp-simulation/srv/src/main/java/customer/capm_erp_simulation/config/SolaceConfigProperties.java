package customer.capm_erp_simulation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "solace")
@Getter
@Setter
public class SolaceConfigProperties {
    private String hostUrl;
    private String vpnName;
    private String userName;
    private String password;
    private String reconnectionAttempts;
    private String connectionRetriesPerHost;
    private String salesOrderTopic;
    private String notificationTopic;
    private String materialMasterTopic;
    private String chartOfAccountsTopic;
    private String businessPartnerTopic;
}
