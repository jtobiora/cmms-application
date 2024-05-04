package ng.upperlink.nibss.cmms.util.emandate.soap.bank;

import lombok.Data;

@Data
public class Response {
    private String otherNames;
    private String bankCode;
    private String status;
    private String surname;
    private String accountNumber;
}
