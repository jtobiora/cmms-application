package ng.upperlink.nibss.cmms.dto.account.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsResponse {
    @JsonProperty
    private String enquiry_id;
    private String client_id;
    private String status;
    private String bankcode;
    private String surname;
    private String othernames;
    private String accno;
}