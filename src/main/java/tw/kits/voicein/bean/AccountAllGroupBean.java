package tw.kits.voicein.bean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Calvin
 */
public class AccountAllGroupBean {
    private ArrayList<HashMap<String, Object>> groups;

    /**
     * @return the groups
     */
    public ArrayList<HashMap<String, Object>> getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(ArrayList<HashMap<String, Object>> groups) {
        this.groups = groups;
    }
}
