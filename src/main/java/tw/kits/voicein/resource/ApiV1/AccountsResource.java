package tw.kits.voicein.resource.ApiV1;

import java.util.logging.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import tw.kits.voicein.models.User;
import tw.kits.voicein.util.MongoManager;
import org.mongodb.morphia.Datastore;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Calvin
 */
@Path("/api/v1")
public class AccountsResource {
    static final Logger LOGGER = Logger.getLogger("AccountsDebugLogging");
    ConsoleHandler consoleHandler = new ConsoleHandler();
    
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
        
        return Response.status(Status.OK).build();
    }
    
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
                
        return Response.status(Status.OK).build();
    }    
    
    /**
     *
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
}
