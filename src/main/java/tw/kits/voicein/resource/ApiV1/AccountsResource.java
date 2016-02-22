package tw.kits.voicein.resource.ApiV1;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import tw.kits.voicein.models.User;
/**
 *
 * @author Calvin
 */
@Path("/api/v1")
public class AccountsResource {
    
    static final String MONGO_URI = "mongodb://hsnl-dev:hsnl33564hsnl33564@ds013908.mongolab.com:13908/voicein";

    @POST
    @Path("/accounts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User createUserAccount() {
       User u = new User("xedx-2dcf-sddd-sdsd", "0988779570");
        
       return u;
    }    
}
