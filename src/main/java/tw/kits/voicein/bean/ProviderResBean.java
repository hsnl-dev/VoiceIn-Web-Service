package tw.kits.voicein.bean;

/***
 *
 * @author Henry
 * "name": "Anthony Kong",
      "avatarId": "...",
      "profile": "I am Anthony Kong.",
      "location": "Taipei",
      "company": "HSNL"
 */
public class ProviderResBean {
    private String name;
    private String avatarId;
    private String profile;
    private String location;
    private String company;

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
     * @return the avatarId
     */
    public String getAvatarId() {
        return avatarId;
    }

    /**
     * @param avatarId the avatarId to set
     */
    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }
}
