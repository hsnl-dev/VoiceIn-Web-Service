package tw.kits.voicein.resource.ApiV2;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.UserContactBean;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.ContactConstants;
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.TokenRequired;

@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v2")
public class AccountContactsResource {

    @Context
    SecurityContext mContext;
    static final Logger LOGGER = Logger.getLogger(AccountContactsResource.class.getName());
    ConsoleHandler consoleHandler = new ConsoleHandler();
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dataStoreObject = mongoManager.getDs();

    private void initLogger() {
        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.CONFIG);
        LOGGER.addHandler(consoleHandler);
    }

    /**
     * This API allows user to get their contact list. API By Calvin
     *
     * @param uuid
     * @param filter
     * @return
     */
    @GET
    @Path("/accounts/{uuid}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getContactListOfAnUser(
            @PathParam("uuid") String uuid,
            @QueryParam("filter") String filter
    ) {
        User user = dataStoreObject.get(User.class, uuid);

        // Return the contact you like or not.
        List<Contact> contactList = null;

        if (filter != null) {
            switch (filter) {
                case "like":
                    contactList = dataStoreObject.find(Contact.class).field("user").equal(user).field("isLike").equal(Boolean.TRUE).asList();
                    break;
                default:
                    contactList = dataStoreObject.find(Contact.class).field("user").equal(user).asList();

            }
        } else {
            contactList = dataStoreObject.find(Contact.class).field("user").equal(user).asList();
        }

        initLogger();

        LOGGER.log(Level.CONFIG, "Contact Length {0}", contactList.size());

        List<UserContactBean> userList = new ArrayList();
        UserContactBean userContactBean;

        for (Contact contact : contactList) {
            userContactBean = new UserContactBean();
            User provider = contact.getProviderUser();
            Icon icon = contact.getCustomerIcon();

            if (provider != null) {
                Contact providerContact = dataStoreObject.find(Contact.class).filter("user =", provider).filter("qrCodeUuid =", contact.getQrCodeUuid()).get();
                userContactBean.setCompany(provider.getCompany());
                userContactBean.setUserName(provider.getUserName());
                userContactBean.setLocation(provider.getLocation());
                userContactBean.setCompany(provider.getCompany());
                userContactBean.setProfile(provider.getProfile());
                userContactBean.setPhoneNumber(provider.getPhoneNumber());
                LOGGER.log(Level.CONFIG, "Provider is available: {0}", Helpers.isAllowedToCall(providerContact));
                userContactBean.setProviderIsEnable(Helpers.isAllowedToCall(providerContact));
                userContactBean.setProfilePhotoId(provider.getProfilePhotoId());
                if (providerContact.getIsHigherPriorityThanGlobal()) {
                    userContactBean.setProviderAvailableEndTime(providerContact.getAvailableEndTime());
                    userContactBean.setProviderAvailableStartTime(providerContact.getAvailableStartTime());
                } else {
                    userContactBean.setProviderAvailableEndTime(provider.getAvailableEndTime());
                    userContactBean.setProviderAvailableStartTime(provider.getAvailableStartTime());
                }

            } else if (icon != null) {
                userContactBean.setCompany(icon.getCompany());
                userContactBean.setUserName(icon.getName());
                userContactBean.setLocation(icon.getLocation());
                userContactBean.setPhoneNumber(icon.getPhoneNumber());

                LOGGER.log(Level.CONFIG, "Icon is available: {0}", Helpers.isAllowedToCall(icon));
                userContactBean.setProviderIsEnable(Helpers.isAllowedToCall(icon));
                userContactBean.setProviderAvailableEndTime(icon.getAvailableEndTime());
                userContactBean.setProviderAvailableStartTime(icon.getAvailableStartTime());
            }

            userContactBean.setAvailableEndTime(contact.getAvailableEndTime());
            userContactBean.setAvailableStartTime(contact.getAvailableStartTime());
            userContactBean.setChargeType(contact.getChargeType());
            userContactBean.setIsEnable(contact.getIsEnable());
            userContactBean.setCustomerIcon(contact.getCustomerIcon());
            userContactBean.setNickName(contact.getNickName());
            userContactBean.setQrCodeUuid(contact.getQrCodeUuid());
            userContactBean.setIsLike(contact.getIsLike());
            
            // return unique object id
            userContactBean.setId(contact.getId().toString());
            userContactBean.setIsHigherPriorityThanGlobal(contact.getIsHigherPriorityThanGlobal());

            userList.add(userContactBean);
        }

        return Response.ok(userList).build();
    }

    /**
     * This API allows client user to update a contact. API By Calvin
     *
     * @param contactId
     * @param nickName
     * @param availableStartTime
     * @param availableEndTime
     * @param isEnable
     * @param isHigherPriorityThanGlobal
     * @param like
     * @return
     */
    @PUT
    @Path("/accounts/{contactId}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response updateAcontactOfAnUser(
            @PathParam("contactId") String contactId,
            @QueryParam("nickName") String nickName,
            @QueryParam("availableStartTime") String availableStartTime,
            @QueryParam("availableEndTime") String availableEndTime,
            @QueryParam("isEnable") String isEnable,
            @QueryParam("isHigherPriorityThanGlobal") String isHigherPriorityThanGlobal,
            @QueryParam("like") String like
    ) {
        Contact modifiedContact = dataStoreObject.get(Contact.class, new ObjectId(contactId));

        initLogger();

        LOGGER.log(Level.CONFIG, "Save A Contact.{0}", nickName);

        if (nickName != null) {
            modifiedContact.setNickName(nickName);
        }

        if (isEnable != null) {
            modifiedContact.setIsEnable(Boolean.parseBoolean(isEnable));
        }

        if (isHigherPriorityThanGlobal != null) {
            modifiedContact.setIsHigherPriorityThanGlobal(Boolean.parseBoolean(isHigherPriorityThanGlobal));
        }

        if (availableStartTime != null) {
            modifiedContact.setAvailableStartTime(availableStartTime);
        }

        if (availableEndTime != null) {
            modifiedContact.setAvailableEndTime(availableEndTime);
        }

        if (like != null) {
            modifiedContact.setIsLike(Boolean.parseBoolean(like));
        }

        dataStoreObject.save(modifiedContact);
        return Response.ok().build();
    }

    /**
     * This API allows client to delete a contact. API By Calvin
     *
     * @param contactId
     * @return
     */
    @DELETE
    @Path("/accounts/{contactId}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response deleteAcontactOfAnUser(@PathParam("contactId") String contactId) {
        Contact payContact = dataStoreObject.get(Contact.class, new ObjectId(contactId));

        User provider = payContact.getProviderUser();
        String qrCodeUuid = payContact.getQrCodeUuid();
        if (payContact.getChargeType() != ContactConstants.TYPE_ICON) {
            Contact freeContact = dataStoreObject.createQuery(Contact.class).filter("qrCodeUuid =", qrCodeUuid).filter("user =", provider).get();
            dataStoreObject.delete(freeContact);
        } else {
            dataStoreObject.delete(payContact.getCustomerIcon());
        }

        dataStoreObject.delete(payContact);

        return Response.ok().build();
    }
}
