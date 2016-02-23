package tw.kits.voicein.resource.ApiV1;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import tw.kits.voicein.bean.TokenBean;
import tw.kits.voicein.bean.UserAuthBean;
import tw.kits.voicein.bean.UserPhoneBean;


@Path("/api/v1")
public class TokenResource {
    
    @POST
    @Path("/accounts/tokens")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response genToken(@Valid @NotNull UserAuthBean user){
        
     
    
       TokenBean token = new TokenBean();
        token.setToken("1234");
        return Response
                .status(Status.CREATED)
                .entity(token)
                .build();
        
    }
    @POST
    @Path("/accounts/validations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response genVerification(@Valid @NotNull UserPhoneBean phone){
        //TODO: send verfiy code
        
     
    
        TokenBean token = new TokenBean();
        token.setToken("1234");
        return Response
                .status(Status.CREATED)
                .entity(token)
                .build();
        
    }
}
