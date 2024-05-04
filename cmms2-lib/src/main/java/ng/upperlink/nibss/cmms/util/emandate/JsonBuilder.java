package ng.upperlink.nibss.cmms.util.emandate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.NibssUser;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;

import java.io.IOException;

public class JsonBuilder {
    public JsonBuilder() {
    }

    public static String generateJson(Object object) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(object);
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.valueToTree(object);
        return s;
    }
    public static Object generateUserObj(String jsonObj, UserType userType) throws IOException, CMMSException {
        ObjectMapper objectMapper = new ObjectMapper();
        switch (userType)
        {
            case NIBSS:
                NibssUser nibssUser = objectMapper.readValue(jsonObj, NibssUser.class);

                nibssUser.setJsonData(null);
                nibssUser.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);
                return nibssUser;

            case BANK:
                BankUser bankUser = objectMapper.readValue(jsonObj, BankUser.class);
                return bankUser;

            case BILLER:
                BillerUser billerUser = objectMapper.readValue(jsonObj, BillerUser.class);
                return billerUser;

                default:
                {
                    throw new CMMSException("Update approval failed","500","500");
                }
        }
    }
    public static Object otherObj(String jsonObj, UserType userType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        switch (userType) {

            case BANK:
                return objectMapper.readValue(jsonObj, Bank.class);

            case BILLER:
                return objectMapper.readValue(jsonObj, Biller.class);
            default:
                return null;
        }
    }
}
