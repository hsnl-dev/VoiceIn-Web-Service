/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.resource.ApiV2;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import tw.kits.voicein.bean.AccountDialBean;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.bean.HangupNotifyBean;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.User;
import tw.kits.voicein.constant.ContactConstant;
import tw.kits.voicein.constant.RecordConstant;
import tw.kits.voicein.model.Record;
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.TokenRequired;

@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v2")
/**
 *
 * @author Calvin
 */
public class CallingServiceResource {

    @Context
    SecurityContext context;
    static final Logger LOGGER = Logger.getLogger(CallingServiceResource.class.getName());
//    private String tokenUser = context.getUserPrincipal().getName(); //user id of token
    ConsoleHandler consoleHandler = new ConsoleHandler();
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dataStoreObject = mongoManager.getDs();

    /**
     * receive call from sip server
     *
     * @param id
     * @param form
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/calls/{recordId}")
    public Response handleHangup(@PathParam("recordId") String id, HangupNotifyBean form) {
        Record record = dataStoreObject.get(Record.class, id);
        if (record == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (form.isSuccess()) {
            float pay = 2 * 0.8f * (form.getEndTime() - form.getStartTime());
            record.setChargeDollar(pay);
            record.setIsAnswer(true);
            record.setStartTime(new Date(form.getStartTime()));
            record.setEndTime(new Date(form.getEndTime()));
        } else {
            record.setStartTime(new Date(form.getStartTime()));
            record.setIsAnswer(false);
            record.setChargeDollar(0.0f);
            
        }
        record.setStatus(RecordConstant.HANGUP);
        dataStoreObject.save(record);
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Call When user click the calling button. API By Calvin
     *
     * @param contactId
     * @param info
     * @return response
     * @throws java.io.IOException
     */
    @POST
    @Path("/accounts/{uuid}/calls")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response makePhoneCall(
            @PathParam("contactId") String contactId,
            @NotNull @Valid AccountDialBean info
    ) throws IOException {

        Contact contact = dataStoreObject.get(Contact.class, info.getContactId());
        if (contact == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessageBean("contact not found")).build();
        }
//        if (contact.getProviderUser().getCredit() <= 0) {
//            return Response.status(Response.Status.PAYMENT_REQUIRED).entity(new ErrorMessageBean("credit <= 0")).build();
//        }
        if (contact.getChargeType() != ContactConstant.TYPE_ICON) {
            int targetType = contact.getChargeType() == ContactConstant.TYPE_FREE ? ContactConstant.TYPE_CHARGE : ContactConstant.TYPE_FREE;
            List<Contact> targets = dataStoreObject.createQuery(Contact.class)
                    .field("providerUser").equal(contact.getUser())
                    .field("user").equal(contact.getProviderUser())
                    .field("chargeType").equal(targetType)
                    .asList();
            LOGGER.info(targets.get(0).getId() + "");
            if (targets.size() != 1) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            if (Helpers.isAllowedToCall(targets.get(0))) {
                Helpers.makeCall(contact.getUser().getPhoneNumber(),targets.get(0).getUser().getPhoneNumber(),
                        contact,
                        dataStoreObject);
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        } else {
            Icon icon = contact.getCustomerIcon();
            if (Helpers.isAllowedToCall(icon)) {
                Helpers.makeCall(contact.getUser().getPhoneNumber(), icon.getPhoneNumber(), contact, dataStoreObject);
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

        }

    }
}
