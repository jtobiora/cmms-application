package ng.upperlink.nibss.cmms.util.emandate.soap.bank;

import ng.upperlink.nibss.cmms.util.emandate.soap.webservice.SoapWebService;
import org.springframework.http.HttpMethod;

public class NameEnquiryWebService extends SoapWebService {

    protected Boolean proxy = false;
    private String narration;

    public NameEnquiryWebService(String url, String action, Object body) {
        call(url, HttpMethod.POST, body, getHeader(action), this.proxy);
    }


    @Override
    public String getNarration() {
        return this.narration;
    }

    @Override
    public String getType() {
        return "NameEnquiry Webservices";
    }
}
