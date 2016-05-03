/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.resource.ApiV2;

import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.bean.PayCreateBean;
import tw.kits.voicein.bean.PaymentChangeBean;
import tw.kits.voicein.model.Payment;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.TokenRequired;

/**
 *
 * @author Henry
 */
@Path("/api/v2")
public class PaymentResource {

    Datastore dataStore = MongoManager.getInstatnce().getDs();
    final Logger LOGGER = Logger.getLogger(PaymentResource.class.getName());
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    @Path("/payments")
    @POST
    public Response createPayment(@Context SecurityContext sc,PayCreateBean form) {
       
        Payment pay = new Payment();
        pay.setMethod(form.getMethod());
        pay.setMoney(form.getMoney());
        pay.setStatus(form.getStatus());
        pay.setUserId(sc.getUserPrincipal().getName());
        pay.setPayId(form.getPayId());
        pay.setTransationStatus(form.getStatus());
        dataStore.save(pay);

        return Response.status(Response.Status.CREATED).build();

    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payments/{payId}/actions/changePayment")
    @POST
    public Response changePayment(@PathParam("payId") String payId, PaymentChangeBean form) {
        Payment payment = dataStore.get(Payment.class, payId);
        if (payment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if ("success".equals(form.getStatus())) {
            if (!"success".equals(payment.getStatus())) {
                Key key = new Key(User.class, "accounts", payment.getUserId());
                UpdateOperations<User> upo = dataStore.createUpdateOperations(User.class).inc("credit", payment.getMoney());
                UpdateResults res;
                res = dataStore.update(key, upo);
                payment.setTransationStatus("success");
                dataStore.save(payment);
                if (res.getUpdatedCount() < 1) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Response.Status.CONFLICT).entity(new ErrorMessageBean("invalid status")).build();
                
            }

        }
        return Response.status(Response.Status.CREATED).build();

    }

}
