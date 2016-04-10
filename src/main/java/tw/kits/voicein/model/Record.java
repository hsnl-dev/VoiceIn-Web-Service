package tw.kits.voicein.model;

import java.util.Date;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import tw.kits.voicein.constant.ContactConstant;
import tw.kits.voicein.constant.RecordConstant;

/**
 *
 * @author Henry
 */
@Entity("record")
public class Record {

    @Id
    private String id;
    private String type;
    @Reference
    private User caller;
    @Reference
    private User callee;
    @Reference
    private Icon callerIcon;
    @Reference
    private Icon calleeIcon;
    private ObjectId callerContactId;
    private Date reqTime;
    private Date startTime;
    private Date endTime;
    private float chargeDollar;
    private boolean isAnswer;
    private int status;
    private String callerPhone;
    private String calleePhone;

    public void setInitCall(User caller, User callee, Contact contact) {
        this.setId(UUID.randomUUID().toString());
        this.setReqTime(new Date());
        this.setStatus(RecordConstant.REQ_SEND);
        this.setCallerPhone(caller.getOriginNumber());
        this.setCalleePhone(callee.getOriginNumber());
        this.setCaller(caller);
        this.setCallee(callee);
        switch (contact.getChargeType()) {
            case ContactConstant.TYPE_FREE:
                this.setType(RecordConstant.APP_TO_APP_CHARGE_CALLEE);
                break;
            case ContactConstant.TYPE_CHARGE:
                this.setType(RecordConstant.APP_TO_APP_CHARGE_CALLER);
                break;
        }
    }

    public void setAsymetricCall(User account, Icon icon, boolean isCallByAccount, Contact callerContact) {
        this.setId(UUID.randomUUID().toString());
        this.setReqTime(new Date());
        this.setStatus(RecordConstant.REQ_SEND);
        if (isCallByAccount) {
            this.setCallerPhone(account.getPhoneNumber());
            this.setCalleePhone(icon.getPhoneNumber());
            this.setCaller(account);
            this.setCalleeIcon(icon);
            this.setType(RecordConstant.APP_TO_ICON);
            this.setCallerContactId(callerContact.getId());
        } else {
            this.setCallerPhone(icon.getPhoneNumber());
            this.setCalleePhone(account.getOriginNumber());
            this.setCallee(account);
            this.setCallerIcon(icon);
            this.setType(RecordConstant.ICON_TO_APP);
        }
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
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the chargeDollar
     */
    public float getChargeDollar() {
        return chargeDollar;
    }

    /**
     * @param chargeDollar the chargeDollar to set
     */
    public void setChargeDollar(float chargeDollar) {
        this.chargeDollar = chargeDollar;
    }

    /**
     * @return the reqTime
     */
    public Date getReqTime() {
        return reqTime;
    }

    /**
     * @param reqTime the reqTime to set
     */
    public void setReqTime(Date reqTime) {
        this.reqTime = reqTime;
    }

    /**
     * @return the isAnswer
     */
    public boolean isIsAnswer() {
        return isAnswer;
    }

    /**
     * @param isAnswer the isAnswer to set
     */
    public void setIsAnswer(boolean isAnswer) {
        this.isAnswer = isAnswer;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the callerPhone
     */
    public String getCallerPhone() {
        return callerPhone;
    }

    /**
     * @param callerPhone the callerPhone to set
     */
    public void setCallerPhone(String callerPhone) {
        this.callerPhone = callerPhone;
    }

    /**
     * @return the calleePhone
     */
    public String getCalleePhone() {
        return calleePhone;
    }

    /**
     * @param calleePhone the calleePhone to set
     */
    public void setCalleePhone(String calleePhone) {
        this.calleePhone = calleePhone;
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
     * @return the callerContactId
     */
    public ObjectId getCallerContactId() {
        return callerContactId;
    }

    /**
     * @param callerContactId the callerContactId to set
     */
    public void setCallerContactId(ObjectId callerContactId) {
        this.callerContactId = callerContactId;
    }

    /**
     * @return the caller
     */
    public User getCaller() {
        return caller;
    }

    /**
     * @param caller the caller to set
     */
    public void setCaller(User caller) {
        this.caller = caller;
    }

    /**
     * @return the callee
     */
    public User getCallee() {
        return callee;
    }

    /**
     * @param callee the callee to set
     */
    public void setCallee(User callee) {
        this.callee = callee;
    }

    /**
     * @return the callerIcon
     */
    public Icon getCallerIcon() {
        return callerIcon;
    }

    /**
     * @param callerIcon the callerIcon to set
     */
    public void setCallerIcon(Icon callerIcon) {
        this.callerIcon = callerIcon;
    }

    /**
     * @return the calleeIcon
     */
    public Icon getCalleeIcon() {
        return calleeIcon;
    }

    /**
     * @param calleeIcon the calleeIcon to set
     */
    public void setCalleeIcon(Icon calleeIcon) {
        this.calleeIcon = calleeIcon;
    }

}
