package ng.upperlink.nibss.cmms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;


@Data
//@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private Long userId;

    private Name name;
    private String phoneNumber;

    @NotBlank(message = "Email address is required")
    @Email(message = "invalid emailAddress address")
    private String emailAddress;

    private String houseNumber;

    private String streetName;

    @NotNull(message = "User role is required")
    private Long roleId;

    private String city;

    private Long lgaId;
    private Long stateId;
    private Long countryId;

    public UserRequest(Long userId, String phoneNumber, String emailAddress,Long roleId) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.roleId = roleId;
    }
    public UserRequest(Long userId, Name name, String phoneNumber, String emailAddress, String houseNumber, String streetName, Long roleId, String city, Long lgaId) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.houseNumber = houseNumber;
        this.streetName = streetName;
        this.roleId = roleId;
        this.city = city;
        this.lgaId = lgaId;
    }

}
