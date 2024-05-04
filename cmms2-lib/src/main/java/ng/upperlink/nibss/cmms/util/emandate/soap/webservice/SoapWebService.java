package ng.upperlink.nibss.cmms.util.emandate.soap.webservice;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
public abstract class SoapWebService extends WebService {

    protected HttpHeaders getHeader(String action){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("Content-Type", ImmutableList.of("text/xml;charset=UTF-8"));
        httpHeaders.put("SOAPAction", ImmutableList.of(action));
        return  httpHeaders;
    }

}
