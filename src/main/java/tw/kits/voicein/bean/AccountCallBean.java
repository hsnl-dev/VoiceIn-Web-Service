package tw.kits.voicein.bean;

import javax.validation.constraints.NotNull;

/**
 *
 * @author Calvin
 */
public class AccountCallBean {
    @NotNull
    private String callee;
    @NotNull
    private String caller;

    /**
     * @return the callee
     */
    public String getCallee() {
        return callee;
    }

    /**
     * @param callee the callee to set
     */
    public void setCallee(String callee) {
        this.callee = callee;
    }

    /**
     * @return the caller
     */
    public String getCaller() {
        return caller;
    }

    /**
     * @param caller the caller to set
     */
    public void setCaller(String caller) {
        this.caller = caller;
    }

}
