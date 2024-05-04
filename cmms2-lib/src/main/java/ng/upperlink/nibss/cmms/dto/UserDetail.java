package ng.upperlink.nibss.cmms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDetail { 
 
    private Long userId;
    private String userType;
    private String roleType;
    private String userAuthorizationType;
    private String emailAddress;
    private String sessionId; 
    private Collection<String> roles;
    private Collection<String> privileges;

} 