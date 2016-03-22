/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.resource.ApiV2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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
import tw.kits.voicein.bean.AccountCallBean;
import tw.kits.voicein.bean.AccountDialBean;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Icon;
import tw.kits.voicein.util.ContactConstants;
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.Http;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.Parameter;
import tw.kits.voicein.util.TokenRequired;

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
        if(contact == null){
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessageBean("contact not found")).build();
        }
        if (contact.getChargeType() != ContactConstants.TYPE_ICON) {
            int targetType = contact.getChargeType() == ContactConstants.TYPE_FREE ? ContactConstants.TYPE_CHARGE : ContactConstants.TYPE_FREE;
            List<Contact> targets = dataStoreObject.createQuery(Contact.class)
                    .field("providerUser").equal(contact.getUser().getUuid())
                    .field("chargeType").equal(targetType)
                    .asList();
            if (targets.size() != 1) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            if (Helpers.isAllowedToCall(targets.get(0))) {
                makeCall(contact.getUser().getPhoneNumber(), targets.get(0).getUser().getPhoneNumber());
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        } else {
            Icon icon = contact.getCustomerIcon();
            if (Helpers.isAllowedToCall(icon)) {
                makeCall(contact.getUser().getPhoneNumber(), icon.getPhoneNumber());
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

        }

    }

    private void makeCall(String caller, String callee) throws IOException {
        String endPoint = Parameter.API_ROOT + Parameter.API_VER + "Call/test01/generalCallRequest/";
        HashMap<String, Object> req = new HashMap();

        req.put("caller", caller);
        req.put("callee", callee);
        req.put("check", false);
        String reqStr = new ObjectMapper().writeValueAsString(req);

        Http http = new Http();
        LOGGER.info(reqStr);
        LOGGER.info(http.post(endPoint, reqStr));

    }

}
