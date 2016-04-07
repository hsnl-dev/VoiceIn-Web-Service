package tw.kits.voicein.model;
import java.util.ArrayList;
import javax.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
/**
 *
 * @author Calvin
 */
@Entity("groups")
public class Group {
    @Id
    private ObjectId id;
    @Reference
    private User user;
    @NotNull
    private ArrayList<String> contacts;
    @NotNull
    private String groupName;
    
    /**
     * @return the id
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * @return the contacts
     */
    public ArrayList<String> getContacts() {
        return contacts;
    }

    /**
     * @param contacts the contacts to set
     */
    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
}
