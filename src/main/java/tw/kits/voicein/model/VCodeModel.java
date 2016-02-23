package tw.kits.voicein.model;

import java.util.Date;
import java.util.UUID;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("code")
public class VCodeModel {
    @Id 
    private UUID vcodeId;
    private String code;
    private Date createAt;
    private int expiredTime;
    private String phoneNumber;
    public VCodeModel(){};
    public VCodeModel(UUID vcodeId, String code, Date createAt, int expiredTime, String phoneNumber) {
        this.vcodeId = vcodeId;
        this.code = code;
        this.createAt = createAt;
        this.expiredTime = expiredTime;
        this.phoneNumber = phoneNumber;
    }
    public UUID getVcodeId() {
        return vcodeId;
    }

    public void setVcodeId(UUID vcodeId) {
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
    
}
