package ng.upperlink.nibss.cmms.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nibss.nip.util.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by stanlee on 16/03/2018.
 */
@Slf4j
public final class CommonUtils {

    private static Logger LOG = LoggerFactory.getLogger(CommonUtils.class);
    
    private CommonUtils() {} // prevent this class from being initialized
    /**
     * returning gson object with setTimezone and dateformat
     * @return
     */
    public static Gson getGson(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Gson gson = new GsonBuilder().setDateFormat(dateFormat.toPattern()).create();
        return gson;
    }

    public static Map<String, Object> jsonMap(String jsonString){

        Map<String, Object> mapResponse = new Gson().fromJson(
                jsonString, new TypeToken<HashMap<String, Object>>() {}.getType()
        );

        return mapResponse;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static Date subtractDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,Math.multiplyExact(-1, days)); //minus number would decrement the days
        return cal.getTime();
    }

    public static Date startOfDay(Date from) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND, 0);

       return calendar.getTime();
    }

    public static Date endOfDay(Date to) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(to);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return cal.getTime();
    }

    public static String generateCode(List<String> generatedCodes, List<String> allCodes, UserType userType, boolean isInstitution){

        switch (userType){

            case NIBSS:
                return getUniqueCode(generatedCodes, allCodes, userType, isInstitution);

            case BANK:
                return getUniqueCode(generatedCodes, allCodes, userType, isInstitution);

            case PSSP:
                return getUniqueCode(generatedCodes, allCodes, userType, isInstitution);

            case BILLER:
                return getUniqueCode(generatedCodes, allCodes, userType, isInstitution);

            case SUBSCRIBER:
                return getUniqueCode(generatedCodes, allCodes, userType, isInstitution);
        }

        return getUniqueCode(generatedCodes, allCodes, userType, isInstitution);
    }

    private static String getUniqueCode(List<String> generatedCodes, List<String> allCodes, UserType userType, boolean isInstitution){

        if (generatedCodes == null){
            generatedCodes = new ArrayList<>();
        }

        if (allCodes == null){
            allCodes = new ArrayList<>();
        }

        String maxCodeGenerated = getMaxCodeGenerated(generatedCodes, userType, isInstitution);
        int counter = 0;
        while (allCodes.contains(maxCodeGenerated) && counter < 4){
            if (generatedCodes.add(maxCodeGenerated)) {
                maxCodeGenerated = getMaxCodeGenerated(generatedCodes, userType, isInstitution);
            }else {
                break;
            }
            ++counter;
        }

        if (allCodes.contains(maxCodeGenerated))
            return Constants.EMPTY;

        return maxCodeGenerated;
    }

    private static String getMaxCodeGenerated(List<String> codes, UserType userType, boolean isInstitution){

        Integer maxValue = 0;

        for (String code : codes) {

//            code = code.replaceFirst(initialCode, "");

            if (code == null || code.isEmpty()){code = "0";}
            maxValue = Math.max(maxValue, Integer.valueOf(code));
        }

        String format = "";

        switch (userType){

            case NIBSS:
                format = String.format("%06d", ++maxValue);
                return format;

            case BANK:
                if (isInstitution){
                    return String.format("%06d", ++maxValue);
                }
                format = String.format("%05d", ++maxValue);
                return format;

            case PSSP:
                format = String.format("%08d", ++maxValue);
                return format;

            case BILLER:
                format = String.format("%08d", ++maxValue);
                return format;
            case SUBSCRIBER:
                format = String.format("%08d", ++maxValue);
                return format;

        }

        format = String.format("%05d", ++maxValue);
        return format;
    }



    /*
 * Converts XMLGregorianCalendar to java.util.Date in Java
 */
    public static Date toDate(XMLGregorianCalendar calendar){
        if(calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }

    public String generateCode(List<String> codes, UserType userType, boolean isInstitution){



        Integer maxValue = 0;

        for (String code : codes) {

//            code = code.replaceFirst(initialCode, "");

            if (code == null || code.isEmpty()){code = "0";}
            maxValue = Math.max(maxValue, Integer.valueOf(code));
        }

        String format = "";

        switch (userType){

            case NIBSS:
                format = String.format("%06d", ++maxValue);
                return format;

            case BANK:
                if (isInstitution){
                    return String.format("%06d", ++maxValue);
                }
                format = String.format("%05d", ++maxValue);
                return format;

            case PSSP:
                format = String.format("%08d", ++maxValue);
                return format;

            case BILLER:
                format = String.format("%08d", ++maxValue);
                return format;
            case SUBSCRIBER:
                            format = String.format("%08d", ++maxValue);
                            return format;

        }

        format = String.format("%05d", ++maxValue);
        return format;
    }
    
    public static Date getPreviousDays(int days) {
        if (days == 0)
            throw new IllegalArgumentException("Invalid number of days provided");
        LocalDate today = LocalDate.now();
        LocalDate daysAgo = today.minusDays(days);
        return Date.from(daysAgo.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public static <T> T convertStringToObject(String data, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            T obj = mapper.readValue(data, clazz);
            return obj;
        } catch (IOException e) {
            log.error("Unable to parse json string {}", data, e);
        }
        return null;
    }
    
    public static String convertObjectToXml(Object object) {
        try {
            JAXBContext jAXBContext = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jAXBContext.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(object, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (JAXBException e) {
            log.error("An exception occurred while trying to convert object to xml", e);
        }
        return null;
    }
    
    public static <T> String convertObjectToJson(T object) {
        try {
            ObjectMapper objMapper = new ObjectMapper();
            return objMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Unable to convert object to a string", e);
        }
        return null;
    }
    
    private static String generateSessionID() {
        return new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + new RandomGenerator().getRandomLong(99999999999D, 999999999999D);
    }
    
    public static String getSessionId(String bankCode, String destinationInstitutionCode) {
        return new StringBuilder(bankCode).append(destinationInstitutionCode).append(generateSessionID()).toString();
    }
    
    /**
     * Convert date to string
     * @param date
     * @param format
     * @return 
     */
    public static String convertDateToString(Date date, String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
    }
    
    /**
     * Calculate the next debit date
     * @param currentNextDebitDate
     * @param mandateFrequency
     * @return 
     */
    public static Date nextDebitDate(Date currentNextDebitDate, int mandateFrequency) {
        int month = mandateFrequency % 4;
        LocalDate theNextMonth = null;
        LocalDate nextDebitDateLocalDate = currentNextDebitDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (mandateFrequency == 0)
            throw new IllegalArgumentException("Invalid mandate frequency provided");
        if (month == 0)
            theNextMonth = nextDebitDateLocalDate.plusMonths((mandateFrequency / 4));
        else 
            theNextMonth = nextDebitDateLocalDate.plusWeeks(mandateFrequency);
        return Date.from(theNextMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
