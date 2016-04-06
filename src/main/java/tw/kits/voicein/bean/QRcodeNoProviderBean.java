package tw.kits.voicein.bean;

import tw.kits.voicein.model.*;
import java.util.Date;
import javax.validation.constraints.NotNull;
import tw.kits.voicein.util.PhoneNum;

/**
 *
 * @author Henry
 */
public class QRcodeNoProviderBean {
   
    @NotNull
    private String id;
    private String userName;
    @NotNull
    @PhoneNum
    private String phoneNumber;
    private String state;
    private Date createdAt;
    private Date updateAt;
    private String location;
    private String company;
    private String type;
    public QRcodeNoProviderBean(){}
    public QRcodeNoProviderBean(QRcode code){
        super();
        this.id = code.getId();
        this.userName = code.getUserName();
        this.phoneNumber = code.getPhoneNumber();
        this.location = code.getLocation();
        this.company = code.getCompany();
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
    
    
}
