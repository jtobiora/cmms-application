package ng.upperlink.nibss.cmms.service.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.dto.account.request.AccountRequest;
import ng.upperlink.nibss.cmms.dto.account.response.AccountResponse;
import ng.upperlink.nibss.cmms.dto.account.response.ICADAccountResponse;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.util.RestTemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ICADService {

    private static Logger logger = LoggerFactory.getLogger(ICADService.class);
//    @Value("${icad.url}")
    private static String icadUrl ="https://196.6.103.10:5035/icad/accounts";
//    @Value("${icad.apiKey}")
    private static String apiKey ="faae9b4a-99e1-42f0-ab3b-679ccb6c1bc7";

    public static String call(AccountRequest request) throws CMMSException {
        try {
            List<AccountRequest> accountRequests = new ArrayList<>();
            accountRequests.add(request);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(org.springframework.http.MediaType.APPLICATION_JSON);
            mediaTypes.add(org.springframework.http.MediaType.APPLICATION_XML);
            httpHeaders.setAccept(mediaTypes);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("apiKey", apiKey);


            HttpEntity<Object> httpEntity = new HttpEntity<>(accountRequests, httpHeaders);
            logger.info("HttpEntity making ICAD call : " + httpEntity);
            ResponseEntity<String> res = restTemplate.exchange(icadUrl, HttpMethod.POST, httpEntity, String.class);
            logger.info("The ICAD response "+res);
            return res.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception trace ", e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(), "500", "500");
        }
    }
    public static AccountResponse setREsponse(AccountRequest request) throws CMMSException{

        List<ICADAccountResponse> icadAccountResponses = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            createSecuredManager();
            String call = call(request);

            objectMapper.readValue(call, List.class).forEach(o -> {
                ICADAccountResponse icad = objectMapper.convertValue(o, ICADAccountResponse.class);
                icadAccountResponses.add(icad);
            });
        } catch (Exception e) {
            logger.error("IOException Trace {} ",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
        }

        ICADAccountResponse response = !icadAccountResponses.isEmpty() ? icadAccountResponses.stream().findAny().get() : null;
        AccountResponse accountResponse = new AccountResponse();
        if (response.getStatus().equalsIgnoreCase("00"))
        {
//            String accountName =response.getSurname()+" "+response.getOtherNames();
            String accountName = response.getAccountName();
            accountResponse.setAccountName(accountName);

            logger.info("ICAD returned account name : "+accountResponse);
            return accountResponse;
        }else
        {
            throw new CMMSException("Account number is not valid","404","404");
        }
    }
    public static void main(String[] args) {
        AccountRequest request = new AccountRequest("023","0020259008");
        ICADService icadService  = new ICADService();
        try {
            setREsponse(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createSecuredManager() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}
