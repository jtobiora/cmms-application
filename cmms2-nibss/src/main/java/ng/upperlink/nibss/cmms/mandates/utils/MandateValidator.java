package ng.upperlink.nibss.cmms.mandates.utils;

import ng.upperlink.nibss.cmms.dto.mandates.MandateRequest;
import ng.upperlink.nibss.cmms.mandates.exceptions.CustomGenericException;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class MandateValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(MandateValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return Mandate.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        logger.info("Doing validation...");

        MandateRequest mandateReq = (MandateRequest) target;

        logger.info("mandate.getFrequency() "+ mandateReq.getFrequency());
        logger.info("mandate.getMandateStartDate() "+ mandateReq.getMandateStartDate());

                 //check the date vs the frequency selected
        if(mandateReq.getFrequency() > 0 && (!mandateReq.getMandateStartDate().equals("") || !mandateReq.getMandateEndDate().equals(""))){
            logger.info("Trying to validate period and frequency");
            String sDate = mandateReq.getMandateStartDate();
            String eDate = mandateReq.getMandateEndDate();

            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date startDate = sdf.parse(sDate);
                Date endDate= sdf.parse(eDate);

                if(startDate.compareTo(DateUtils.nullifyTime(new Date())) <= 0){
                    throw new CustomGenericException("Mandate start date cannot be today or less!");

                } else {
                    long difference = endDate.getTime() - startDate.getTime();

                    difference = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);

                    logger.info("Date difference between start and end date is " + difference);

                    if(difference < (mandateReq.getFrequency() * 7)){
                        throw new CustomGenericException("Mandate date range must be able to accommodate frequency!");
                    }

                }
            } catch (ParseException e) {
                logger.error(null,e);
                throw new CustomGenericException("Unable to compute debit frequency and period!");
            }
        }
    }


}
