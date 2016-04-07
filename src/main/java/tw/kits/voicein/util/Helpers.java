/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import okhttp3.Response;
import org.mongodb.morphia.Datastore;
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
        Phonenumber.PhoneNumber phone = util.parse(phoneNumber, "ZZ");

        return util.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164);

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
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.CONFIG);
        LOGGER.addHandler(consoleHandler);

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

        LOGGER.setLevel(Level.ALL);
        LOGGER.log(Level.CONFIG, "{0} {1}", new Object[]{availableStartTime, availableEndTime});

        if (isEnable) {
            // If the contact is isEnable, check the available time.
            LOGGER.log(Level.CONFIG, "{0}-{1}", new Object[]{isAfter, isAfter});
            return isAfter && isBefore;
        } else {
            // If the contact is Disable, the call is not allowed.
            LOGGER.log(Level.CONFIG, "{0}", isEnable);
            return isEnable;
        }
    }

    public static Response makeCall(String caller, String callee, Contact contact, Datastore dsobj) throws IOException {
        LOGGER.info(contact.toString());
        Record cdr = new Record();
        cdr.setInitCall(caller, callee);
        cdr.setIsViaIcon(false);
        cdr.setViaContact(contact);
        dsobj.save(cdr);
        InitPhoneCallBean ipcb = new InitPhoneCallBean();
        ipcb.setCalleeNumber(callee);
        ipcb.setCallerNumber(caller);
        ipcb.setCallerid("vi$" + cdr.getId());
        ipcb.setHisuid("vi$" + cdr.getId());

        Http http = new Http();
        ObjectMapper mapper = new ObjectMapper();
        String reqStr;
        reqStr = mapper.writeValueAsString(ipcb);
        return http.postResponse(SIP_URL, reqStr);

    }
    public static Response makeCall(String caller, String callee, Icon icon, Datastore dsobj) throws IOException {
        
        Record cdr = new Record();
        cdr.setInitCall(caller, callee);
        cdr.setIsViaIcon(true);
        cdr.setViaIcon(icon);
        dsobj.save(cdr);
        
        InitPhoneCallBean ipcb = new InitPhoneCallBean();
        ipcb.setCalleeNumber(callee);
        ipcb.setCallerNumber(caller);
        ipcb.setCallerid("vi$" + cdr.getId());
        ipcb.setHisuid("vi$" + cdr.getId());

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

}
