package ng.upperlink.nibss.cmms.errorHandler;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateFormatValidator implements ConstraintValidator<DateFormatConstraint, String>{

    @Override
    public void initialize(DateFormatConstraint dateFormatContraint) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }
}
