package tw.kits.voicein.model;

import java.util.Date;
import java.util.UUID;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("token")
public class TokenModel {
    @Id 
    private UUID tokenId;

    public TokenModel(UUID tokenId, Date createdAt, int expire) {
        this.tokenId = tokenId;
        this.createdAt = createdAt;
        this.expire = expire;
    }

    public UUID getTokenId() {
        return tokenId;
    }

    public void setTokenId(UUID tokenId) {
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
    private Date createdAt;
    private int expire;
    
    
}
