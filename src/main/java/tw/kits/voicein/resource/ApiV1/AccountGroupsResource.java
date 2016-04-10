package tw.kits.voicein.resource.ApiV1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.AccountAllGroupBean;
import tw.kits.voicein.bean.AccountGroupUpdateBean;
import tw.kits.voicein.bean.UserContactBean;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Group;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.User;
import static tw.kits.voicein.resource.ApiV1.AccountContactsResource.LOGGER;
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.TokenRequired;

/**
 *
 * @author Calvin
 */
@Path("/api/v1")
public class AccountGroupsResource {

    // For mongodb.
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dataStoreObject = mongoManager.getDs();

    // Logger.
    static final Logger LOGGER = Logger.getLogger(AccountAvatarsResource.class.getName());
    ConsoleHandler consoleHandler = new ConsoleHandler();

    /**
     *
     * @param uuid
     * @return
     */
    @GET
    @Path("/accounts/{uuid}/groups")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    @SuppressWarnings("empty-statement")
    public Response getGroupList(
            @PathParam("uuid") String uuid
    ) {
        User owner = dataStoreObject.get(User.class, uuid);
        List<Group> groups = dataStoreObject.createQuery(Group.class).field("user").equal(owner).asList();
        ArrayList<HashMap<String, Object>> groupsEntities = new ArrayList();
        AccountAllGroupBean accountAllGroupBean = new AccountAllGroupBean();
        
        for (Group group : groups) {
            HashMap<String, Object> groupEntity = new HashMap();
            
            groupEntity.put("groupName", group.getGroupName());
            groupEntity.put("groupId", group.getId());            
            groupsEntities.add(groupEntity);
        }
        
        accountAllGroupBean.setGroups(groupsEntities);
        return Response.ok(accountAllGroupBean).build();
    }
    
    /**
     *
     * @param uuid
     * @param groupId
     * @return
     */
    @GET
    @Path("/accounts/{uuid}/groups/{groupId}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    @SuppressWarnings("empty-statement")
    public Response getGroupContacts(
            @PathParam("uuid") String uuid,
            @PathParam("groupId") String groupId
    ) {
        Group groups = dataStoreObject.get(Group.class, groupId);
        ArrayList<String> contacts = groups.getContacts();
        List<UserContactBean> userList = new ArrayList();
        UserContactBean userContactBean;
        
        for (String contactId : contacts) {
            Contact contact = dataStoreObject.get(Contact.class, new ObjectId(contactId));
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
     *
     * @param uuid
     * @param groupUuid
     * @param action - expected parameters are delete or add.
     * @param contactsToUpdate
     * @return
     */
    @PUT
    @Path("/accounts/{uuid}/groups/{groupUuid}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response updateContactInFGroup(
            @PathParam("uuid") String uuid,
            @PathParam("groupUuid") String groupUuid,
            @QueryParam("action") String action,
            AccountGroupUpdateBean contactsToUpdate
    ) {
        ArrayList<String> contactsToModified = contactsToUpdate.getContacts();
        Group group = dataStoreObject.get(Group.class, groupUuid);
        ArrayList<String> existedContacts = group.getContacts();

        if (group != null) {
            // The group is found!
            switch (action) {
                case "add":

                    for (String contact : contactsToModified) {
                        existedContacts.add(contact);
                    }

                    group.setContacts(existedContacts);
                    dataStoreObject.save(group);

                    return Response.ok().build();
                case "delete":
                    for (String contact : contactsToModified) {
                        existedContacts.remove(contact);
                    }

                    group.setContacts(existedContacts);
                    dataStoreObject.save(group);

                    return Response.ok().build();
                default:
                    return Response.status(Status.NOT_ACCEPTABLE).build();

            }
        } else {
            // The aimed group is not found.
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    /**
     *
     * @param uuid
     * @param group
     * @return
     */
    @POST
    @Path("/accounts/{uuid}/groups")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response createAgroup(@PathParam("uuid") String uuid, @NotNull @Valid Group group) {
        User owner = dataStoreObject.get(User.class, uuid);

        if (owner != null) {
            group.setUser(owner);
            dataStoreObject.save(group);
            return Response.ok().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }

    }

    /**
     *
     * @param uuid
     * @param groupUuid
     * @return
     */
    @DELETE
    @Path("/accounts/{uuid}/groups/{groupUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response deleteAGroup(
            @PathParam("uuid") String uuid,
            @PathParam("groupUuid") String groupUuid
    ) {
        Group groupToDelete = dataStoreObject.get(Group.class, groupUuid);

        if (groupToDelete != null) {
            dataStoreObject.delete(groupToDelete);
            return Response.ok().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
