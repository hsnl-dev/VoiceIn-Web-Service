package tw.kits.voicein.models;

/**
 *
 * @author Calvin
 */
public class User {
    String uuid;
    String phoneNumber;
    String location;
    String profile;
    
    public User(String uuid, String phoneNumber) {
        this.uuid = uuid;
        this.phoneNumber = phoneNumber;
    }
    
}
