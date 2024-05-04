package ng.upperlink.nibss.cmms.tokenauth;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.dashboard.MandateView;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenBuilder {
    private Long userId;

    private String requestId;

    private String softToken;

    private String sessionKey;

}
