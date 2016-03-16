package tw.kits.voicein.resource.ApiV1;

import java.io.IOException;
import java.util.logging.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import tw.kits.voicein.util.MongoManager;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.AccountCallBean;

import tw.kits.voicein.model.User;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.Http;
import tw.kits.voicein.util.Parameter;

import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import tw.kits.voicein.util.Helpers;

import tw.kits.voicein.util.TokenRequired;

/**
 * Accounts Resource
 *
 * @author Calvin
 */
@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v1")
public class AccountsResource {

    @Context
    SecurityContext context;
    static final Logger LOGGER = Logger.getLogger(AccountsResource.class.getName());
//    private String tokenUser = context.getUserPrincipal().getName(); //user id of token
    ConsoleHandler consoleHandler = new ConsoleHandler();
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dataStoreObject = mongoManager.getDs();

    private void initLogger() {
        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.CONFIG);
        LOGGER.addHandler(consoleHandler);
    }

    /**
     * This API allows client to retrieve user's full informations. API By
     * Calvin
     *
     * @param uuid
     * @return User
     */
    @GET
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getUserAccount(@PathParam("uuid") String uuid) {
        User user = dataStoreObject.get(User.class, uuid);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        initLogger();

        LOGGER.log(Level.CONFIG, "Get User u{0}", uuid);

        return Response.ok(user).build();
    }

    /**
     * This API allows client to update user's information. API By Calvin
     *
     * @param uuid
     * @param user
     * @return response to the client
     */
    @PUT
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response updateUserAccount(@PathParam("uuid") String uuid, @Valid User user) {
        User modifiedUser = dataStoreObject.get(User.class, uuid);

        user.setUuid(uuid);
        user.setProfilePhotoId(modifiedUser.getProfilePhotoId());
        user.setQrCodeUuid(modifiedUser.getQrCodeUuid());

        dataStoreObject.save(user);

        initLogger();
        LOGGER.log(Level.CONFIG, "Update User u{0}", user);

        return Response.ok().build();
    }

    /**
     * This API allows user to delete a user account by given UUID. API By
     * Calvin.
     *
     * @param uuid
     * @return
     */
    @DELETE
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response deleteUserAccount(@PathParam("uuid") String uuid) {
        dataStoreObject.delete(User.class, uuid);

        initLogger();

        LOGGER.log(Level.CONFIG, "Delete User u{0}", uuid);

        return Response.ok().build();
    }

    /**
     * Call When user click the calling button. API By Calvin
     *
     * @param uuid
     * @param qrCodeUuid
     * @param callBean
     * @return response
     * @throws java.io.IOException
     */
    @POST
    @Path("/accounts/{uuid}/calls/{qrCodeUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response makePhoneCall(
            @PathParam("uuid") String uuid,
            @PathParam("qrCodeUuid") String qrCodeUuid,
            @Valid AccountCallBean callBean
    ) throws IOException {
        String endPoint = Parameter.API_ROOT + Parameter.API_VER + "Call/test01/generalCallRequest/";
        User user = dataStoreObject.get(User.class, uuid);

        initLogger();

        Contact contactToCall = dataStoreObject.createQuery(Contact.class).filter("user =", user).filter("qrCodeUuid", qrCodeUuid).get();
        contactToCall = dataStoreObject.createQuery(Contact.class)
                .filter("user =", contactToCall.getProviderUser())
                .filter("qrCodeUuid =", qrCodeUuid).get();

        LOGGER.log(Level.CONFIG, " ContactToCall {0}", contactToCall);
        LOGGER.log(Level.CONFIG, " qrCodeUuid {0}", qrCodeUuid);

        if (Helpers.isAllowedToCall(contactToCall)) {
            String caller = callBean.getCaller();
            String callee = callBean.getCallee();
            String payload = "{\"caller\":\"%s\",\"callee\":\"%s\",\"check\":false}";

            Http http = new Http();
            System.out.println(payload);
            System.out.println(http.post(endPoint, String.format(payload, caller, callee)));
            return Response.ok().build();
        } else {
            return Response.status(Status.FORBIDDEN).build();
        }
    }

    /**
     * This API allows client user to update a contact. API By Calvin
     *
     * @param uuid
     * @param qrCodeUuid
     * @param nickName
     * @param availableStartTime
     * @param availableEndTime
     * @param isEnable
     * @param isHigherPriorityThanGlobal
     * @return
     */
    @PUT
    @Path("/accounts/{uuid}/contacts/{qrCodeUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response updateAcontactOfAnUser(
            @PathParam("uuid") String uuid,
            @PathParam("qrCodeUuid") String qrCodeUuid,
            @QueryParam("nickName") String nickName,
            @QueryParam("availableStartTime") String availableStartTime,
            @QueryParam("availableEndTime") String availableEndTime,
            @QueryParam("isEnable") String isEnable,
            @QueryParam("isHigherPriorityThanGlobal") String isHigherPriorityThanGlobal
    ) {
        User u = dataStoreObject.get(User.class, uuid);
        Contact modifiedContact = dataStoreObject.createQuery(Contact.class).filter("qrCodeUuid =", qrCodeUuid).filter("user =", u).get();

        initLogger();

        LOGGER.log(Level.CONFIG, "Save A Contact.{0}", nickName);

        if (nickName != null) {
            modifiedContact.setNickName(nickName);
        }

        if (isEnable != null) {
            if (isEnable.equalsIgnoreCase("true")) {
                modifiedContact.setIsEnable(true);
            } else {
                modifiedContact.setIsEnable(false);
            }

        }

        if (isHigherPriorityThanGlobal != null) {
            if (isHigherPriorityThanGlobal.equalsIgnoreCase("true")) {
                modifiedContact.setIsHigherPriorityThanGlobal(true);
            } else {
                modifiedContact.setIsHigherPriorityThanGlobal(false);
            }
        }

        if (availableStartTime != null) {
            modifiedContact.setAvailableStartTime(availableStartTime);
        }

        if (availableEndTime != null) {
            modifiedContact.setAvailableEndTime(availableEndTime);
        }

        dataStoreObject.save(modifiedContact);
        return Response.ok().build();
    }
}
