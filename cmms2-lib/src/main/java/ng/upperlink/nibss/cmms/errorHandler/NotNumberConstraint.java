package ng.upperlink.nibss.cmms.errorHandler;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = NotNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNumberConstraint {
    String message() default "Invalid data, cannot contain any digit";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
