package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.Data;

@Data
public class BillerDetails {
    String accountNumber;
    String rcNumber;
    String apiKey;
    String billerPassKey;
    String domainName;
    String sessionId;
}
