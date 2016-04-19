package tw.kits.voicein.model;

import java.util.Date;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author Calvin
 */
@Entity("notifications")
public class Notification {
    @Id
    private ObjectId id;
    @Reference
    private User user;
    private Date createdAt;
    private String notificationContent;
    private String contactId;
    
    public Notification() {
        this.createdAt = new Date();
    }

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
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the notificationContent
     */
    public String getNotificationContent() {
        return notificationContent;
    }

    /**
     * @param notificationContent the notificationContent to set
     */
    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    /**
     * @return the contactId
     */
    public String getContactId() {
        return contactId;
    }

    /**
     * @param contactId the contactId to set
     */
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}
