package tw.kits.voicein.resource.ApiV1;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.RandomStringUtils;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.TokenResBean;
import tw.kits.voicein.bean.UserAuthBean;
import tw.kits.voicein.bean.UserPhoneBean;
import tw.kits.voicein.model.VCodeModel;
import tw.kits.voicein.util.MongoManager;


@Path("/api/v1")
public class TokenResource {
    
    @POST
    @Path("/accounts/tokens")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response genToken(@Valid @NotNull UserAuthBean user){
        
     
    
       TokenResBean token = new TokenResBean("1234");
        
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
        Datastore ds = MongoManager.getInstatnce().getDs();
        ds.ensureIndexes();
        VCodeModel code = new VCodeModel(UUID.randomUUID(),
                RandomStringUtils.random(6,false,true),
                new Date(),
                3600,
                phone.getPhoneNumber());
        ds.save(code);
        HashMap<String,String> res = new HashMap<String,String> ();
        res.put("userUuid",code.getVcodeId().toString());
        return Response
                .status(Status.CREATED)
                .entity(res)
                .build();
        
    }
}
