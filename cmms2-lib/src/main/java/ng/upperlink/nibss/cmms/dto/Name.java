package ng.upperlink.nibss.cmms.dto;

import ng.upperlink.nibss.cmms.enums.Title;
import ng.upperlink.nibss.cmms.errorHandler.EnumConstraint;
import ng.upperlink.nibss.cmms.errorHandler.NotNumberConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Name {

//    @NotBlank(message = "Invalid title, title is required")
//    @EnumConstraint(enumClass = Title.class, message = "Invalid title. it should be either DR, MR, MRS, MS, MISS, PROF or CHIEF")
//    private String title;

    @NotNumberConstraint(message = "Invalid first name, name cannot contain digits")
    private String firstName;

    private String middleName;

    @NotNumberConstraint(message = "Invalid last name, name cannot contain digits")
    private String lastName;

}
