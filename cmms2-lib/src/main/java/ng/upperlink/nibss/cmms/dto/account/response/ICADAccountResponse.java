package ng.upperlink.nibss.cmms.dto.account.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ICADAccountResponse
{
        private String status;
        private String accountName;
        private String accountNumber;
        private String otherNames;
        private String surname;
        private String bankCode;
}

