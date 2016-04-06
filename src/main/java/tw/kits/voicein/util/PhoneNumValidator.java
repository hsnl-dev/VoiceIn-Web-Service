package tw.kits.voicein.util;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Henry
 */
public class PhoneNumValidator implements ConstraintValidator<PhoneNum, String> {

    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    @Override
    public void initialize(PhoneNum constraintAnnotation) {

    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }
        return phoneUtil.isPossibleNumber(object, "ZZ");
        /*ZZ means +country code*/
    }
}
