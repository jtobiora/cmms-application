package ng.upperlink.nibss.cmms.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserStatus{

    private Status nibssUserStatus;
    private Status bankUserStatus;
    private Status billerUserStatus;
    private Status psspUserStatus;


    public UserStatus(Status nibssUserStatus,Status bankUserStatus,Status billerUserStatus,Status psspUserStatus){
        this.nibssUserStatus = nibssUserStatus;
        this.bankUserStatus = bankUserStatus;
        this.billerUserStatus = billerUserStatus;
        this.psspUserStatus = psspUserStatus;
    }

}