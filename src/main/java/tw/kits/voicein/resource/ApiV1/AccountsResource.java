package tw.kits.voicein.resource.ApiV1;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.*;
import javax.validation.constraints.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import tw.kits.voicein.util.MongoManager;
import org.mongodb.morphia.Datastore;
import org.bson.types.ObjectId;

import tw.kits.voicein.model.User;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.Http;

/**
 * Accounts Resource
 *
 * @author Calvin
 */
@Path("/api/v1")
public class AccountsResource {

    static final Logger LOGGER = Logger.getLogger(AccountsResource.class .getName());
    ConsoleHandler consoleHandler = new ConsoleHandler();
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dsObj = mongoManager.getDs();

    /**
     * This API allows user to delete a user account by given uuid.
     *
     * @param uuid
     * @return
     */
    @DELETE
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUserAccount(@PathParam("uuid") String uuid) {
        dsObj.delete(User.class, uuid);

        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.CONFIG);

        LOGGER.addHandler(consoleHandler);    

        LOGGER.log(Level.CONFIG, "[Config] Delete user u{0}", uuid);

        return Response.ok().build();
    }

    /**
     * This API allows client to update user's information.
     *
     * @param uuid
     * @param u
     * @return response to the client
     */
    @PUT
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.APPLICATION_JSON)
    public Response updateUserAccount(@PathParam("uuid") String uuid, User u) {        
        u.setUuid(uuid);
        dsObj.save(u);

        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.ALL);

        LOGGER.addHandler(consoleHandler);

        LOGGER.log(Level.CONFIG, "[Config] Update user u{0}", u);

        return Response.ok().build();
    }

    /**
     * This API allows client to retrieve user's full informations.
     *
     * @param uuid
     * @return User
     */
    @GET
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public User getUserAccount(@PathParam("uuid") String uuid) {
        User user = dsObj.get(User.class, uuid);

        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.CONFIG);

        LOGGER.addHandler(consoleHandler);

        LOGGER.log(Level.CONFIG, "[Config] Get user u{0}", uuid);

        return user;
    }

    /**
     * Call
     *
     * @param uuid
     * @return response
     */
    @POST
    @Path("/accounts/{uuid}/calls")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response makePhoneCall(@PathParam("uuid") String uuid) throws IOException {
        String endPoint = "https://ts.kits.tw/projectLYS/v0/Call/test01/generalCallRequest/";
        String caller = "0988779570";
        String callee = "0975531859";
        String payload = "{\"caller\":\"%s\",\"callee\":\"%s\",\"check\":false}";
        
        Http http = new Http();
        System.out.println(payload);
        System.out.println(http.post(endPoint, String.format(payload, caller, callee)));
        return Response.status(Status.OK).build();
    }

    /**
     * This API allows user to get their contact list.
     *
     * @param uuid
     * @return
     */
    @GET
    @Path("/accounts/{uuid}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> getContactListOfAnUser(@PathParam("uuid") String uuid) {
        User user = dsObj.get(User.class, uuid);

        List<Contact> queryResult = dsObj.find(Contact.class).field("user").equal(user).asList();

        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.CONFIG);
        LOGGER.addHandler(consoleHandler);
        LOGGER.log(Level.CONFIG, "[Config] contact length {0}", queryResult.size());

        return queryResult;
    }

    /**
     * This API allows user to add a contact.
     *
     * @param uuid
     * @param contact
     * @return
     */
    @POST
    @Path("/accounts/{uuid}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewContactofAnUser(@PathParam("uuid") String uuid, Contact contact) {
        User refUser = dsObj.get(User.class, uuid);
        contact.setUser(refUser);

        dsObj.save(contact);
        return Response.ok().build();
    }

    


    
    /**
     * This API allows client user to update a contact.
     * @param uuid
     * @param contactId
     * @param contact
     * @return
     */
    @PUT
    @Path("/accounts/{uuid}/contacts/{contactId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAcontactOfAnUser(@PathParam("uuid") String uuid, @PathParam("contactId") String contactId, Contact contact) {
        ObjectId oid = new ObjectId(contactId);
        contact.setId(oid);
        dsObj.save(contact);
        return Response.ok().build();
    }
    
    /**
     * This API allows client to delete a contact.
     * @param uuid
     * @param contactId
     * @return
     */
    @DELETE
    @Path("/accounts/{uuid}/contacts/{contactId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAcontactOfAnUser(@PathParam("uuid") String uuid, @PathParam("contactId") String contactId) {
        ObjectId oid = new ObjectId(contactId);
        dsObj.delete(Contact.class, oid);
        return Response.ok().build();
    }
    
    /**
     * This API allows client to retrieve their QRCode
     * @param uuid
     * @return
     */
    @GET
    @Path("/accounts/{uuid}/qrcode")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getAccountQRCode(@PathParam("uuid") String uuid) {
        // TODO: Retrieve QRCode image.
        return Response.ok().build();
    }
    /**
     * This API allows user to upload avatar
     *
     */
    @POST
    @Path("/accounts5/{uuid}/avatar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAvatar(
            @NotNull @FormDataParam("photo") InputStream fileInputStream,
            @NotNull @FormDataParam("photo") FormDataContentDisposition header,
            @PathParam("uuid") String uuid) throws IOException {
        String tmp_dir = System.getProperty("java.io.tmpdir");
        String photoUuid = UUID.randomUUID().toString();

        
        FileOutputStream out = null;
        InputStream in = fileInputStream;
        try {
            byte[] bytes = new byte[1024];
            int read = 0;
            out = new FileOutputStream(new File(tmp_dir + "/" + photoUuid));
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            LOGGER.log(Level.INFO,String.format("Upload file success", tmp_dir + "/" + photoUuid ));
        } finally {
            LOGGER.log(Level.INFO,String.format("Finalize", tmp_dir + "/" + photoUuid ));
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(AccountsResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(AccountsResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return Response.ok().build();

    }
}
