package customer.capm_erp_simulation.models.config;

import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class SolaceConnectionParameters {

    @NotEmpty(message = "Host URL cannot be empty")
    private String hostUrl;
    @NotEmpty(message = "VPN name cannot be empty")
    private String vpnName;
    @NotEmpty(message = "Username cannot be empty")
    private String userName;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}
