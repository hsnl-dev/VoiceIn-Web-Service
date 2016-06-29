/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.exceptions.InvalidSSLConfig;
import com.notnoop.exceptions.NetworkIOException;
import com.notnoop.exceptions.RuntimeIOException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import okhttp3.Headers;
import okhttp3.Response;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.GcmPayloadBean;
import tw.kits.voicein.bean.InitPhoneCallBean;
import tw.kits.voicein.bean.MvpnResBean;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.Notification;
import tw.kits.voicein.model.Record;
import tw.kits.voicein.model.User;
import static tw.kits.voicein.util.Parameter.IS_SANDBOX;

/**
 *
 * @author Henry
 */
public class Helpers {

    static final Logger LOGGER = Logger.getLogger(Helpers.class.getName());
    static final String SIP_URL = "http://210.71.198.42:33564/sip/SingleCall";

    public static String normalizePhoneNum(String phoneNumber) throws NumberParseException {

        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        PhoneNumber phone = util.parse(phoneNumber, "ZZ");

        return util.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    public static String transferRawPhoneNumberToNationalFormat(String phoneNumber, String defaultContry) throws NumberParseException {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        PhoneNumber number = phoneNumberUtil.parse(phoneNumber, defaultContry);
        return phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL).replaceAll("\\s", "");
    }

    public static boolean isUserMatchToken(String userUuid, SecurityContext sc) {
        String tokenUserUuid = sc.getUserPrincipal().getName();
        LOGGER.info(userUuid);
        LOGGER.log(Level.CONFIG, ":{0}", tokenUserUuid);
        return tokenUserUuid.equals(userUuid);
    }
    // Helpers methods.

    public static boolean isAllowedToCall(Contact contact) {
        String availableStartTime;
        String availableEndTime;
        //ConsoleHandler consoleHandler = new ConsoleHandler();
        //consoleHandler.setLevel(Level.CONFIG);
        //LOGGER.addHandler(consoleHandler);

        User provider = contact.getUser();
        boolean isEnable = contact.getIsEnable();

        if (contact.getIsHigherPriorityThanGlobal()) {
            availableStartTime = contact.getAvailableStartTime();
            availableEndTime = contact.getAvailableEndTime();
        } else {
            availableStartTime = provider.getAvailableStartTime();
            availableEndTime = provider.getAvailableEndTime();
        }

        // Get current time.
        Date currentTimeStamp = new Date();
        // In 24 type.
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        String currentTimeInString = sdf.format(currentTimeStamp);

        // timeA.compareTo(timeB) timeA > timeB; return 1; timeA = time B; return 0; timeA < timeB; return -1;
        boolean isAfter = currentTimeInString.compareTo(availableStartTime) >= 0;
        boolean isBefore = currentTimeInString.compareTo(availableEndTime) <= 0;

        //LOGGER.setLevel(Level.ALL);
        //LOGGER.log(Level.CONFIG, "{0} {1}", new Object[]{availableStartTime, availableEndTime});
        return isEnable && isAfter && isBefore;
    }

    public static Response makeCall(User caller, User callee, Contact contact, Datastore dsobj) throws IOException {
        LOGGER.info(contact.toString());
        Record cdr = new Record();
        cdr.setInitCall(caller, callee, contact);
        dsobj.save(cdr);
        InitPhoneCallBean ipcb = new InitPhoneCallBean();
        String callerPhone;
        String calleePhone;
        if (caller.getEnableMVPNChecker()) {
            callerPhone = checkMVPN(cdr.getCallerPhone()) ? 
                    changeStandardToMvpnFormat(cdr.getCallerPhone()) : cdr.getCallerPhone();
            calleePhone = checkMVPN(cdr.getCalleePhone()) ? 
                    changeStandardToMvpnFormat(cdr.getCalleePhone()) : cdr.getCalleePhone();
        }else{
            callerPhone = cdr.getCallerPhone();
            calleePhone = cdr.getCalleePhone();
        }
        
        
        ipcb.setCalleeNumber(calleePhone);
        ipcb.setCallerNumber(callerPhone);
        ipcb.setCallerid(Parameter.SIP_CALL_PREFIX + cdr.getId());
        ipcb.setHisuid(Parameter.SIP_CALL_PREFIX + cdr.getId());

        Http http = new Http();
        ObjectMapper mapper = new ObjectMapper();
        String reqStr;
        reqStr = mapper.writeValueAsString(ipcb);
        return http.postResponse(SIP_URL, reqStr);

    }

    public static boolean checkMVPN(String phone) {
        String caller = String.format("%s%smvpn/checkMvpnRequest/%s", Parameter.API_ROOT, Parameter.API_VER, phone);
        Http http = new Http();
        Headers headers = new Headers.Builder().add("apiKey", Parameter.API_KEY).build();
        Response res = null;
        try {
            res = http.getResponse(caller, headers);
            if (res.isSuccessful()) {
                String resStr = res.body().string();
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(resStr, MvpnResBean.class).isIsMvpn();
            }
        } catch (IOException ex) {
            Logger.getLogger(Helpers.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            if(res!=null){
                res.body().close();
            }
        }
         return false;
    }
    public static String changeStandardToMvpnFormat(String phone){
        return phone.replace("+8869", "*2809");
    }
    public static Response makeAsymmeticCall(User account, Icon icon, boolean isCallByAccount, Contact callerContact, Datastore dsobj) throws IOException {
        Record record = new Record();
        record.setAsymetricCall(account, icon, isCallByAccount, callerContact);
        dsobj.save(record);
        InitPhoneCallBean ipcb = new InitPhoneCallBean();
        String callerPhone;
        String calleePhone;
        
        if (account.getEnableMVPNChecker()) {
            callerPhone = checkMVPN(record.getCallerPhone()) ? 
                    changeStandardToMvpnFormat(record.getCallerPhone()) : record.getCallerPhone();
            calleePhone = checkMVPN(record.getCalleePhone()) ? 
                    changeStandardToMvpnFormat(record.getCalleePhone()) : record.getCalleePhone();
        }else{
            callerPhone = record.getCallerPhone();
            calleePhone = record.getCalleePhone();
        }
       
        ipcb.setCallerNumber(callerPhone);
        ipcb.setCalleeNumber(calleePhone);
        ipcb.setCallerid(Parameter.SIP_CALL_PREFIX + record.getId());
        ipcb.setHisuid(Parameter.SIP_CALL_PREFIX + record.getId());

        Http http = new Http();
        ObjectMapper mapper = new ObjectMapper();
        String reqStr;
        reqStr = mapper.writeValueAsString(ipcb);
        LOGGER.warning(reqStr);
        return http.postResponse(SIP_URL, reqStr);

    }

    public static boolean isAllowedToCall(Icon target) {
        String availableStartTime;
        String availableEndTime;
        boolean enable;
        //ConsoleHandler consoleHandler = new ConsoleHandler();
        //consoleHandler.setLevel(Level.CONFIG);
        //LOGGER.addHandler(consoleHandler);

        availableStartTime = target.getAvailableStartTime();
        availableEndTime = target.getAvailableEndTime();
        enable = target.getIsEnable();

        // Get current time.
        Date currentTimeStamp = new Date();
        // In 24 type.
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        String currentTimeInString = sdf.format(currentTimeStamp);

        // timeA.compareTo(timeB) timeA > timeB; return 1; timeA = time B; return 0; timeA < timeB; return -1;
        boolean isAfter = currentTimeInString.compareTo(availableStartTime) >= 0;
        boolean isBefore = currentTimeInString.compareTo(availableEndTime) <= 0;

        //LOGGER.setLevel(Level.ALL);
        //LOGGER.log(Level.CONFIG, "{0} {1}", new Object[]{availableStartTime, availableEndTime});
        return (enable & isAfter & isBefore);
    }

    public static Notification createNotificationInstance(String content, User owner, String contactId) {
        Notification notification = new Notification();
        notification.setUser(owner);
        notification.setNotificationContent(content);
        notification.setContactId(contactId);

        return notification;
    }

    public static void pushNotification(String content, String os, String deviceToken) throws IOException {
        if (os.equalsIgnoreCase("ios")) {
            // APNS Part
            ClassLoader classLoader = Helpers.class.getClassLoader();

            try {
                ApnsService service = null;

                if (!IS_SANDBOX) {
                    File productionFile = new File(classLoader.getResource("apn-key.p12").getFile());
                    service = APNS.newService()
                            .withCert(productionFile.getAbsolutePath(), "hsnl33564")
                            .withProductionDestination()
                            .build();
                } else {
                    File sandboxFile = new File(classLoader.getResource("apn-key-dev.p12").getFile());
                    service = APNS.newService()
                            .withCert(sandboxFile.getAbsolutePath(), "hsnl33564")
                            .withSandboxDestination()
                            .build();
                }

                String payload = APNS.newPayload().alertBody(content).badge(1).instantDeliveryOrSilentNotification().build();

                if (deviceToken != null && deviceToken.length() > 20) {
                    // deviceToken must be longer than 20.
                    service.push(deviceToken, payload);
                }

            } catch (RuntimeIOException | InvalidSSLConfig | NetworkIOException e) {
                System.out.print(e);
            }

        } else {
            // GCM Part.
            Http http = new Http();
            Headers headers = new Headers.Builder().add("Authorization", "key=AIzaSyAI-4ZELTey6llacUkj9-o99AOWjRFPKII").build();
            GcmPayloadBean payload = new GcmPayloadBean();
            payload.setTo(deviceToken);
            payload.getData().setMessage(content);
            ObjectMapper mapper = new ObjectMapper();
            String payloadStr = mapper.writeValueAsString(payload);
            LOGGER.warning(payloadStr);
            Response res = null;
            try{
                res = http.postResponse("https://gcm-http.googleapis.com/gcm/send", payloadStr, headers);
            }finally{
                if(res!=null){
                    LOGGER.log(Level.WARNING, "{0}", res.code());
                    res.body().close();
                }
            }   
        }
    }
}
