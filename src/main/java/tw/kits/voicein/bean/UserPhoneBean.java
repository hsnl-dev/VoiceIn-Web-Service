package tw.kits.voicein.bean;

import com.google.i18n.phonenumbers.NumberParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.constraints.NotNull;
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.PhoneNum;

public class UserPhoneBean {

    @NotNull
    @PhoneNum
    private String phoneNumber;
    private String mode;

    public String getPhoneNumber() {
         try {
            return Helpers.normalizePhoneNum(phoneNumber);
        } catch (NumberParseException ex) {
            Logger.getLogger(UserAuthBean.class.getName()).log(Level.SEVERE, null, ex);
            return phoneNumber;
        }   
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
}
