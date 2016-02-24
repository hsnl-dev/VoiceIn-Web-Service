package tw.kits.voicein.model;

import java.util.Date;
import java.util.UUID;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity("code")
public class VCodeModel {
    @Id 
    private String vcodeId;
    @Reference
    private User user;
    private String code;
    private Date createAt;
    private int expiredTime;
    
    public VCodeModel() {
    };

    public VCodeModel(User userUuid, String code, Date createAt, int expiredTime) {
        this.vcodeId = UUID.randomUUID().toString();
        this.user = userUuid;
        this.code = code;
        this.createAt = createAt;
        this.expiredTime = expiredTime;
    }
  
    public String getVcodeId() {
        return vcodeId;
    }

    public void setVcodeId(String vcodeId) {
        this.vcodeId = vcodeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public int getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(int expiredTime) {
        this.expiredTime = expiredTime;
    }

    /**
     * @return the userUuid
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }
    
}
