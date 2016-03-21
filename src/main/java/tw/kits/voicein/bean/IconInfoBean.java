/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.bean;

import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.Helpers;

/**
 *
 * @author Henry
 */
public class IconInfoBean {

    private User provider;
    private String name;
    private String phoneNumber;
    private String location;
    private String company;
    private String availableStartTime;
    private String availableEndTime;
    private boolean isEnable;
    private String iconId;
    private boolean isTargetEnable;

    public IconInfoBean() {
    }

    public IconInfoBean(Icon icon, Contact target) {
        this.provider = icon.getProvider();
        this.name = icon.getName();
        this.phoneNumber = icon.getPhoneNumber();
        this.location = icon.getLocation();
        this.company = icon.getCompany();
        this.availableStartTime = icon.getAvailableStartTime();
        this.availableEndTime = icon.getAvailableEndTime();
        this.isEnable = icon.getIsEnable();
        this.isTargetEnable = Helpers.isAllowedToCall(target);
        this.iconId = icon.getIconId();

    }

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
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
        return availableStartTime;
    }

    /**
     * @param availableStartTime the availableStartTime to set
     */
    public void setAvailableStartTime(String availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    /**
     * @return the availableEndTime
     */
    public String getAvailableEndTime() {
        return availableEndTime;
    }

    /**
     * @param availableEndTime the availableEndTime to set
     */
    public void setAvailableEndTime(String availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    /**
     * @return the isEnable
     */
    public boolean isIsEnable() {
        return isEnable;
    }

    /**
     * @param isEnable the isEnable to set
     */
    public void setIsEnable(boolean isEnable) {
        this.isEnable = isEnable;
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
     * @return the isTargetEnable
     */
    public boolean isIsTargetEnable() {
        return isTargetEnable;
    }

    /**
     * @param isTargetEnable the isTargetEnable to set
     */
    public void setIsTargetEnable(boolean isTargetEnable) {
        this.isTargetEnable = isTargetEnable;
    }
}
