package ng.upperlink.nibss.cmms.dto;


import ng.upperlink.nibss.cmms.embeddables.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {

    private Name name;

    private String bvn;

    private Set<String> phoneList = new HashSet<>();

    private String emailAddress;

    private String username;

    private boolean change_password = false;

    private boolean activated = true;

    private String userType;

    private Image facialImage;

    private Image fingerprintImage;

    private String agentCode;

    private String enrollerCode;

    public UserResponse(Name name, String bvn, Set<String> phoneList, String emailAddress, String username, boolean change_password, boolean activated, String userType) {
        this.name = name;
        this.bvn = bvn;
        this.phoneList = phoneList;
        this.emailAddress = emailAddress;
        this.username = username;
        this.change_password = change_password;
        this.activated = activated;
        this.userType = userType;
    }
}

