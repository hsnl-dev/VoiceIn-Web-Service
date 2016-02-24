package tw.kits.voicein.model;

import org.mongodb.morphia.annotations.*;

/**
 *
 * @author Calvin
 */
@Entity("accounts")
public class User {

    @Id
    private String uuid;
    private String phoneNumber;
    private String location;
    private String profile;
    private String company;
    private String profilePhotoUrl;

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
    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    /**
     * @param profilePhotoUrl the profilePhotoUrl to set
     */
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    @Override
    public String toString() {
        return "User [uuid=" + getUuid() + ", phoneNumber=" + getPhoneNumber() + "] Saved!";
    }
}
