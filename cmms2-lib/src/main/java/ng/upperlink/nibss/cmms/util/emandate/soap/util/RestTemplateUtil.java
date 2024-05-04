package ng.upperlink.nibss.cmms.util.emandate.soap.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestTemplateUtil {
    private String url;
    private HttpMethod httpMethod;
    private Object body;
    private HttpHeaders httpHeaders;
    private Boolean proxy = false;

    public RestTemplateUtil(String url) {
        this(url, HttpMethod.GET, null, null, false);
    }

    public RestTemplateUtil(String url, HttpMethod httpMethod, Object body, HttpHeaders httpHeaders, Boolean proxy) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.body = body;
        this.httpHeaders = httpHeaders;
        this.proxy = proxy;
    }

    public ResponseEntity exchange() {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Object> httpEntity = new HttpEntity<>(body, httpHeaders);

        return restTemplate.exchange(url, httpMethod, httpEntity, String.class);
    }
}
