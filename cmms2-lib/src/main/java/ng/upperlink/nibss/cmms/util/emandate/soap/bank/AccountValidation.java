package ng.upperlink.nibss.cmms.util.emandate.soap.bank;

import ng.upperlink.nibss.cmms.util.emandate.soap.builder.SoapBuilder;
import ng.upperlink.nibss.cmms.util.emandate.soap.builder.SoapPayload;
import ng.upperlink.nibss.cmms.util.emandate.soap.transformer.soap.AccountValidationTransformer;
import ng.upperlink.nibss.cmms.util.emandate.soap.webservice.bank.AccountValidationWebService;
import ng.upperlink.nibss.cmms.util.emandate.soap.xml.accountvalidation.GetAccountDetailsResponse;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountValidation  {

    private static final String ENDPOINT = "";

    private String accountNumber;
    private String countryId;
    protected String reason;
    private GetAccountDetailsResponse accountDetailsResponse;

    public AccountValidation(String accountNumber, String reason){
        this.accountNumber = accountNumber;
        this.reason = reason;
        this.countryId = "09";
        this.lookUp();
    }

    private void lookUp(){
        try {
            String narration = "Lookup account number: " + accountNumber;

            String soapAction = "http://tempuri.org/IService1/GetAccountDetails";

            Map<String, String> namespace = new HashMap<String, String>() {{
                put("tem", "http://tempuri.org/");
                put("bap", "http://schemas.datacontract.org/2004/07/BAPMandateCreationService.BO");
            }};

            SoapBuilder soapBuilder = new SoapBuilder("soapenv", namespace);

            soapBuilder.set("soapenv__Header", new SoapPayload());
            soapBuilder.set("soapenv__Body", new SoapPayload());
            soapBuilder.get("soapenv__Body").set("tem__GetAccountDetails", new SoapPayload());
            soapBuilder.get("soapenv__Body").get("tem__GetAccountDetails").set("tem__request", new SoapPayload());
            soapBuilder.get("soapenv__Body").get("tem__GetAccountDetails").get("tem__request").set("bap__AccountNumber", new SoapPayload(accountNumber));
            soapBuilder.get("soapenv__Body").get("tem__GetAccountDetails").get("tem__request").set("bap__CountryId", new SoapPayload(countryId));

            AccountValidationWebService accountValidationWebService = new AccountValidationWebService(ENDPOINT, soapAction, narration, soapBuilder.getXml());

            ResponseEntity response = accountValidationWebService.getResponse();
            String xml = Objects.requireNonNull(response.getBody()).toString();


            // Casting the XML string to a Class
            AccountValidationTransformer<GetAccountDetailsResponse> accountValidationTransformer = new AccountValidationTransformer<>();
            accountDetailsResponse = accountValidationTransformer.transform(xml, GetAccountDetailsResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}