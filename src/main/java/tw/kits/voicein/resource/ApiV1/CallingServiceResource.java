/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.resource.ApiV1;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import tw.kits.voicein.util.MongoManager;
import org.mongodb.morphia.Datastore;

import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.Http;
import tw.kits.voicein.util.Parameter;

import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.core.Response.Status;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.model.Record;

import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.constant.RecordConstant;

@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v1")
public class CallingServiceResource {

    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dsObj = mongoManager.getDs();
    private static final Logger LOGGER = Logger.getLogger(CallingServiceResource.class.getName());

    /**
     * This API allows customer to call provider after confirm button click. API
     * By Calvin Henry 
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
        Date reqStime = new Date();
        Record cdr = new Record();
        cdr.setId(UUID.randomUUID().toString());
        cdr.setReqTime(reqStime);
        cdr.setStatus(RecordConstant.REQ_SEND);
        dsObj.save(cdr);
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
