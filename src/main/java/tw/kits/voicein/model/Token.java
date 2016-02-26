package tw.kits.voicein.model;

import java.util.Date;
import java.util.UUID;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity("token")
public class Token {
    @Id 
    private String tokenId;
    @Reference
    private User user;
    private Date createdAt;
    private int expire;

    public Token(String tokenId, Date createdAt, int expire) {
        this.tokenId = tokenId;
        this.createdAt = createdAt;
        this.expire = expire;
    }
    
    public Token(int expire) {
        this.tokenId = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.expire = expire;
    }
    
    public Token() {
    }
    
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    
}
