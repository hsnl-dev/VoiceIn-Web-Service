/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.bean;

import javax.validation.constraints.NotNull;

/**
 *
 * @author Henry
 */
public class UserAuthBean {
   @NotNull
    private String code;
@NotNull
    private String userUuid;

    public UserAuthBean(String code, String userUuid) {
        this.code = code;
        this.userUuid = userUuid;
    }
    public UserAuthBean() {
    }
    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the userUuid
     */
    public String getUserUuid() {
        return userUuid;
    }

    /**
     * @param userUuid the userUuid to set
     */
    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }
    
}
