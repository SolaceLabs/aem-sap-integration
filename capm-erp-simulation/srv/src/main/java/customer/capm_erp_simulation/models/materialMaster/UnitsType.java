package customer.capm_erp_simulation.models.materialMaster;

import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@ToString
@Builder
public class UnitsType {

    private String uOM;
    private Date ean11;


}
