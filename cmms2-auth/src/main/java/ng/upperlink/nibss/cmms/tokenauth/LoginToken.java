package ng.upperlink.nibss.cmms.tokenauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginToken {
    private Long userId;
    private String token;
    private String requestId;
    private String sessionKey;
    private String message;

}
