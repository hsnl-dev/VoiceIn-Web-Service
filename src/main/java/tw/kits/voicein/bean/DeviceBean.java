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
public class DeviceBean {
    @NotNull
    private String deviceOS;
    @NotNull
    private String deviceKey;

    /**
     * @return the deviceOS
     */
    public String getDeviceOS() {
        return deviceOS;
    }

    /**
     * @param deviceOS the deviceOS to set
     */
    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    /**
     * @return the deviceKey
     */
    public String getDeviceKey() {
        return deviceKey;
    }

    /**
     * @param deviceKey the deviceKey to set
     */
    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }
}
