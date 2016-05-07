package tw.kits.voicein.resource.ApiV2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/api/v2")
public class WelcomeResource {
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String index() {
        return "<center>VoiceIn API Version: V2.<br/> 2016 built by Henry Chang and Calvin Jeng</center>";
    }
    
}
