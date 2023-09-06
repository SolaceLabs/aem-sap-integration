package customer.capm_erp_simulation.models.config;

import lombok.Data;

@Data
public class ErpSimulatorSchedules {
    private int salesOrderCreateDuration;
    private int salesOrderChangeDuration;
    private int businessPartnerCreateDuration;
    private int businessPartnerChangeDuration;
    private int materialMasterCreateDuration;
    private int materialMasterChangeDuration;
    private int chartOfAccountsCreateDuration;
    private int chartOfAccountsChangeDuration;
    private int notificationCreateDuration;
    private int notificationChangeDuration;
}
