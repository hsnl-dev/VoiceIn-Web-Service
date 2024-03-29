package tw.kits.voicein.resource.ApiV2.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.mongodb.morphia.query.Query;
import tw.kits.voicein.bean.DeviceBean;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.bean.PasswordChangeBean;
import tw.kits.voicein.bean.RecordResBean;
import tw.kits.voicein.constant.ContactConstant;
import tw.kits.voicein.constant.RecordConstant;
import tw.kits.voicein.model.Record;
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.PasswordHelper;
import tw.kits.voicein.util.TokenRequired;

/**
 * Accounts Resource
 *
 * @author Calvin
 */
@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v2")
public class AccountsResource {

    @Context
    SecurityContext context;
    static final Logger LOGGER = Logger.getLogger(AccountsResource.class.getName());
//    private String tokenUser = context.getUserPrincipal().getName(); //user id of token
    ConsoleHandler consoleHandler = new ConsoleHandler();
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dataStoreObject = mongoManager.getDs();

    /**
     * This API allows client to retrieve of logined user's full informations.
     * API By Calvin
     *
     * @param uuid
     * @param field
     * @return User
     */
    @GET
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getUserAccount(
            @PathParam("uuid") String uuid,
            @QueryParam("field") String field
    ) {
        User user = dataStoreObject.get(User.class, context.getUserPrincipal().getName());

        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        
        LOGGER.log(Level.CONFIG, "Get User u{0}", uuid);

        if (field != null) {
            switch (field) {
                case "credit":
                    HashMap<String, Object> res = new HashMap<>();
                    res.put("credit", user.getCredit());
                    return Response.ok(res).build();
                default:
                    return Response.ok(user).build();
            }
        } else {
            return Response.ok(user).build();
        }
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
        User modifiedUser = dataStoreObject.get(User.class, context.getUserPrincipal().getName());
        if (modifiedUser == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        user.setUuid(uuid);
        user.setCredit(modifiedUser.getCredit());
        dataStoreObject.merge(user);

        LOGGER.log(Level.CONFIG, "Update User u{0}", user);
        return Response.ok().build();
    }

    /**
     *
     * @param uuid
     * @param deviceInfo
     * @return
     */
    @PUT
    @Path("/accounts/{uuid}/device")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response updateUserDeviceId(@PathParam("uuid") String uuid, @Valid DeviceBean deviceInfo) {
        User modifiedUser = dataStoreObject.get(User.class, context.getUserPrincipal().getName());

        modifiedUser.setDeviceKey(deviceInfo.getDeviceKey());
        modifiedUser.setDeviceOS(deviceInfo.getDeviceOS());

        dataStoreObject.save(modifiedUser);

        LOGGER.log(Level.CONFIG, "Update User u{0}", modifiedUser);
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
        dataStoreObject.delete(User.class, context.getUserPrincipal().getName());

        LOGGER.log(Level.CONFIG, "Delete User u{0}", uuid);
        return Response.ok().build();
    }

    /**
     * Call When user click the calling button. API By Calvin =========== This
     * is depreciated! ===========
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

    @GET
    @Path("/accounts/{userUuid}/history")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getHistory(@PathParam("userUuid") String uid, @QueryParam("before") long timestamp) {

        User user = dataStoreObject.get(User.class, context.getUserPrincipal().getName());
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        Query<Record> query = dataStoreObject.createQuery(Record.class).disableValidation();

        query.or(
                query.criteria("caller").equal(user),
                query.criteria("callee").equal(user));
        List<Record> invs;
        if (timestamp != 0) {
            invs = query.field("-reqTime").lessThanOrEq(new Date(timestamp)).limit(100).order("-reqTime").asList();
        } else {
            invs = query.order("-reqTime").limit(100).asList();
        }

        Date last = null;
        List<RecordResBean> res = new ArrayList<RecordResBean>();
        for (Record one : invs) {
            RecordResBean rrb = null;
            String type = one.getType();
            if (type.equals(RecordConstant.APP_TO_APP_CHARGE_CALLEE)
                    || type.equals(RecordConstant.APP_TO_APP_CHARGE_CALLER)) {
                if (uid.equals(one.getCaller().getUuid())) {
                    Contact contact = dataStoreObject.get(Contact.class, one.getCallerContactId());
                    rrb = new RecordResBean(one.getCallee(), one, contact, one.getCalleePhone());
                    rrb.setType("outgoing");
                } else if (uid.equals(one.getCallee().getUuid())) {
                    Contact contact = dataStoreObject.get(Contact.class, one.getCallerContactId());
                    if (contact == null) {
                        rrb = new RecordResBean(one.getCaller(), one, null, one.getCallerPhone());
                        rrb.setType("incoming");
                    } else {
                        int contactType;
                        if (one.getType().equals(RecordConstant.APP_TO_APP_CHARGE_CALLEE)) {
                            contactType = ContactConstant.TYPE_CHARGE;
                        } else {
                            contactType = ContactConstant.TYPE_FREE;
                        }
                        Contact another = dataStoreObject.createQuery(Contact.class)
                                .field("user").equal(contact.getProviderUser())
                                .field("providerUser").equal(contact.getUser())
                                .field("chargeType").equal(contactType).get();
                        rrb = new RecordResBean(one.getCaller(), one, another, one.getCallerPhone());
                        rrb.setType("incoming");
                    }

                }
            } else if (type.equals(RecordConstant.APP_TO_ICON)) {
                Contact contact = dataStoreObject.get(Contact.class, one.getCallerContactId());
                rrb = new RecordResBean(one.getCalleeIcon(), one, contact, one.getCalleePhone());
                rrb.setType("outgoing");
            } else if (type.equals(RecordConstant.ICON_TO_APP)) {
                Contact another = dataStoreObject.createQuery(Contact.class).field("customerIcon").equal(one.getCallerIcon()).get();
                rrb = new RecordResBean(one.getCallerIcon(), one, another, one.getCallerPhone());
                rrb.setType("incoming");
            } else {

            }
            res.add(rrb);
            last = one.getReqTime();
        }

        //retrieve last res;
        HashMap<String, Object> output = new HashMap<>();
        output.put("record", res);
//        output.put("nextMills",last);
        return Response.ok(output)
                .build();

    }

    @POST
    @Path("/accounts/{uuid}/actions/changePassword")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response changePass(@PathParam("uuid") String uuid, @NotNull @Valid PasswordChangeBean form) {
        User modifiedUser = dataStoreObject.get(User.class, context.getUserPrincipal().getName());
        if (modifiedUser.getPassword() == null) {
            modifiedUser.setPassword(PasswordHelper.getHashedString(form.getNewPassword()));
        } else if (PasswordHelper.isValidPassword(form.getOldPassword(), modifiedUser.getPassword())) {
            modifiedUser.setPassword(PasswordHelper.getHashedString(form.getNewPassword()));
        } else {
            return Response.status(Status.UNAUTHORIZED).entity(new ErrorMessageBean("Your old password is invalid")).build();
        }

        dataStoreObject.save(modifiedUser);
        LOGGER.log(Level.CONFIG, "Update User u{0}", modifiedUser);
        return Response.ok().build();
    }

}
