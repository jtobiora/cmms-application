package ng.upperlink.nibss.cmms.service.account;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.account.request.AccountDetailRequest;
import ng.upperlink.nibss.cmms.dto.account.request.AccountRequest;
import ng.upperlink.nibss.cmms.dto.account.response.AccountDetailsResponse;
import ng.upperlink.nibss.cmms.dto.account.response.AccountResponse;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Random;

@Service
@Slf4j
public class AccountValidationService {

    static final char[] CHARSET_AZ_09 = "0123456789".toCharArray();
    private static final  String URI ="http://css.ng/v1prod/name_enquiry_rest";

    public static String randomString(char[] characterSet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }
    private static AccountResponse call(AccountDetailRequest requestt) throws CMMSException {
        ResponseEntity<AccountDetailsResponse> res =null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            log.info("Account detail Request Payload "+requestt);
            res = restTemplate.postForEntity(URI, requestt, AccountDetailsResponse.class);

            log.info("Response from the server "+res);
        }catch (Exception ex){
            log.error("Error track  ---{} ",ex);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500", "500");
        }
        if (res ==null){
                throw new CMMSException(Errors.DATA_IS_NULL.getValue().replace("{}","Account details"),"500","500" );
        }
        AccountDetailsResponse response  = res.getBody();

        log.info("Response body from server"+response);
        AccountResponse accountResponse = new AccountResponse();
        if (response.getStatus().equalsIgnoreCase("00"))
        {
            accountResponse.setAccountName(response.getSurname()+" "+response.getOthernames());

            return accountResponse;
        }else
        {
            throw new CMMSException("Account number is not valid","404","404");
        }

    }
    public static AccountDetailRequest setUpAccountRequest(AccountRequest request) {
        log.info("Account request: "+request);
        final String secreteKey = "utjjT5dX7TG37D9TwTAE8NccamqwNZWNJaVzPt4b";
        final String clientId = "WSABNSLCNC";
        String enqId = randomString(CHARSET_AZ_09,8);
        String salt = randomString(CHARSET_AZ_09, 20);
        String mac = EncyptionUtil.doSHA512Encryption(clientId+"-"+secreteKey+"-"+salt);
        AccountDetailRequest accountLookUpRequest = new AccountDetailRequest(request.getBankCode(),request.getAccountNumber(),salt,mac,clientId,enqId);

        return accountLookUpRequest;
    }
    public static AccountResponse request(AccountRequest request) throws CMMSException {

       return call(setUpAccountRequest(request));// TODO This is upperlink Account Name inquiry service
//        return ICADService.setREsponse(request);//TODO this is NIBSS ICAD service for  Account Name inquiry
    }

    public static void  main(String[] agrs) throws CMMSException {
        AccountRequest req = new AccountRequest("058","0231116887");
        AccountResponse response =request(req);
        System.out.println("Account name: "+response.getAccountName());
    }
}
