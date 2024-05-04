package ng.upperlink.nibss.cmms.dto.pssp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.model.bank.Bank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PsspLoginDetails extends UserDetail{
    private Bank bank;

    public PsspLoginDetails (UserDetail userDetail){
        this.setUserId(userDetail.getUserId());
        this.setRoleType(userDetail.getRoleType());
        this.setEmailAddress(userDetail.getEmailAddress());
        this.setPrivileges(userDetail.getPrivileges());
        this.setRoles(userDetail.getRoles());
        this.setSessionId(userDetail.getSessionId());
        this.setUserAuthorizationType(userDetail.getUserAuthorizationType());
        this.setUserType(userDetail.getUserType());
    }
}
