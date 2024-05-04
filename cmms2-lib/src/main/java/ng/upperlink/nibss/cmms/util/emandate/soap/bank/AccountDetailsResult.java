package ng.upperlink.nibss.cmms.util.emandate.soap.bank;

import lombok.Data;

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