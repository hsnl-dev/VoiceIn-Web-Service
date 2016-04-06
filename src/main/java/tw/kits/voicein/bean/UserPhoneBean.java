package tw.kits.voicein.bean;

import javax.validation.constraints.NotNull;
import tw.kits.voicein.util.PhoneNum;

public class UserPhoneBean {

    @NotNull
    @PhoneNum
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
