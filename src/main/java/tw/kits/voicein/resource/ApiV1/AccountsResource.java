package tw.kits.voicein.resource.ApiV1;

import java.util.List;
import java.util.logging.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import tw.kits.voicein.models.User;
import tw.kits.voicein.util.MongoManager;
import org.mongodb.morphia.Datastore;
import javax.ws.rs.core.Response.Status;
import org.mongodb.morphia.query.Query;

/**
 *
 * @author Calvin
 */
@Path("/api/v1")
public class AccountsResource {
    static final Logger LOGGER = Logger.getLogger("AccountsDebugLogging");
    ConsoleHandler consoleHandler = new ConsoleHandler();
          
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
        LOGGER.log(Level.CONFIG, "[Config] u{0}", u);
                
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
        LOGGER.log(Level.CONFIG, "[Config] uuid u{0}", uuid);
        
        return user;
    }
}
