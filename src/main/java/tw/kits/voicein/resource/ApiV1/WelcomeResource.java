package tw.kits.voicein.resource.ApiV1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import tw.kits.voicein.util.TokenRequired;

@TokenRequired
@Path("/api/v1")
public class WelcomeResource {
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "<center>Voicein API Version: V1.<br/> 2016 built by Henry Chang and Calvin Jeng</center>";
        
    }
    
}
