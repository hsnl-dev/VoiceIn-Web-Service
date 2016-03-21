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
import javax.ws.rs.core.Response.Status;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.bean.IconCreateBean;
import tw.kits.voicein.bean.IconInfoBean;
import tw.kits.voicein.bean.IconUpdateBean;
import tw.kits.voicein.bean.ProviderResBean;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.ContactConstants;
import tw.kits.voicein.util.Helpers;
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

    /**
     * This API allows customer to add a icon. API By Henry
     *
     * @param icb
     * @return iconUuid
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/icons")
    public Response genIcon(@Valid @NotNull IconCreateBean icb) {
        QRcode code = dsObj.get(QRcode.class, icb.getProviderUuid());
        if (code == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        //set icon
        LOGGER.info("setting icon");
        Icon icon = new Icon();
        icon.setIconId(UUID.randomUUID().toString());
        icon.setProvider(code.getProvider());
        icon.setName(icb.getName());
        icon.setPhoneNumber(icb.getPhoneNumber());
        icon.setAvailableStartTime(icb.getCustomer().getAvailableStartTime());
        icon.setAvailableEndTime(icb.getCustomer().getAvailableEndTime());
        icon.setCompany(icb.getCustomer().getCompany());
        icon.setLocation(icb.getCustomer().getLocation());
        icon.setIsEnable(icb.getCustomer().getIsEnable() == null ? true : icb.getCustomer().getIsEnable());
        dsObj.save(icon);
        LOGGER.info("add to user contact");

        //provider!
        Contact contact = new Contact();
        contact.setCustomerIcon(icon);
        contact.setChargeType(ContactConstants.TYPE_ICON);
        contact.setIsEnable(true);
        contact.setIsHigherPriorityThanGlobal(false);
        contact.setAvailableEndTime("23:59");
        contact.setAvailableStartTime("00:00");
        dsObj.save(contact);
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("iconId", icon.getIconId());
        return Response.status(Response.Status.CREATED).entity(res).build();

    }

    /**
     * This API allows customer to update their information including phone
     * number and name. API By Henry
     *
     * @param uuid
     * @param iub
     * @return
     */
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
        icon.setAvailableEndTime(uuid);
        icon.setAvailableStartTime(uuid);
        icon.setCompany(uuid);
        icon.setIsEnable(Boolean.FALSE);
        icon.setLocation(uuid);
        dsObj.save(icon);
        return Response.ok().build();
    }

    /**
     * This API allows client to get provider 's information by
     * qrCodeUuid(providerId) API By Henry
     *
     * @param uProviderId
     * @return
     */
    @GET
    @Path("/providers/{providerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIconProvider(@PathParam("providerId") String uProviderId) {
        QRcode code = dsObj.get(QRcode.class, uProviderId);
        User user = code.getProvider();
        ProviderResBean prb = new ProviderResBean();
        prb.setName(user.getUserName());
        prb.setCompany(user.getCompany());
        prb.setLocation(user.getLocation());
        prb.setProfile(user.getProfile());
        prb.setAvatarId(user.getProfilePhotoId());
        prb.setCustomerName(code.getUserName());
        prb.setCustomerCompany(code.getCompany());
        prb.setCustomerLocation(code.getLocation());
        prb.setCustomerPhoneNum(code.getPhoneNumber());
        prb.setType(code.getType());
        prb.setState(code.getState());
        return Response.ok(prb).build();
    }

    /**
     * Get icon info!!!! 200 OK 404 NOT found!
     *
     * @param iconId
     * @return
     */
    @GET
    @Path("/icons/{iconId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIconInfo(@PathParam("iconId") String iconId) {
        Icon icon = dsObj.get(Icon.class, iconId);
        if (icon == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        List<Contact> contact = dsObj.createQuery(Contact.class).field("customerIcon").equal(icon).asList();
        if (contact.size()!=1) {
            return Response.status(Status.NOT_FOUND).entity(new ErrorMessageBean("contact is not found")).build();
        }
        return Response.ok(new IconInfoBean(icon,contact.get(0))).build();
    }

    /**
     * This API allows customer to call provider after confirm button click. API
     * By Henry
     *
     * @param iconId
     * @return
     * @throws IOException
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/icons/{iconId}/calls")
    public Response callAfterConfirm(@PathParam("iconId") String iconId) throws IOException {
        Icon icon = dsObj.get(Icon.class, iconId);
        if (icon == null) {
            ErrorMessageBean erb = new ErrorMessageBean("icon is not found");
            return Response.status(Status.NOT_FOUND).entity(erb).build();
        }
        List<Contact> target = dsObj.createQuery(Contact.class).field("customerIcon").equal(icon).asList();
        if (target.size() != 1) {
            ErrorMessageBean erb = new ErrorMessageBean("contact is not found");
            return Response.status(Status.NOT_FOUND).entity(erb).build();
        }
        if (!Helpers.isAllowedToCall(target.get(0))) {
            ErrorMessageBean erb = new ErrorMessageBean("Call is not allowed");
            return Response.status(Status.FORBIDDEN).entity(erb).build();
        }

        String endPoint = Parameter.API_ROOT + Parameter.API_VER + "Call/test01/generalCallRequest/";
        HashMap<String, Object> sendToObj = new HashMap<String, Object>();

        sendToObj.put("caller", icon.getPhoneNumber());
        sendToObj.put("callee", target.get(0).getUser().getPhoneNumber());
        sendToObj.put("check", false);
        LOGGER.info(String.format("Starting calling %s", endPoint));
        Http http = new Http();
        String json = new ObjectMapper().writeValueAsString(sendToObj);
        okhttp3.Response res = http.postResponse(endPoint, json);

        if (res.isSuccessful()) {
            return Response.status(Response.Status.CREATED).entity(res).build();
        } else {
            ErrorMessageBean erb = new ErrorMessageBean("Kits Main Server Error");
            LOGGER.severe(res.body().string());
            return Response.serverError().entity(erb).build();
        }

    }

}
