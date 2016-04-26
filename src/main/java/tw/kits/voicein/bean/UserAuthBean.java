/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.bean;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.constraints.NotNull;
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.PhoneNum;

/**
 *
 * @author Henry
 */
public class UserAuthBean {

    
    private String code;
    private String mode;
    private String password;
    private String phoneNumber;
    private String userUuid;

    public UserAuthBean(String code, String userUuid) {
        this.code = code;
        this.userUuid = userUuid;
    }

    public UserAuthBean() {

    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the userUuid
     */
    public String getUserUuid() {
        return userUuid;
    }

    /**
     * @param userUuid the userUuid to set
     */
    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
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

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        try {
            return Helpers.normalizePhoneNum(phoneNumber);
        } catch (NumberParseException ex) {
            Logger.getLogger(UserAuthBean.class.getName()).log(Level.SEVERE, null, ex);
            return phoneNumber;
        }   
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
