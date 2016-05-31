package tw.kits.voicein.resource.ApiV2.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.mongodb.morphia.query.Query;
import tw.kits.voicein.bean.UserContactBean;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.User;
import tw.kits.voicein.constant.ContactConstant;
import tw.kits.voicein.model.Group;
import tw.kits.voicein.model.Notification;
import tw.kits.voicein.model.Record;
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.TokenRequired;

@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v2")
public class ContactsResource {

    @Context
    SecurityContext mContext;
    static final Logger LOGGER = Logger.getLogger(ContactsResource.class.getName());
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
     * @param conditional
     * @return
     */
    @GET
    @Path("/accounts/{uuid}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getContactListOfAnUser(
            @PathParam("uuid") String uuid,
            @QueryParam("filter") String filter,
            @QueryParam("conditional") String conditional
    ) {
        User user = dataStoreObject.get(User.class, uuid);
        UserContactBean userContactBean;
        List<UserContactBean> userList = new ArrayList();
        // Return the contact you like or not.
        List<Contact> contactList = null;

        if (filter != null) {
            switch (filter) {
                case "like":
                    contactList = dataStoreObject.find(Contact.class).field("user").equal(user).field("isLike").equal(Boolean.TRUE).asList();
                    break;
                default:
                    if (conditional == null || "false".equalsIgnoreCase(conditional)) {
                        contactList = dataStoreObject.find(Contact.class).field("user").equal(user).asList();
                    } else {
                        Date lastContactGet = user.getLastContactGet();
                        contactList = dataStoreObject.find(Contact.class).field("user").equal(user).filter("updateAt >=", lastContactGet).asList();
                    }

                    user.setLastContactGet(new Date());
                    dataStoreObject.save(user);
            }
        } else {
            if (conditional == null || "false".equalsIgnoreCase(conditional)) {
                contactList = dataStoreObject.find(Contact.class).field("user").equal(user).asList();
            } else {
                Date lastContactGet = user.getLastContactGet();

                List<Contact> allContactList = dataStoreObject.find(Contact.class)
                        .field("user").equal(user)
                        .asList();
                
                 for (Contact c : allContactList) {
                     Date profileUpdateTime = c.getProviderUser().getProfilePhotoLastModifiedTime();
                     if (profileUpdateTime.getTime() >= c.getUpdateAt().getTime()) {
                         c.setUpdateAt(profileUpdateTime);
                         dataStoreObject.save(c);
                     } 
                 }
                 
                 contactList = dataStoreObject.find(Contact.class)
                        .field("user").equal(user)
                        .filter("updateAt >=", lastContactGet)
                        .asList();
            }

            ArrayList<String> deletedQueue = user.getDeletedQueue();

            if (deletedQueue != null) {
                for (String deletedContactId : deletedQueue) {
                    userContactBean = new UserContactBean();
                    userContactBean.setId(deletedContactId);
                    userList.add(userContactBean);
                }
                user.setDeletedQueue(new ArrayList());
            }

            user.setLastContactGet(new Date());
            dataStoreObject.save(user);
        }

        initLogger();

        LOGGER.log(Level.CONFIG, "Contact Length {0}", contactList.size());

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
                userContactBean.setEmail(provider.getEmail());
                userContactBean.setJobTitle(provider.getJobTitle());

                //LOGGER.log(Level.CONFIG, "Provider is available: {0}", Helpers.isAllowedToCall(providerContact));
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
                userContactBean.setEmail("尚未設定");
                userContactBean.setJobTitle("尚未設定");
                //LOGGER.log(Level.CONFIG, "Icon is available: {0}", Helpers.isAllowedToCall(icon));
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
    public Response createNewContactOfAnUser(@PathParam("uuid") String uuid, @PathParam("qrCodeUuid") String qrCodeUuid, @NotNull @Valid Contact contact) throws IOException {
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

            // Create notifications.
            Notification notification = new Notification();
            notification.setUser(provider);
            notification.setNotificationContent(owner.getUserName() + " 已經加入您為聯絡人");
            notification.setContactId(contact.getId().toString());
            dataStoreObject.save(notification);

            if ("ios".equalsIgnoreCase(provider.getDeviceOS())) {
                Helpers.pushNotification(owner.getUserName() + " 已經加入您為聯絡人", "ios", provider.getDeviceKey());
            } else {
                Helpers.pushNotification(owner.getUserName() + " 已經加入您為聯絡人", "android", provider.getDeviceKey());
            }

            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

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

        /* == Get othersSideContact == */
        User provider = modifiedContact.getProviderUser();
        Date modifiedTime = new Date();

        if (provider != null) {
            /* Its not icon on the other side. */
            User user = modifiedContact.getUser();
            int type = modifiedContact.getChargeType() == 1 ? 2 : 1;
            Contact othersSideContact = dataStoreObject.createQuery(Contact.class)
                    .field("user").equal(provider)
                    .field("providerUser").equal(user)
                    .field("chargeType").equal(type).get();

            othersSideContact.setUpdateAt(modifiedTime);
            dataStoreObject.save(othersSideContact);
        }

        modifiedContact.setUpdateAt(modifiedTime);
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
        User user = payContact.getUser();
        String qrCodeUuid = payContact.getQrCodeUuid();
        Query<Group> query = dataStoreObject.createQuery(Group.class);

        if (payContact.getChargeType() != ContactConstant.TYPE_ICON) {
            Contact freeContact = dataStoreObject.createQuery(Contact.class).filter("qrCodeUuid =", qrCodeUuid).filter("user =", provider).get();
            List<String> contacts = Arrays.asList(freeContact.getId().toString(), payContact.getId().toString());
            List<Group> groupList = query.field("contacts").hasAnyOf(contacts).asList();

            for (Group group : groupList) {
                group.getContacts().remove(payContact.getId().toString());
                group.getContacts().remove(freeContact.getId().toString());
                dataStoreObject.save(group);
            }

            /**
             * add deleted contacts to the queue *
             */
            ArrayList<String> userDeleteQueue = user.getDeletedQueue() == null ? new ArrayList<String>() : user.getDeletedQueue();
            ArrayList<String> providerDeleteQueue = provider.getDeletedQueue() == null ? new ArrayList<String>() : provider.getDeletedQueue();

            userDeleteQueue.add(contactId);
            providerDeleteQueue.add(freeContact.getId().toString());

            user.setDeletedQueue(userDeleteQueue);
            provider.setDeletedQueue(providerDeleteQueue);

            dataStoreObject.save(provider, user);
            dataStoreObject.delete(freeContact);

        } else {
            //remove dependency;
            List<String> contacts = Arrays.asList(payContact.getId().toString());
            List<Group> groupList = query.field("contacts").hasAnyOf(contacts).asList();
            for (Group group : groupList) {
                group.getContacts().remove(payContact.getId().toString());
                dataStoreObject.save(group);
            }
            dataStoreObject.delete(payContact.getCustomerIcon());
            Query<Record> invalidRec = dataStoreObject.createQuery(Record.class);
            invalidRec.or(
                    invalidRec.criteria("callerIcon").equal(payContact.getCustomerIcon()),
                    invalidRec.criteria("calleeIcon").equal(payContact.getCustomerIcon())
            );

            dataStoreObject.delete(invalidRec);
        }

        dataStoreObject.delete(payContact);
        //clean dependency

        return Response.ok().build();
    }
}
