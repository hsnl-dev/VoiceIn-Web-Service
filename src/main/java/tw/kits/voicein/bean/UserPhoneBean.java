package tw.kits.voicein.bean;

import javax.validation.constraints.NotNull;

public class UserPhoneBean {

    @NotNull
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
