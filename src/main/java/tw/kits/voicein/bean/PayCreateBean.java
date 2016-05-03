/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.bean;

/**
 *
 * @author Henry
 */
public class PayCreateBean {
    private String payId;
    private String status;
    private int money;
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


}
