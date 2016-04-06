package tw.kits.voicein.util;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author Henry
 */
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = PhoneNumValidator.class)
@Documented
public @interface PhoneNum  {
    String message() default "Phone number format is not correct need add \"+{coutrycode}\"";

    // Required by validation runtime
    Class<?>[] groups() default {};

    // Required by validation runtime
    Class< ? extends Payload>[] payload() default {};
}
