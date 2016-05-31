package tw.kits.voicein.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public class Parameter {

    public static final String API_ROOT = "https://ts.kits.tw/projectLYS/";
    public static final String API_VER = "v0/";
    public static final String API_KEY = Parameter.getParameter("API_KEY","2a4e0dd8db3807790d853dabf0f448de21cea6057b5dc48539330f934e9bddfb");
    public static final AWSCredentials AWS_CREDENTIALS = new BasicAWSCredentials("AKIAI4RNBMGTLTP6HWFQ", "bKOJ+9emilF2cZIK+44bRBoRHpPxyJmfc7y+VpFe");
    public static final String DB_NAME = Parameter.getParameter("DB_NAME","voicein");
    public final static String DB_URI = Parameter.getParameter("DB_URI","mongodb://hsnl-dev:hsnl33564hsnl33564@ds013908.mongolab.com:13908/voicein");
    public static final Boolean IS_SANDBOX = !"false".equals(Parameter.getParameter("IS_SANDBOX","false"));
    public static final Boolean IS_APS_SANDBOX = !"false".equals(Parameter.getParameter("IS_APS_SANDBOX","true"));
    public static final String HOST_NAME = Parameter.getParameter("WEB_HOST_NAME","https://voicein.herokuapp.com/");
    public static final String WEB_SITE_QRCODE= HOST_NAME+"qrcode?id=";
    public static final String S3_QR_CODE_FOLDER = Parameter.getParameter("S3_QR_CODE_FOLDER","qrCode");
    public static final String S3_AVATAR_FOLDER = Parameter.getParameter("S3_AVATAR_FOLDER","userPhotos");
    public static final String SECRET_KEY = "RYlvpRyzJMLlR5vpXKnn";
    public static String getParameter(String key,String defaultValue){
        if(System.getProperty(key) != null){
            return System.getProperty(key);
        }else if(System.getenv(key) != null){
            return System.getenv(key);
        }else{
            return defaultValue;
        }
    }
    
}
