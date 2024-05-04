package ng.upperlink.nibss.cmms.dto.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ng.upperlink.nibss.cmms.dto.UserRequest;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class BankUserRequest  extends UserRequest {
    private String staffNumber;

//    @NotBlank(message = "User bank is required")
    private Long bankId;

    @JsonIgnore
    Bank bank;

    //    @JsonIgnore
    private String loginUrl;
}
