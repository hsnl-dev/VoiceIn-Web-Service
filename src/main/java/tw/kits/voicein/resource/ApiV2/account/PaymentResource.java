/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.resource.ApiV2.account;

import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.PayCreateBean;
import tw.kits.voicein.model.Payment;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.MongoManager;

/**
 *
 * @author Henry
 */
@Path("/api/v2")
public class PaymentResource {

    Datastore dataStore = MongoManager.getInstatnce().getDs();

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/accounts/{uuid}/payments")
    @POST
    public Response createPayment(@PathParam("uuid") String uuid, PayCreateBean form) {
        final Logger LOGGER = Logger.getLogger(PaymentResource.class.getName());
        Payment pay = new Payment();
        pay.setMethod(form.getMethod());
        pay.setMoney(form.getMoney());
        pay.setStatus(form.getStatus());
        pay.setUserId(uuid);
        pay.setTransationStatus("create");
        dataStore.save(pay);
 
        User user = dataStore.get(User.class, uuid);
        if ("success".equals(pay.getStatus())) {
            LOGGER.info(user+"");
            if (user != null) {
                user.setCredit(user.getCredit() + form.getMoney());
                dataStore.save(user);
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();    
            }

        }
        return Response.status(Response.Status.CREATED).build();

    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/accounts/{uuid}/payments")
    @GET
    public Response readPayment(@PathParam("uuid") String uuid) {

        return Response.status(Response.Status.CREATED).entity(dataStore.createQuery(Payment.class).asList()).build();

    }
}
