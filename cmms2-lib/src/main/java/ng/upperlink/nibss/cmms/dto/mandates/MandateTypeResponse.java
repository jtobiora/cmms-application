package ng.upperlink.nibss.cmms.dto.mandates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MandateTypeResponse {
    private int id;
    private String mandateType;
}
