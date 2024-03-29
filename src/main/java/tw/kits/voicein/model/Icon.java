/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.model;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import tw.kits.voicein.util.Helpers;

/**
 *
 * @author Henry
 */
@Entity("icon")
public class Icon {

    @Reference
    private User provider;
    private String qrCodeId;
    private String name;
    private String phoneNumber;
    private String location;
    private String company;
    private String availableStartTime;
   
    private String availableEndTime;
    private boolean isEnable;
    @Id
    private String iconId;

    /**
     * @return the provider
     */
    public User getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(User provider) {
        this.provider = provider;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        try {
            return Helpers.transferRawPhoneNumberToNationalFormat(phoneNumber, "");
        } catch (NumberParseException ex) {
            Logger.getLogger(QRcode.class.getName()).log(Level.SEVERE, null, ex);
            return phoneNumber;
        }
    }
      public String getOriginPhoneNumber() {
            return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) { 
        try {
            this.phoneNumber = Helpers.normalizePhoneNum(phoneNumber);
        } catch (NumberParseException ex) {
            Logger.getLogger(Icon.class.getName()).log(Level.SEVERE, null, ex);
            this.phoneNumber = phoneNumber;
        }
    }

    /**
     * @return the iconId
     */
    public String getIconId() {
        return iconId;
    }

    /**
     * @param iconId the iconId to set
     */
    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the company
     */
    public String getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return the availableStartTime
     */
    public String getAvailableStartTime() {
        return availableStartTime.replaceAll("\\s+","");
    }

    /**
     * @param availableStartTime the availableStartTime to set
     */
    public void setAvailableStartTime(String availableStartTime) {
        this.availableStartTime = availableStartTime.replaceAll("\\s+","");
    }

    /**
     * @return the availableEndTime
     */
    public String getAvailableEndTime() {
        return availableEndTime.replaceAll("\\s+","");
    }

    /**
     * @param availableEndTime the availableEndTime to set
     */
    public void setAvailableEndTime(String availableEndTime) {
        this.availableEndTime = availableEndTime.replaceAll("\\s+","");
    }

    /**
     * @return the isEnable
     */
    public Boolean getIsEnable() {
        return isEnable;
    }

    /**
     * @param isEnable the isEnable to set
     */
    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    /**
     * @return the qrCodeId
     */
    public String getQrCodeId() {
        return qrCodeId;
    }

    /**
     * @param qrCodeId the qrCodeId to set
     */
    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }


}
