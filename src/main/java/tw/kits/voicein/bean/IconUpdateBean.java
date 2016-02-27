/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * *
 * {
 * "name": "Henry", "phoneNumber": "XXXXXXXX" }
 *
 * @author Henry
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IconUpdateBean {

    private String name;
    private String phoneNumber;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
