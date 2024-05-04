package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccessToken {
    String hash;// sha512(nipCode + apiKey + bankCode)
    String sessionId;
    String billerPassKey;
}
