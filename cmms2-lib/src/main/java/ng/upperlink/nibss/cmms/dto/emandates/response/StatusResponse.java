package ng.upperlink.nibss.cmms.dto.emandates.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusResponse {
    String mandateCode;
    String status;
    String statusCode;
}
