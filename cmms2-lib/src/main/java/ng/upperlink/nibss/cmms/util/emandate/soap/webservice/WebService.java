package ng.upperlink.nibss.cmms.util.emandate.soap.webservice;

import ng.upperlink.nibss.cmms.util.emandate.soap.util.RestTemplateUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public abstract class WebService {

    private ResponseEntity response;
//    private LogWebService logWebService = new LogWebService();
    private Boolean logging = false;
    private Boolean proxying = true;

    public abstract String getNarration();

    public abstract String getType();

    public Boolean isLogging(){
        return logging;
    }

    public Boolean isProxying(){
        return proxying;
    }

    protected void call(String url) {
        call(url, HttpMethod.GET, null, null);
    }

    protected void call(String url, HttpMethod httpMethod, Object body, HttpHeaders httpHeaders) {
        this.call(url, httpMethod, body, httpHeaders,isProxying());
    }

    protected void call(String url, HttpMethod httpMethod, Object body, HttpHeaders httpHeaders, boolean proxy) {

        logging = isLogging();
        proxying = proxy;

        if (logging) this.logRequest(url, httpMethod, body, httpHeaders);

        try {
            RestTemplateUtil restTemplateUtil = new RestTemplateUtil(url, httpMethod, body, httpHeaders, proxying);
            this.response = restTemplateUtil.exchange();
            if (logging) this.logResponse(this.response.getStatusCode());

        } catch (Exception e) {
//            if(e instanceof HttpClientErrorException){}
            logExceptionResponse(e);
        }
    }

    private void logExceptionResponse(Exception e) {
        e.printStackTrace();

        String error = e.getMessage();
        if (logging) {
            this.logResponse(HttpStatus.FAILED_DEPENDENCY, error);
        }
    }

    private void logRequest(String endpoint, HttpMethod httpMethod, Object body, Object headers) {

        HashMap<String, Object> request = new HashMap<>();
        request.put("header", headers);
        request.put("body", body);

        /*logWebService.setLogin("Anonymous");
        logWebService.setType(this.getType());
        logWebService.setNarration(this.getNarration());
        logWebService.setEndpoint(endpoint);
        logWebService.setHttpMethod(httpMethod.toString());
        logWebService.setRequest(request.toString());*/
    }

    private void logResponse(HttpStatus httpStatus) {

        HashMap<String, Object> response = new HashMap<>();
        response.put("header", this.response.getHeaders());
        response.put("body", this.response.getBody());

        logResponse(httpStatus,response.toString());
    }

    private void logResponse(HttpStatus httpStatus, String response) {

        /*logWebService.setStatusCode(httpStatus.toString());
        logWebService.setResponse(response);*/
    }

    public ResponseEntity getResponse() {
        return response;
    }
}
