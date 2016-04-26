/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author Henry
 */
public class PasswordHelper {

    private static final String HASH_ALGO = "sha-256";

    public static boolean isValidPassword(String passwordBeforeHash, String hashedPassword) {
      String compareTarget = getHashedString(passwordBeforeHash);
        if(compareTarget==null)
            return false;
        else{
            return compareTarget.equals(hashedPassword);
        }
        
       
    }

    public static String getHashedString(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("sha-256");
            md.update(password.getBytes());
            String idont =  Hex.encodeHexString( md.digest() );
            return idont;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PasswordHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }
}
