package ng.upperlink.nibss.cmms.dto.biller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.model.biller.Biller;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BillerLoginDetails extends UserDetail {
    private Biller biller;

    public BillerLoginDetails (UserDetail userDetail){
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
