package tw.kits.voicein.bean;

/**
 * for send to sip server
 * @author Henry
 */
public class InitPhoneCallBean {
    private String callerNumber;
    private String calleeNumber;
    private String callerid;
    private String hisuid;
    private String credit;
    private boolean check;

    /**
     * @return the callerNumber
     */
    public String getCallerNumber() {
        return callerNumber;
    }

    /**
     * @param callerNumber the callerNumber to set
     */
    public void setCallerNumber(String callerNumber) {
        this.callerNumber = callerNumber;
    }

    /**
     * @return the calleeNumber
     */
    public String getCalleeNumber() {
        return calleeNumber;
    }

    /**
     * @param calleeNumber the calleeNumber to set
     */
    public void setCalleeNumber(String calleeNumber) {
        this.calleeNumber = calleeNumber;
    }

    /**
     * @return the callerid
     */
    public String getCallerid() {
        return callerid;
    }

    /**
     * @param callerid the callerid to set
     */
    public void setCallerid(String callerid) {
        this.callerid = callerid;
    }

    /**
     * @return the hisuid
     */
    public String getHisuid() {
        return hisuid;
    }

    /**
     * @param hisuid the hisuid to set
     */
    public void setHisuid(String hisuid) {
        this.hisuid = hisuid;
    }

    /**
     * @return the credit
     */
    public String getCredit() {
        return credit;
    }

    /**
     * @param credit the credit to set
     */
    public void setCredit(String credit) {
        this.credit = credit;
    }

    /**
     * @return the check
     */
    public boolean isCheck() {
        return check;
    }

    /**
     * @param check the check to set
     */
    public void setCheck(boolean check) {
        this.check = check;
    }
}
