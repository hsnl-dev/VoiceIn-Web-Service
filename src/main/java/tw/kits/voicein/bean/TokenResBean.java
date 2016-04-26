package tw.kits.voicein.bean;

public class TokenResBean {

    private String token;
    private String userUuid;

    public TokenResBean(String token) {
        this.token = token;
    }

    public TokenResBean() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
}
