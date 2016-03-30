/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.resource.ApiV1;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.TokenRequired;

@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v1")
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
     * =========== This is depreciated! ===========
     * @param uuid
     * @return
     */
    @GET
    @Path("/accounts/{uuid}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getContactListOfAnUser(@PathParam("uuid") String uuid) {
        User user = dataStoreObject.get(User.class, uuid);

        List<Contact> contactList = dataStoreObject.find(Contact.class).field("user").equal(user).asList();

        LOGGER.log(Level.CONFIG, "Contact Length {0}", contactList.size());

        List<UserContactBean> userList = new ArrayList();
        UserContactBean userContactBean;

        for (Contact contact : contactList) {
            userContactBean = new UserContactBean();
            User provider = contact.getProviderUser();
            Icon icon = contact.getCustomerIcon();
            userContactBean.setId(userContactBean.getId());
            if (provider != null) {
                Contact providerContact = dataStoreObject.find(Contact.class).filter("user =", provider).filter("qrCodeUuid =", contact.getQrCodeUuid()).get();
                userContactBean.setCompany(provider.getCompany());
                userContactBean.setUserName(provider.getUserName());
                userContactBean.setLocation(provider.getLocation());
                userContactBean.setCompany(provider.getCompany());
                userContactBean.setProfile(provider.getProfile());
                userContactBean.setPhoneNumber(provider.getPhoneNumber());
                LOGGER.log(Level.CONFIG, "Provider is available {0}", Helpers.isAllowedToCall(providerContact));
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
                userContactBean.setCompany(icon.getCompany());
                userContactBean.setPhoneNumber(icon.getPhoneNumber());
                LOGGER.log(Level.CONFIG, "Icon is available {0}", Helpers.isAllowedToCall(icon));
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
            userContactBean.setIsHigherPriorityThanGlobal(contact.getIsHigherPriorityThanGlobal());

            userList.add(userContactBean);
        }

        return Response.ok(userList).build();
    }

    /**
     * This API allows user to add a contact. API By Calvin
     *
     * @param uuid
     * @param qrCodeUuid
     * @param contact
     * @return
     */
    @POST
    @Path("/accounts/{uuid}/contacts/{qrCodeUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response createNewContactOfAnUser(@PathParam("uuid") String uuid, @PathParam("qrCodeUuid") String qrCodeUuid, @NotNull @Valid Contact contact) {
        User owner = dataStoreObject.get(User.class, uuid);
        List<User> providers = dataStoreObject.createQuery(User.class).field("qrCodeUuid").equal(qrCodeUuid).asList();

        LOGGER.log(Level.CONFIG, "Save A Contact.");

        List<Contact> contacts = dataStoreObject.createQuery(Contact.class).filter("qrCodeUuid =", qrCodeUuid).filter("user =", owner).asList();

        if (contacts.size() > 0) {
            // Owner has already add the provider as friend.
            return Response.notModified().build();
        }

        // user.size() must be 1.
        if (providers.size() == 1) {
            User provider = providers.get(0);
            if (contact.getChargeType() != 0) {
                // the contact of the scanner side.
                contact.setUser(owner);
                contact.setProviderUser(provider);
                contact.setQrCodeUuid(qrCodeUuid);
                contact.setIsEnable(true);
                contact.setChargeType(1);
                contact.setAvailableStartTime("00:00");
                contact.setAvailableEndTime("23:59");
                dataStoreObject.save(contact);

                // the contact of the provider side.
                contact.setId(new ObjectId());
                contact.setUser(provider);
                contact.setProviderUser(owner);
                contact.setNickName("");
                contact.setChargeType(2);

                dataStoreObject.save(contact);
            } else {
                // icon
                contact.setUser(provider);
                contact.setProviderUser(owner);
                contact.setQrCodeUuid(qrCodeUuid);
                contact.setIsEnable(true);
                contact.setChargeType(0);
                dataStoreObject.save(contact);
            }

            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

    /**
     * This API allows client to delete a contact. API By Calvin
     * =========== This is depreciated! ===========
     * @param uuid
     * @param qrCodeUuid
     * @return
     */
    @DELETE
    @Path("/accounts/{uuid}/contacts/{qrCodeUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response deleteAcontactOfAnUser(@PathParam("uuid") String uuid, @PathParam("qrCodeUuid") String qrCodeUuid) {
        User user = dataStoreObject.get(User.class, uuid);

        Contact payContact = dataStoreObject.createQuery(Contact.class).filter("qrCodeUuid =", qrCodeUuid).filter("user =", user).get();

        User provider = payContact.getProviderUser();
        Contact freeContact = dataStoreObject.createQuery(Contact.class).filter("qrCodeUuid =", qrCodeUuid).filter("user =", provider).get();

        dataStoreObject.delete(Contact.class, payContact.getId());
        dataStoreObject.delete(Contact.class, freeContact.getId());
        return Response.ok().build();
    }
}
