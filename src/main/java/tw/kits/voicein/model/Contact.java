package tw.kits.voicein.model;

import java.util.Date;
import javax.validation.constraints.Min;
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
    @NotNull
    private String nickName;
    @Min(0)
    @NotNull
    private int chargeType;
    @NotNull
    private String availableStartTime;
    @NotNull
    private String availableEndTime;
    @NotNull
    private Boolean isEnable;
    private String qrCodeUuid;
    @Reference
    private Icon customerIcon;
    private Boolean isHigherPriorityThanGlobal;
    private Boolean isLike;
    private Date createAt;
    private Date updateAt;

    public Contact() {
        this.isHigherPriorityThanGlobal = false;
        this.availableStartTime = "00:00";
        this.availableEndTime = "23:59";
        this.createAt = new Date();
        this.updateAt = new Date();
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

    /**
     * @return the chargeType
     */
    public int getChargeType() {
        return chargeType;
    }

    /**
     * @param chargeType the chargeType to set
     */
    public void setChargeType(int chargeType) {
        this.chargeType = chargeType;
    }

    /**
     * @return the isEnable
     */
    public Boolean getIsEnable() {
        return isEnable;
    }

    /**
     * @param isEnable the isEnable to set
     */
    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    /**
     * @return the isHigherPriorityThanGlobal
     */
    public Boolean getIsHigherPriorityThanGlobal() {
        return isHigherPriorityThanGlobal;
    }

    /**
     * @param isHigherPriorityThanGlobal the isHigherPriorityThanGlobal to set
     */
    public void setIsHigherPriorityThanGlobal(Boolean isHigherPriorityThanGlobal) {
        this.isHigherPriorityThanGlobal = isHigherPriorityThanGlobal;
    }

    /**
     * @return the isLike
     */
    public Boolean getIsLike() {
        return isLike;
    }

    /**
     * @param isLike the isLike to set
     */
    public void setIsLike(Boolean isLike) {
        this.isLike = isLike;
    }

    /**
     * @return the createAt
     */
    public Date getCreateAt() {
        return createAt;
    }

    /**
     * @param createAt the createAt to set
     */
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
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

}
