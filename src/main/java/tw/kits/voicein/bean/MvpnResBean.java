package tw.kits.voicein.bean;

/**
 *
 * @author Henry
 * 
 */
public class MvpnResBean {
    private boolean isMvpn;
    private String status;

    /**
     * @return the isMvpn
     */
    public boolean isIsMvpn() {
        return isMvpn;
    }

    /**
     * @param isMvpn the isMvpn to set
     */
    public void setIsMvpn(boolean isMvpn) {
        this.isMvpn = isMvpn;
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
}
