package ng.upperlink.nibss.cmms.util.emandate.soap.webservice.bank;

import ng.upperlink.nibss.cmms.util.emandate.soap.webservice.WebService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class SmsLoggerWebService extends WebService {

    protected Boolean proxy = false;
    private String narration;

    public SmsLoggerWebService(String url, String narration, Object body, HttpHeaders httpHeaders) {

        this.setNarration(narration);
        call(url, HttpMethod.POST, body, httpHeaders);
    }

    @Override
    public String getNarration() {
        return this.narration;
    }

    @Override
    public String getType() {
        return "SMS Webservices";
    }

    private void setNarration(String narration) {
        this.narration = narration;
    }
}
