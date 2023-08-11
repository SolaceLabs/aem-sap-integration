package customer.cap_event_consumer.model.materialMaster;

import lombok.Data;

@Data
public class MaterialMasterUpdate {
    private MaterialUpdate[] material;
}