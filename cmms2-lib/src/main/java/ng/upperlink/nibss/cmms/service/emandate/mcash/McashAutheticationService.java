package ng.upperlink.nibss.cmms.service.emandate.mcash;

import ng.upperlink.nibss.cmms.dto.emandates.AuthenticationRequest;
import ng.upperlink.nibss.cmms.dto.emandates.EmandateRequest;
import ng.upperlink.nibss.cmms.dto.emandates.response.AuthenticationResponse;
import ng.upperlink.nibss.cmms.dto.emandates.response.EmandateResponse;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.emandate.McashResponseCode;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.service.emandate.BillerEmandateService;
import ng.upperlink.nibss.cmms.service.emandate.EmandateBaseService;
import ng.upperlink.nibss.cmms.util.emandate.soap.bank.XMLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
//import ng.upperlink.nibss.cmms.exceptions.emandate.EmandateException;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;

@Service
public class McashAutheticationService {
    private static Logger logger = LoggerFactory.getLogger(BillerEmandateService.class);


    @Value("${nibss-product-code}")
    private String nibssProductCode;

    @Value("${mcash.url}")
    private String mcashUrl;

    @Value("${mcash.testCode}")
    private String mcashTestCode;

    public EmandateResponse setupMcashRequest(Mandate mandate, EmandateRequest emandateRequest,String channel) {
        AuthenticationRequest authenticationRequest =null;
        if(mandate !=null)
            authenticationRequest= generateAuthRequest(mandate,emandateRequest,channel);
        if (authenticationRequest ==null )
        {
            logger.info(EmandateResponseCode.MCASH_REQUEST_NOT_GENERATED.getValue());
        }


        AuthenticationResponse mRes = call(authenticationRequest);
        if (mRes == null)
        {
            return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.MCASH_AUTHENTICATION_FAILED,null,"");
        }
        McashResponseCode responseCode = McashResponseCode.getResponseByCode(mRes.getResponseCode());
        return EmandateBaseService.generateMcashResponse(responseCode,mRes.getMandateReferenceNumber()); //TODO make a REST API call to mcash
    }

    public AuthenticationRequest generateAuthRequest(Mandate mandate, EmandateRequest emandateRequest,String channel)
    {
        String bankCode = mandate.getBank().getCode();
        String testCode = mcashTestCode + bankCode;
        AuthenticationRequest mcahRequest = new AuthenticationRequest();
        mcahRequest.setSessionId(generateSessionId(mandate));
        mcahRequest.setRequestorID(testCode);
        mcahRequest.setPayerPhoneNumber(mandate.getPhoneNumber());
        mcahRequest.setAmount(mandate.getAmount());
        mcahRequest.setAdditionalFIRequiredData(mandate.getBank().getName());
        mcahRequest.setFIInstitution(testCode);
        mcahRequest.setAccountNumber(mandate.getAccountNumber());
        mcahRequest.setAccountName(mandate.getAccountName());
        mcahRequest.setPassCode(emandateRequest.getSubscriberPassCode());
        mcahRequest.setMandateReferenceNumber(mandate.getMandateCode());
        mcahRequest.setProductCode(channel);
        return mcahRequest;
    }
    private String generateSessionId(Mandate mandate)
    {
        SimpleDateFormat f = new SimpleDateFormat("YYMMDDHHmmss");
        String createdAt = null;
        try{
            createdAt = f.format(new Date().getDate());
        }catch (Exception ex){
            logger.error("Format date parse error {}",ex);
           return null;//EmandateException(EmandateResponseCode.PARSE_DATE_ERROR.getValue(),
                    //Response.Status.INTERNAL_SERVER_ERROR,
                    //EmandateResponseCode.PARSE_DATE_ERROR.getCode());
        }
        String code = mcashTestCode+mandate.getBank().getCode();
        String mandateCode = mandate.getMandateCode().replace("/","");
        if (code==null)
        {
            logger.error("Could not retrieve subscriber's bank nip code");
            return null;
        }
        if (mandateCode==null)
        {
            logger.error("Mandat code is null");
            return null;
        }
        logger.info("Mcash Session generated at "+createdAt);
        logger.info("The sessionId "+code+createdAt+mandateCode);
        String sId = (code + createdAt + mandateCode).substring(0, 30);
        return sId;
    }
    private AuthenticationResponse call(AuthenticationRequest requestt) {
//        String xmlRequest = XMLBuilder.mcashAuthRequetXML(requestt);
        String xmlRequest = XMLBuilder.marshal(requestt, AuthenticationRequest.class);
        logger.info("Mcash request xml:=================== "+xmlRequest);
        try
        {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_XML);
            List<org.springframework.http.MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(org.springframework.http.MediaType.APPLICATION_XML);
            mediaTypes.add(org.springframework.http.MediaType.APPLICATION_JSON);
            httpHeaders.setAccept(mediaTypes);

            logger.info("Http hearder: "+httpHeaders);
            logger.info("Media types: "+mediaTypes);
            HttpEntity<String> httpEntity = new HttpEntity<>(xmlRequest, httpHeaders);
            //AuthenticationResponse response = new AuthenticationResponse();
//            response.setResponseCode("00");
            logger.info("HttpEntity making mcash call : "+httpEntity);
            ResponseEntity<String> res = restTemplate.exchange(mcashUrl, HttpMethod.POST,httpEntity,String.class);
            AuthenticationResponse response = XMLBuilder.unmarshalWithoutDecrypting(res.getBody(), AuthenticationResponse.class);
            logger.info("Mcash response:============ "+res);
            if (res ==null){
                logger.info("Mcahs response is null");
                return null;
            }
            logger.info("Mcash response body: "+response);
            return response;
//            return response;
        }catch (Exception ex){
            logger.error(ex.toString());
            logger.error(EmandateResponseCode.MCASH_AUTHENTICATION_FAILED.getCode(), Response.Status.INTERNAL_SERVER_ERROR,
                    "Mcash authentication failed: ");
            return null;
        }
    }
//    private AuthenticationResponse call(AuthenticationRequest requestt)
//    {
//        try
//        {
//            Client client = ClientBuilder.newClient();
//            WebTarget webTarget = client.target(mcashUrl);
//            Response postResponse = webTarget
//                    .request(MediaType.APPLICATION_XML)
//                    .post(Entity.xml(requestt));
//            AuthenticationResponse mcashResponse = postResponse.readEntity(AuthenticationResponse.class);
//            if (mcashResponse ==null){
//                throw new EmandateException(EmandateResponseCode.MCASH_NO_RESPONSE.getValue(),
//                        Response.Status.INTERNAL_SERVER_ERROR,EmandateResponseCode.MCASH_NO_RESPONSE.getCode());
//            }
//            return mcashResponse;
//        }catch (Exception ex){
//            throw new EmandateException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR,
//                    EmandateResponseCode.MCASH_AUTHENTICATION_FAILED.getCode());
//        }
//    }
}
