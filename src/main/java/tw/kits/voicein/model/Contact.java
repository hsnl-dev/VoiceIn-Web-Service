package tw.kits.voicein.model;

import javax.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 *
 * @author Calvin
 */
@Entity("contacts")
public class Contact {
    //Field

    @Id
    private ObjectId id;
    @Reference
    private User user;
    @Reference
    private User providerUser;
    private String nickName;
    @NotNull
    private Boolean isFree;
    @NotNull
    private String availableStartTime;
    @NotNull
    private String availableEndTime;
    private String qrCodeUuid;
    @Reference
    private Icon customerIcon;

    public Contact() {

    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the nickName
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @param nickName the nickName to set
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * @return the id
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * @return the customerIcon
     */
    public Icon getCustomerIcon() {
        return customerIcon;
    }

    /**
     * @param customerIcon the customerIcon to set
     */
    public void setCustomerIcon(Icon customerIcon) {
        this.customerIcon = customerIcon;
    }

    /**
     * @return the availableStartTime
     */
    public String getAvailableStartTime() {
        return availableStartTime;
    }

    /**
     * @param availableStartTime the availableStartTime to set
     */
    public void setAvailableStartTime(String availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    /**
     * @return the availableEndTime
     */
    public String getAvailableEndTime() {
        return availableEndTime;
    }

    /**
     * @param availableEndTime the availableEndTime to set
     */
    public void setAvailableEndTime(String availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    /**
     * @return the providerUser
     */
    public User getProviderUser() {
        return providerUser;
    }

    /**
     * @param providerUser the providerUser to set
     */
    public void setProviderUser(User providerUser) {
        this.providerUser = providerUser;
    }

    /**
     * @return the isFree
     */
    public Boolean getIsFree() {
        return isFree;
    }

    /**
     * @param isFree the isFree to set
     */
    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    /**
     * @return the qrCodeUuid
     */
    public String getQrCodeUuid() {
        return qrCodeUuid;
    }

    /**
     * @param qrCodeUuid the qrCodeUuid to set
     */
    public void setQrCodeUuid(String qrCodeUuid) {
        this.qrCodeUuid = qrCodeUuid;
    }

}
