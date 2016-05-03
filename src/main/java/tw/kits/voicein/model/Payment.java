/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import tw.kits.voicein.bean.*;

/**
 *
 * @author Henry
 */
@Entity("payments")
public class Payment {
    @Id
    private String id;
    private String payId;
    private String userId;
    private String status;
    private int money;
    private String transationStatus;
    private String method;

    /**
     * @return the payId
     */
    public String getPayId() {
        return payId;
    }

    /**
     * @param payId the payId to set
     */
    public void setPayId(String payId) {
        this.payId = payId;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the money
     */
    public int getMoney() {
        return money;
    }

    /**
     * @param money the money to set
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
     * @return the transationStatus
     */
    public String getTransationStatus() {
        return transationStatus;
    }

    /**
     * @param transationStatus the transationStatus to set
     */
    public void setTransationStatus(String transationStatus) {
        this.transationStatus = transationStatus;
    }
}
