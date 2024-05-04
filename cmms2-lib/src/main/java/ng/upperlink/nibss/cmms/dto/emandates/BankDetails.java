package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.Data;

@Data
public class BankDetails {
    String code;
    String nipCode;
    String apiKey;
    String secretKey;
    String domainName;
    String base64Code;
    String sha512String;
}
