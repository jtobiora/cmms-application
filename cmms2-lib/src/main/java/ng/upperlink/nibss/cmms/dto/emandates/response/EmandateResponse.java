package ng.upperlink.nibss.cmms.dto.emandates.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmandateResponse {

    String responseCode;
    String mandateCode;
    String responseDescription;
}
