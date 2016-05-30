package tw.kits.voicein.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public interface Parameter {
    public static final String API_ROOT = "https://ts.kits.tw/projectLYS/";
    public static final String API_VER = "v0/";
    public static final String API_KEY = System.getenv("API_KEY") == null ? "2a4e0dd8db3807790d853dabf0f448de21cea6057b5dc48539330f934e9bddfb" : System.getenv("API_KEY");
    public static final AWSCredentials AWS_CREDENTIALS = new BasicAWSCredentials("AKIAI4RNBMGTLTP6HWFQ", "bKOJ+9emilF2cZIK+44bRBoRHpPxyJmfc7y+VpFe");
    public static final String DB_NAME = System.getenv("DB_NAME") == null ? "voicein" : System.getenv("DB_NAME");
    public final static String DB_URI = System.getenv("DB_URI") == null ? "mongodb://hsnl-dev:hsnl33564hsnl33564@ds013908.mongolab.com:13908/voicein" : System.getenv("DB_URI");
    public static final Boolean IS_SANDBOX = !"false".equals(System.getenv("IS_SANDBOX"));
    public static final Boolean IS_APS_SANDBOX = !"false".equals(System.getenv("IS_APS_SANDBOX"));
    public static final String WEB_SITE_QRCODE= "https://voicein.kits.tw/qrcode?id=";
    public static final String HOST_NAME = "https://voicein-api.kits.tw/";
    public static final String SECRET_KEY = "RYlvpRyzJMLlR5vpXKnn";
}
