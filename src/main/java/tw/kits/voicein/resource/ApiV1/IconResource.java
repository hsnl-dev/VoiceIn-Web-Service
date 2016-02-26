
package tw.kits.voicein.resource.ApiV1;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.IconCreateBean;
import tw.kits.voicein.bean.IconUpdateBean;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.MongoManager;

/***
 * this is for icon
 * @author Henry
 */
@Path("/api/v1")
public class IconResource {
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dsObj = mongoManager.getDs();
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/icons")
    public Response genIcon(@Valid @NotNull IconCreateBean icb){
        List<User> users = dsObj.createQuery(User.class).field("qrCodeUuid").equal(icb.getProviderUuid()).asList();
        if(users.size()!=1){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Icon icon = new Icon();
        icon.setIconId(UUID.randomUUID().toString());
        icon.setProvider(users.get(0));
        icon.setName(icb.getName());
        icon.setPhoneNumber(icb.getPhoneNumber());
        dsObj.save(icon);
        HashMap<String,String> res = new HashMap<String,String>();
        res.put("iconId", icon.getIconId());
        return Response.status(Response.Status.CREATED).entity(res).build();
        
    } 
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/icons/{iconId}")
    public Response updateIcon(@PathParam("iconId") String uuid, IconUpdateBean iub){
        Icon icon = dsObj.get(Icon.class, uuid);
        if(icon==null)
            return Response.status(Response.Status.NOT_FOUND).build();
        if(iub.getName()!=null){
            icon.setName(iub.getName());
        }
        if(iub.getPhoneNumber()!=null){
            icon.setPhoneNumber(iub.getPhoneNumber());
        }
        return Response.ok().build();
    }
}
