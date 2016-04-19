/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.bean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Calvin
 */
public class NotificationListBean {
    private ArrayList<HashMap<String, Object>> notifications;

    /**
     * @return the notifications
     */
    public ArrayList<HashMap<String, Object>> getNotifications() {
        return notifications;
    }

    /**
     * @param notifications the notifications to set
     */
    public void setNotifications(ArrayList<HashMap<String, Object>> notifications) {
        this.notifications = notifications;
    }
}
