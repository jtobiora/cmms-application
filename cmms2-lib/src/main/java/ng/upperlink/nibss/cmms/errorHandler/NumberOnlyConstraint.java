package ng.upperlink.nibss.cmms.errorHandler;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = NumberOnlyValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberOnlyConstraint {
    String message() default "Invalid data, must contain only digits.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
