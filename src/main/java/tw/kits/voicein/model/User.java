package tw.kits.voicein.model;

import javax.validation.constraints.NotNull;
import org.mongodb.morphia.annotations.*;

/**
 *
 * @author Calvin
 */
@Entity("accounts")
public class User {

    @Id
    private String uuid;
    @NotNull
    private String userName;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String location;
    @NotNull
    private String profile;
    @NotNull
    private String company;
    private String profilePhotoId;
    private String qrCodeUuid;
    @NotNull
    private String availableStartTime;
    @NotNull
    private String availableEndTime;
    

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
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
     * @return the profilePhotoUrl
     */
    public String getProfilePhotoId() {
        return profilePhotoId;
    }

    /**
     * @param profilePhotoUrl the profilePhotoUrl to set
     */
    public void setProfilePhotoId(String profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }

    @Override
    public String toString() {
        return "User [uuid=" + getUuid() + ", phoneNumber=" + getPhoneNumber() + "] Saved!";
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
}
