package tw.kits.voicein.bean;

public class ErrorMessageBean {

    private String errorReason;

    public ErrorMessageBean(String msg) {
        errorReason = msg;
    }

    public ErrorMessageBean() {
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }
}
