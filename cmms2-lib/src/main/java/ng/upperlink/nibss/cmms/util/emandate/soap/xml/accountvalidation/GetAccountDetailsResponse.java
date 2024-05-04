package ng.upperlink.nibss.cmms.util.emandate.soap.xml.accountvalidation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nero on 13/12/2018.
 *
 */
@Data
public class GetAccountDetailsResponse {
    private AccountDetailsResult AccountDetailsResult;

    public Boolean isSuccessful(){
        return AccountDetailsResult != null && AccountDetailsResult.getStatus().equalsIgnoreCase("00");
    }

    @Data
    public class AccountDetailsResult {
        private String EnquiryId;
        private String ClientId;
        private String Status;
        private String BankCode;
        private String Surname;
        private String OtherNames;
        private String AccountNumber;
    }
}
