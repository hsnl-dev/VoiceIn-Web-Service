package tw.kits.voicein.resource.ApiV1;

import java.util.logging.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import tw.kits.voicein.util.MongoManager;
import org.mongodb.morphia.Datastore;

import tw.kits.voicein.model.User;
import tw.kits.voicein.model.Contact;

/**
 * Accounts Resource
 * @author Calvin
 */
@Path("/api/v1")
public class AccountsResource {
    static final Logger LOGGER = Logger.getLogger("AccountsDebugLogging");
    ConsoleHandler consoleHandler = new ConsoleHandler();
    
    /**
     * This API allows user to delete a user account by given uuid.
     * @param uuid
     * @return
     */
    @DELETE 
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUserAccount(@PathParam("uuid") String uuid) {
        MongoManager mongoManager = MongoManager.getInstatnce();
        Datastore dsObj = mongoManager.getDs();
        dsObj.delete(User.class, uuid);
        
        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.CONFIG);
        
        LOGGER.addHandler(consoleHandler);        
        LOGGER.log(Level.CONFIG, "[Config] Delete user u{0}", uuid);
        
        return Response.ok().build();
    }
    
    /**
     * This API allows client to update user's information.
     * @param uuid
     * @param u
     * @return response to the client
     */
    @PUT
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.APPLICATION_JSON)
    public Response updateUserAccount(@PathParam("uuid") String uuid, User u) {
        MongoManager mongoManager = MongoManager.getInstatnce();
        Datastore dsObj = mongoManager.getDs();
        
        u.setUuid(uuid);
        dsObj.save(u);
        
        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.ALL);
        
        LOGGER.addHandler(consoleHandler);        
        LOGGER.log(Level.CONFIG, "[Config] Update user u{0}", u);
                
        return Response.ok().build();
    }    
    
    /**
     * This API allows client to retrieve user's full informations.
     * @param uuid
     * @return User
     */
    @GET
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserAccount(@PathParam("uuid")String uuid) {
        MongoManager mongoManager = MongoManager.getInstatnce();
        Datastore dsObj = mongoManager.getDs();
        User user = dsObj.get(User.class, uuid);
        
        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.CONFIG);
        
        LOGGER.addHandler(consoleHandler);        
        LOGGER.log(Level.CONFIG, "[Config] Get user u{0}", uuid);
        
        return user;
    }
    
    /**
     *
     * @param uuid
     * @return response
     */
    @POST
    @Path("/accounts/{uuid}/calls")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response makePhoneCall(@PathParam("uuid") String uuid) {
        //TODO Call the phone API.
        return Response.status(Status.OK).build();
    }
    
    @GET
    @Path("/accounts/{uuid}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Contact getContactListOfAnUser(@PathParam("uuid") String uuid) {
        return new Contact();
    }
    
    @POST
    @Path("/accounts/{uuid}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewContactofAnUser(@PathParam("uuid") String uuid) {
        return Response.ok().build();
    }
    
}
