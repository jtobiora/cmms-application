package ng.upperlink.nibss.cmms.dto.account.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailRequest {
     private String  bankcode;
     private String  accno;
     private String  salt;
     private String  mac;
     private String  client_id;
     private String  enquiry_id;


}
