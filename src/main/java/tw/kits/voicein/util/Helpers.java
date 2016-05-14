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
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import okhttp3.Headers;
import okhttp3.Response;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.GcmPayloadBean;
import tw.kits.voicein.bean.InitPhoneCallBean;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.Record;
import tw.kits.voicein.model.User;

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
        ipcb.setCalleeNumber(cdr.getCalleePhone());
        ipcb.setCallerNumber(cdr.getCallerPhone());
        ipcb.setCallerid("vi$" + cdr.getId());
        ipcb.setHisuid("vi$" + cdr.getId());

        Http http = new Http();
        ObjectMapper mapper = new ObjectMapper();
        String reqStr;
        reqStr = mapper.writeValueAsString(ipcb);
        return http.postResponse(SIP_URL, reqStr);

    }

    public static Response makeAsymmeticCall(User account, Icon icon, boolean isCallByAccount, Contact callerContact, Datastore dsobj) throws IOException {
        Record record = new Record();
        record.setAsymetricCall(account, icon, isCallByAccount, callerContact);
        dsobj.save(record);
        InitPhoneCallBean ipcb = new InitPhoneCallBean();
        ipcb.setCalleeNumber(record.getCalleePhone());
        ipcb.setCallerNumber(record.getCallerPhone());
        ipcb.setCallerid("vi$" + record.getId());
        ipcb.setHisuid("vi$" + record.getId());

        Http http = new Http();
        ObjectMapper mapper = new ObjectMapper();
        String reqStr;
        reqStr = mapper.writeValueAsString(ipcb);
        return http.postResponse(SIP_URL, reqStr);

    }

    public static boolean isAllowedToCall(Icon target) {
        String availableStartTime;
        String availableEndTime;
        boolean enable;
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.CONFIG);
        LOGGER.addHandler(consoleHandler);

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

        LOGGER.setLevel(Level.ALL);
        LOGGER.log(Level.CONFIG, "{0} {1}", new Object[]{availableStartTime, availableEndTime});
        return (enable & isAfter & isBefore);
    }

    public static void pushNotification(String content, String os, String deviceToken) throws IOException {
        if (os.equalsIgnoreCase("ios")) {
            // Push notification to the caller.
            ClassLoader classLoader = Helpers.class.getClassLoader();
            File file = new File(classLoader.getResource("apn-key.p12").getFile());
            ApnsService service
                    = APNS.newService()
                    .withCert(file.getAbsolutePath(), "hsnl33564")
                    .withProductionDestination()
                    .build();

            String payload = APNS.newPayload().alertBody(content).build();
            String token = deviceToken;
            service.push(token, payload);
        } else {
            Http http = new Http();
            Headers headers = new Headers.Builder().add("Authorization", "key=AIzaSyD_SV6Nm12yXqGfIyU5Jt1qkihECJtUPbM").build();
            GcmPayloadBean payload = new GcmPayloadBean();
            payload.setTo(deviceToken);
            payload.getData().setMessage(content);
            ObjectMapper mapper = new ObjectMapper();
            String payloadStr = mapper.writeValueAsString(payload);
            LOGGER.warning(payloadStr);
            Response res = http.postResponse("https://gcm-http.googleapis.com/gcm/send", payloadStr, headers);
            LOGGER.warning(res.code() + "");

        }

    }
}
