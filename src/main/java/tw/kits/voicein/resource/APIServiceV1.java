package tw.kits.voicein.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v1")
public class APIServiceV1 {

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "API version: v1.";
    }
    
}
