package tw.kits.voicein.resource.ApiV2;

import java.util.List;
import java.util.logging.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import tw.kits.voicein.util.MongoManager;
import org.mongodb.morphia.Datastore;

import tw.kits.voicein.model.User;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.bson.types.ObjectId;
import tw.kits.voicein.model.Notification;
import tw.kits.voicein.util.TokenRequired;
/**
 *
 * @author Calvin
 */
@Path("/api/v2")
public class NotificationResource {
   @Context
    SecurityContext context;
    static final Logger LOGGER = Logger.getLogger(NotificationResource.class.getName());
    ConsoleHandler consoleHandler = new ConsoleHandler();
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dataStoreObject = mongoManager.getDs();

    /**
     * Get all notifications of an user.
     * 
     * @param uuid
     * @return User
     */
    @GET
    @Path("/accounts/{uuid}/notifications")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getUserAccount(@PathParam("uuid") String uuid) {
        User user = dataStoreObject.get(User.class, uuid);
        List<Notification> notifications = dataStoreObject.createQuery(Notification.class).field("user").equal(user).asList();
        
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            LOGGER.log(Level.CONFIG, "Get User u{0}", uuid);
            return Response.ok(notifications).build();
        }
    }

    /**
     *
     * @param uuid
     * @param notificationUuid
     * @return
     */
    @DELETE
    @Path("/accounts/{uuid}/notifications/{notificationUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response deleteUserAccount(@PathParam("uuid") String uuid, @PathParam("notificationUuid") String notificationUuid) {
        dataStoreObject.delete(Notification.class, new ObjectId(notificationUuid));
        LOGGER.log(Level.CONFIG, "Delete notification u{0}", notificationUuid);
        return Response.ok().build();
    }

    
}
