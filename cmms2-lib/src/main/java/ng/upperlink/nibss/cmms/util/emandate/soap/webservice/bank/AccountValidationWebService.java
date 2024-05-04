package ng.upperlink.nibss.cmms.util.emandate.soap.webservice.bank;

import ng.upperlink.nibss.cmms.util.emandate.soap.webservice.SoapWebService;
import org.springframework.http.HttpMethod;

public class AccountValidationWebService extends SoapWebService {

    protected Boolean proxy = false;
    private String narration;

    public AccountValidationWebService(String url, String action, String narration, Object body) {
        this.narration = narration;
        call(url, HttpMethod.POST, body, getHeader(action));
    }


    @Override
    public String getNarration() {
        return this.narration;
    }

    @Override
    public String getType() {
        return "Account validation Webservices";
    }
}
