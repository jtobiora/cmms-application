package ng.upperlink.nibss.cmms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResponseCode {

    @JsonProperty("ResponseCode")
    private String responseCode;

}
