package tw.kits.voicein.resource.ApiV1;

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
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.constant.ContactConstant;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.QRcodeType;

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
    private static final Logger LOGGER = Logger.getLogger(IconResource.class.getName());

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
        if (contact.size() != 1) {
            return Response.status(Status.NOT_FOUND).entity(new ErrorMessageBean("contact is not found")).build();
        }
        
        return Response.ok(new IconInfoBean(icon, contact.get(0))).build();
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
        icon.setAvailableEndTime(iub.getAvailableEndTime());
        icon.setAvailableStartTime(iub.getAvailableStartTime());
        icon.setCompany(iub.getCompany());
        icon.setIsEnable(iub.getIsEnable());
        icon.setLocation(iub.getLocation());
        dsObj.save(icon);
        return Response.ok().build();
    }

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
        Icon icon = null;
        if (code.getType().equals(QRcodeType.TYPE_SPECIAL)) {
            List<Icon> existedIcons = dsObj.createQuery(Icon.class).field("qrCodeId").equal(code.getId()).asList();
            if (existedIcons.size() > 0) {
                icon = existedIcons.get(0);
                saveNewIcon(icon, code, icb);
            } else {
                icon = new Icon();
                icon.setIconId(UUID.randomUUID().toString());
                saveNewIcon(icon, code, icb);
                linkNewContact(code, icon);
            }
        } else {
            icon = new Icon();
            icon.setIconId(UUID.randomUUID().toString());
            saveNewIcon(icon, code, icb);
            linkNewContact(code, icon);
        }

        LOGGER.info("add to user contact");
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("iconId", icon.getIconId());
        return Response.status(Response.Status.CREATED).entity(res).build();

    }

    /**
     * save icon
     *
     * @param icon
     * @param code
     * @param icb
     */
    private void saveNewIcon(Icon icon, QRcode code, IconCreateBean icb) {
        icon.setQrCodeId(code.getId());
        icon.setProvider(code.getProvider());
        icon.setName(icb.getName());
        icon.setPhoneNumber(icb.getPhoneNumber());
        icon.setAvailableStartTime(icb.getCustomer().getAvailableStartTime());
        icon.setAvailableEndTime(icb.getCustomer().getAvailableEndTime());
        icon.setCompany(icb.getCustomer().getCompany());
        icon.setLocation(icb.getCustomer().getLocation());
        icon.setIsEnable(icb.getCustomer().getIsEnable() == null ? true : icb.getCustomer().getIsEnable());
        dsObj.save(icon);
    }

    /**
     * *
     * link provider contact to icon
     *
     * @param code
     * @param icon
     * @return
     */
    private Contact linkNewContact(QRcode code, Icon icon) {
        //provider!
        Contact contact = new Contact();
        contact.setUser(code.getProvider());
        contact.setCustomerIcon(icon);
        contact.setChargeType(ContactConstant.TYPE_ICON);
        contact.setIsEnable(true);
        contact.setIsHigherPriorityThanGlobal(false);
        contact.setAvailableEndTime("23:59");
        contact.setAvailableStartTime("00:00");
        dsObj.save(contact);
        return contact;
    }
}
