/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.bean;

import org.bson.types.ObjectId;
import tw.kits.voicein.model.Icon;

/**
 *
 * @author Calvin
 */
public class UserContactBean {

    //Field
    private String id; 
    private String userName;
    private String phoneNumber;
    private String location;
    private String profile;
    private String company;
    private String profilePhotoId;
    private String nickName;
    private int chargeType;
    private String availableStartTime;
    private String availableEndTime;
    private Boolean isEnable;
    private Boolean isHigherPriorityThanGlobal;
    private String providerAvailableStartTime;
    private String providerAvailableEndTime;
    private Boolean providerIsEnable;
    private String qrCodeUuid;
    private Icon customerIcon;

    public UserContactBean() {

    }

    /**
     * @return the nickName
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @param nickName the nickName to set
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * @return the customerIcon
     */
    public Icon getCustomerIcon() {
        return customerIcon;
    }

    /**
     * @param customerIcon the customerIcon to set
     */
    public void setCustomerIcon(Icon customerIcon) {
        this.customerIcon = customerIcon;
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
     * @return the qrCodeUuid
     */
    public String getQrCodeUuid() {
        return qrCodeUuid;
    }

    /**
     * @param qrCodeUuid the qrCodeUuid to set
     */
    public void setQrCodeUuid(String qrCodeUuid) {
        this.qrCodeUuid = qrCodeUuid;
    }

    /**
     * @return the chargeType
     */
    public int getChargeType() {
        return chargeType;
    }

    /**
     * @param chargeType the chargeType to set
     */
    public void setChargeType(int chargeType) {
        this.chargeType = chargeType;
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
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
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
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(String profile) {
        this.profile = profile;
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
     * @return the profilePhotoId
     */
    public String getProfilePhotoId() {
        return profilePhotoId;
    }

    /**
     * @param profilePhotoId the profilePhotoId to set
     */
    public void setProfilePhotoId(String profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }

    /**
     * @return the providerAvailableStartTime
     */
    public String getProviderAvailableStartTime() {
        return providerAvailableStartTime;
    }

    /**
     * @param providerAvailableStartTime the providerAvailableStartTime to set
     */
    public void setProviderAvailableStartTime(String providerAvailableStartTime) {
        this.providerAvailableStartTime = providerAvailableStartTime;
    }

    /**
     * @return the providerAvailableEndTime
     */
    public String getProviderAvailableEndTime() {
        return providerAvailableEndTime;
    }

    /**
     * @param providerAvailableEndTime the providerAvailableEndTime to set
     */
    public void setProviderAvailableEndTime(String providerAvailableEndTime) {
        this.providerAvailableEndTime = providerAvailableEndTime;
    }

    /**
     * @return the providerIsEnable
     */
    public Boolean getProviderIsEnable() {
        return providerIsEnable;
    }

    /**
     * @param providerIsEnable the providerIsEnable to set
     */
    public void setProviderIsEnable(Boolean providerIsEnable) {
        this.providerIsEnable = providerIsEnable;
    }

    /**
     * @return the isHigherPriorityThanGlobal
     */
    public Boolean getIsHigherPriorityThanGlobal() {
        return isHigherPriorityThanGlobal;
    }

    /**
     * @param isHigherPriorityThanGlobal the isHigherPriorityThanGlobal to set
     */
    public void setIsHigherPriorityThanGlobal(Boolean isHigherPriorityThanGlobal) {
        this.isHigherPriorityThanGlobal = isHigherPriorityThanGlobal;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

}
