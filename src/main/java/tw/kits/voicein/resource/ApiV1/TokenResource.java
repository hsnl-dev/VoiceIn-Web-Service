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
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import tw.kits.voicein.bean.TokenResBean;
import tw.kits.voicein.bean.UserAuthBean;
import tw.kits.voicein.bean.UserPhoneBean;
import tw.kits.voicein.model.TokenModel;
import tw.kits.voicein.model.User;
import tw.kits.voicein.model.VCodeModel;
import tw.kits.voicein.util.MongoManager;


@Path("/api/v1")
public class TokenResource {
    @POST
    @Path("/accounts/validations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response genVerification(@Valid @NotNull UserPhoneBean phone){
        //TODO: send verfiy code
        Datastore ds = MongoManager.getInstatnce().getDs();
        ds.ensureIndexes();
        Query<User> q = ds.createQuery(User.class);
        
        User u = q.field("phoneNumber").equal(phone.getPhoneNumber()).get();
        if(u==null){
            u = new User();
            u.setUuid(UUID.randomUUID().toString());
            u.setPhoneNumber(phone.getPhoneNumber());
        }
        VCodeModel code = new VCodeModel(
                u,
                RandomStringUtils.random(6,false,true),
                new Date(),
                3600);
        ds.save(u);
        ds.save(code);
        HashMap<String,String> res = new HashMap<String,String> ();
        res.put("userUuid",u.getUuid().toString());
        return Response
                .status(Status.CREATED)
                .entity(res)
                .build();
        
    }
    @POST
    @Path("/accounts/tokens")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(@Valid @NotNull UserAuthBean user){
        System.out.print(user.getUserUuid());
        Datastore ds = MongoManager.getInstatnce().getDs();
        Key key = new Key(User.class,"accounts",user.getUserUuid());
        VCodeModel code = ds.find(VCodeModel.class).field("user").equal(key).get();
        
       if(code!=null){
           if (code.getCode().equals(user.getCode())){
               TokenModel tm = new TokenModel(3600);
               ds.save(tm);
               TokenResBean res = new TokenResBean(tm.getTokenId());
               return Response
                       .status(Status.CREATED)
                       .entity(res)
                       .build();   
           }
       }
       
        return Response
                .status(Status.UNAUTHORIZED)
                .entity(user)
                .build();
      
    }
}
