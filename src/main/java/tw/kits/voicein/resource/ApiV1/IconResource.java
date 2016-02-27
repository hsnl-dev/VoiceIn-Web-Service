package tw.kits.voicein.resource.ApiV1;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.bean.IconCreateBean;
import tw.kits.voicein.bean.IconUpdateBean;
import tw.kits.voicein.bean.ProviderResBean;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.Http;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.Parameter;

/**
 * *
 * this is for icon
 *
 * @author Henry
 */
@Path("/api/v1")
public class IconResource {

    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dsObj = mongoManager.getDs();
    private final Logger LOGGER = Logger.getLogger(IconResource.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/icons")
    public Response genIcon(@Valid @NotNull IconCreateBean icb) {
        List<User> users = dsObj.createQuery(User.class).field("qrCodeUuid").equal(icb.getProviderUuid()).asList();
        if (users.size() != 1) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        //set icon
        LOGGER.info("setting icon");
        Icon icon = new Icon();
        icon.setIconId(UUID.randomUUID().toString());
        icon.setProvider(users.get(0));
        icon.setName(icb.getName());
        icon.setPhoneNumber(icb.getPhoneNumber());
        dsObj.save(icon);
        LOGGER.info("add to user contact");
        Contact contact = new Contact();
        contact.setUser(users.get(0));
        contact.setCustomerIcon(icon);
        dsObj.save(contact);
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("iconId", icon.getIconId());
        return Response.status(Response.Status.CREATED).entity(res).build();

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/icons/{iconId}")
    public Response updateIcon(@PathParam("iconId") String uuid, IconUpdateBean iub) {
        Icon icon = dsObj.get(Icon.class, uuid);
        if (icon == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (iub.getName() != null) {
            icon.setName(iub.getName());
        }
        if (iub.getPhoneNumber() != null) {
            icon.setPhoneNumber(iub.getPhoneNumber());
        }
        return Response.ok().build();
    }

    @GET
    @Path("/providers/{providerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIconProvider(@PathParam("providerId") String uProviderId) {
        List<User> users = dsObj.createQuery(User.class).field("qrCodeUuid").equal(uProviderId).asList();
        if (users.size() != 1) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        User user = users.get(0);
        ProviderResBean prb = new ProviderResBean();
        prb.setName(user.getUserName());
        prb.setCompany(user.getCompany());
        prb.setLocation(user.getLocation());
        prb.setProfile(user.getProfile());
        prb.setAvatarId(user.getProfilePhotoId());
        return Response.ok(prb).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/icons/{iconId}")
    public Response genIcon(@PathParam("iconId") String iconId) throws IOException {
        Icon icon = dsObj.get(Icon.class, iconId);
        String endPoint = Parameter.API_ROOT + Parameter.API_VER + "Call/test01/generalCallRequest/";
        HashMap<String, Object> sendToObj = new HashMap<String, Object>();
        sendToObj.put("callee", icon.getPhoneNumber());
        sendToObj.put("caller", icon.getProvider().getPhoneNumber());
        sendToObj.put("check", false);
        LOGGER.info(String.format("Starting calling %s",endPoint));
        Http http = new Http();
        String json =  new ObjectMapper().writeValueAsString(sendToObj);
        okhttp3.Response res = http.postResponse(endPoint, json);
        if(res.isSuccessful()){
            return Response.status(Response.Status.CREATED).entity(res).build();
        }else{
            ErrorMessageBean erb = new ErrorMessageBean("Kits Main Server Error");
            LOGGER.severe(res.body().string());
            return Response.serverError().entity(erb).build();
        }
        

    }

}