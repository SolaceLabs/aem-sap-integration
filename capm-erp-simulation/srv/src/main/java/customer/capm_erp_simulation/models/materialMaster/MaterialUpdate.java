package customer.capm_erp_simulation.models.materialMaster;

import lombok.Data;

import java.util.List;

@Data
public class MaterialUpdate {
    private List<MaterialUpdateType> material;
}
