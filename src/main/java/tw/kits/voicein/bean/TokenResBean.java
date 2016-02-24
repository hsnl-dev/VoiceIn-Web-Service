package tw.kits.voicein.bean;


public class TokenResBean {
    private String token;
    
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
}
