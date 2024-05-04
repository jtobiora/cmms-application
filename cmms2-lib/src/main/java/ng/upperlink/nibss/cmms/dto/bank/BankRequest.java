package ng.upperlink.nibss.cmms.dto.bank;

import lombok.Data;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.MakerCheckerType;
import ng.upperlink.nibss.cmms.errorHandler.EnumConstraint;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Data
public class BankRequest{
    private Long id;

    @NotBlank (message = "Please provide bank name")
    private String name;

    @NotBlank(message = "Invalid bank code, can not be blank")
    private String code;

    @NotBlank(message = "Nip code cannot be null")
    private String nipBankCode;

    private String username;

//    @NotNull(message = "Domain name is require")
    private String domainName;

    private String notificationUrl;
}
