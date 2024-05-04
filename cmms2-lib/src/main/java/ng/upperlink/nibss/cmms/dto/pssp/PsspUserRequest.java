package ng.upperlink.nibss.cmms.dto.pssp;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ng.upperlink.nibss.cmms.dto.UserRequest;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.pssp.Pssp;

@Data
public class PsspUserRequest  extends UserRequest {
    private String staffNumber;
    private Long psspId;
    @JsonIgnore
    Pssp pssp;
}
