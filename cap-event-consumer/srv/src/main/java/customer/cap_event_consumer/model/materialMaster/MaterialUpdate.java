package customer.cap_event_consumer.model.materialMaster;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MaterialUpdate {
    @JsonProperty("materialNumber")
    private String materialNumber;

    @JsonProperty("creationDate")
    private String creationDate;

    @JsonProperty("creatorUser")
    private String creatorUser;

    @JsonProperty("changerUser")
    private String changerUser;

    @JsonProperty("maintenanceStatusGroup")
    private String maintenanceStatusGroup;

    @JsonProperty("maintenanceStatusMaterial")
    private String maintenanceStatusMaterial;

    @JsonProperty("deletionIndicator")
    private String deletionIndicator;

    @JsonProperty("materialType")
    private String materialType;

    @JsonProperty("industrySector")
    private String industrySector;

    @JsonProperty("materialClass")
    private String materialClass;

    @JsonProperty("baseUnit")
    private String baseUnit;

    @JsonProperty("units")
    private Unit[] units;

    @JsonProperty("lastChange")
    private String lastChange;

    @Data
    public static class Unit {
        @JsonProperty("uOM")
        private String uOM;

        @JsonProperty("ean11")
        private String ean11;
    }
}
