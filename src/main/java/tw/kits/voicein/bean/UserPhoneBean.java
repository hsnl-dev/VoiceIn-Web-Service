package tw.kits.voicein.bean;

import javax.validation.constraints.NotNull;
import tw.kits.voicein.util.PhoneNum;

public class UserPhoneBean {

    @NotNull
    @PhoneNum
    private String phoneNumber;
    private String mode;

    public String getPhoneNumber() {
        return phoneNumber;
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
