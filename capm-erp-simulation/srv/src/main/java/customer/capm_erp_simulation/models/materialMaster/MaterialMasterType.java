package customer.capm_erp_simulation.models.materialMaster;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@Builder
public class MaterialMasterType {
    private String materialNumber;
    private Date creationDate;
    private String creatorUser;
    private String changerUser;
    private String maintenanceStatusGroup;
    private String maintenanceStatusMaterial;
    private String deletionIndicator;
    private String materialType;
    private String industrySector;
    private String materialClass;
    private String baseUnit;
    private List<UnitsType> units;

}
