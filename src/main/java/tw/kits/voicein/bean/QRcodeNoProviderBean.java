package tw.kits.voicein.bean;

import tw.kits.voicein.model.*;
import java.util.Date;

/**
 *
 * @author Henry
 */
public class QRcodeNoProviderBean {
   
    private String id;
    private String userName;
    private String phoneNumber;
    private String state;
    private Date createdAt;
    private Date updateAt;
    private String type;
    public QRcodeNoProviderBean(){}
    public QRcodeNoProviderBean(QRcode code){
        super();
        this.id = code.getId();
        this.userName = code.getUserName();
        this.phoneNumber = code.getPhoneNumber();
        this.state = code.getState();
        this.createdAt = code.getCreatedAt();
        this.updateAt = code.getUpdateAt();
        this.type = code.getType();
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the updateAt
     */
    public Date getUpdateAt() {
        return updateAt;
    }

    /**
     * @param updateAt the updateAt to set
     */
    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    
}
