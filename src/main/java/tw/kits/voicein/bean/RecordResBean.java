/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.bean;

import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.Record;
import tw.kits.voicein.model.User;

/**
 *
 * @author Henry
 */
public class RecordResBean {

    private String id;
    private long startTime;
    private long endTime;
    private boolean answer;
    private String type;
    private long intervalMills;
    private String anotherUserId;
    private boolean anotherIsIcon;
    private String anotherIconId;
    private String anotherNickName;
    private String anotherName;
    private String anotherNum;
    private String anotherAvatarId;
    private String contactId;

    public RecordResBean() {
    }

    public void setByRecord(Record res) {
        this.id = res.getId();
        this.startTime = res.getStartTime() == null ? -1 : res.getStartTime().getTime();
        this.endTime = res.getEndTime() == null ? -1 : res.getEndTime().getTime();
        this.answer = res.isIsAnswer();
    }

    public RecordResBean(User another, Record res, Contact contact) {
        setByRecord(res);
        this.answer = res.isIsAnswer();
        this.anotherAvatarId = another.getProfilePhotoId();
        this.anotherUserId = another.getUuid();
        this.anotherName = another.getUserName();
        if (contact != null) {
            this.anotherNickName = contact.getNickName();
            this.contactId = contact.getId().toString();
        }

        this.anotherNum = another.getPhoneNumber();
        this.anotherIsIcon = false;
    }

    public RecordResBean(Icon another, Record res, Contact contact) {
        setByRecord(res);
        this.answer = res.isIsAnswer();
        this.anotherAvatarId = null;
        this.anotherUserId = null;
        this.anotherName = another.getName();
        if (contact != null) {
            this.anotherNickName = contact.getNickName();
            this.contactId = contact.getId().toString();
        }
        this.anotherNum = another.getPhoneNumber();
        this.anotherIsIcon = true;
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
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the answer
     */
    public boolean isAnswer() {
        return answer;
    }

    /**
     * @param answer the answer to set
     */
    public void setAnswer(boolean answer) {
        this.answer = answer;
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
     * @return the intervalMills
     */
    public long getIntervalMills() {
        return intervalMills;
    }

    /**
     * @param intervalMills the intervalMills to set
     */
    public void setIntervalMills(long intervalMills) {
        this.intervalMills = intervalMills;
    }

    /**
     * @return the calleeId
     */
    public String getAnotherUserId() {
        return anotherUserId;
    }

    /**
     * @param calleeId the calleeId to set
     */
    public void setAnotherUserId(String calleeId) {
        this.anotherUserId = calleeId;
    }

    /**
     * @return the calleeNickName
     */
    public String getAnotherNickName() {
        return anotherNickName;
    }

    /**
     * @param calleeNickName the calleeNickName to set
     */
    public void setAnotherNickName(String calleeNickName) {
        this.anotherNickName = calleeNickName;
    }

    /**
     * @return the calleeName
     */
    public String getAnotherName() {
        return anotherName;
    }

    /**
     * @param calleeName the calleeName to set
     */
    public void setAnotherName(String calleeName) {
        this.anotherName = calleeName;
    }

    /**
     * @return the calleeNum
     */
    public String getAnotherNum() {
        return anotherNum;
    }

    /**
     * @param calleeNum the calleeNum to set
     */
    public void setAnotherNum(String calleeNum) {
        this.anotherNum = calleeNum;
    }

    /**
     * @return the calleeAvatarId
     */
    public String getAnotherAvatarId() {
        return anotherAvatarId;
    }

    /**
     * @param calleeAvatarId the calleeAvatarId to set
     */
    public void setAnotherAvatarId(String calleeAvatarId) {
        this.anotherAvatarId = calleeAvatarId;
    }

    /**
     * @return the anotherIconId
     */
    public String getAnotherIconId() {
        return anotherIconId;
    }

    /**
     * @param anotherIconId the anotherIconId to set
     */
    public void setAnotherIconId(String anotherIconId) {
        this.anotherIconId = anotherIconId;
    }

    /**
     * @return the anotherIsIcon
     */
    public boolean isAnotherIsIcon() {
        return anotherIsIcon;
    }

    /**
     * @param anotherIsIcon the anotherIsIcon to set
     */
    public void setAnotherIsIcon(boolean anotherIsIcon) {
        this.anotherIsIcon = anotherIsIcon;
    }

    /**
     * @return the contactId
     */
    public String getContactId() {
        return contactId;
    }

    /**
     * @param contactId the contactId to set
     */
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

}
