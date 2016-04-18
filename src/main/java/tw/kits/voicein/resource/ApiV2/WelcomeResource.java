package tw.kits.voicein.resource.ApiV2;

import tw.kits.voicein.resource.ApiV1.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import tw.kits.voicein.util.TokenRequired;


@Path("/api/v1")
public class WelcomeResource {
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public String index() {
        return "<center>VoiceIn API Version: V1.<br/> 2016 built by Henry Chang and Calvin Jeng</center>";
    }
    
}
