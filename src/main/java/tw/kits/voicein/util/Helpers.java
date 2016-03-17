/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.User;
    
/**
 *
 * @author Henry
 */
public class Helpers {

    static final Logger LOGGER = Logger.getLogger(Helpers.class.getName());

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
        String currentTimeInString = sdf.format(currentTimeStamp);

        boolean isAfter = currentTimeInString.compareTo(availableStartTime) >= 0;
        boolean isBefore = currentTimeInString.compareTo(availableEndTime) < 0;
        
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.CONFIG);
        LOGGER.addHandler(consoleHandler);    
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

}
