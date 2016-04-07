package tw.kits.voicein.model;

import java.util.Date;
import org.mongodb.morphia.annotations.Id;

/**
 *
 * @author Henry
 */
public class Record {
    @Id
    private String id;
    private boolean isViaIcon;
    private Contact viaContact;
    private Icon viaIcon;
    private Date reqTime;
    private Date startTime;
    private Date endTime;
    private float chargeDollar;
    private boolean isAnswer;
    private int status;

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
     * @return the isViaIcon
     */
    public boolean isIsViaIcon() {
        return isViaIcon;
    }

    /**
     * @param isViaIcon the isViaIcon to set
     */
    public void setIsViaIcon(boolean isViaIcon) {
        this.isViaIcon = isViaIcon;
    }

    /**
     * @return the viaContact
     */
    public Contact getViaContact() {
        return viaContact;
    }

    /**
     * @param viaContact the viaContact to set
     */
    public void setViaContact(Contact viaContact) {
        this.viaContact = viaContact;
    }

    /**
     * @return the viaIcon
     */
    public Icon getViaIcon() {
        return viaIcon;
    }

    /**
     * @param viaIcon the viaIcon to set
     */
    public void setViaIcon(Icon viaIcon) {
        this.viaIcon = viaIcon;
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
    
}
