package ng.upperlink.nibss.cmms.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersSearchRequest {
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String role;
    private String city;
    private String staffNumber;
    private String activated;
    private String createdAt;


}
