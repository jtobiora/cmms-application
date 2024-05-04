package ng.upperlink.nibss.cmms.dto.biller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.model.biller.Industry;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequest {
       //company details

//    @Digits(integer=11, fraction=0, message="{company.id.digits}")
    private Long id;

    @NotEmpty(message = "Company Name cannot be empty")
    private String name;
    private String description;
    @NotNull(message = "Please select industry")
    private Long industryId;
//    @JsonIgnore
//    private Industry industry;
}
