package ng.upperlink.nibss.cmms.errorHandler;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class NumberOnlyValidator implements ConstraintValidator<NumberOnlyConstraint, String> {

        @Override
        public void initialize(NumberOnlyConstraint numberOnlyConstraint) {

        }

        @Override
        public boolean isValid(String text, ConstraintValidatorContext cxt) {

                if (text != null && text.matches("^\\d*$")){
                        return true;
                }
                        return false;
                }

        }
