package ng.upperlink.nibss.cmms.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
public class CMMSExceptionResponse {

    private String responseCode;
    private String errorMessage;
    private String mandateCode ;
}