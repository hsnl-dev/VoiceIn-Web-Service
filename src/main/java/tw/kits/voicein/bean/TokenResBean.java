package tw.kits.voicein.bean;


public class TokenResBean {

    public TokenResBean(String token) {
        this.token = token;
    }
    public TokenResBean() {
      
    }
   private String token; 


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
