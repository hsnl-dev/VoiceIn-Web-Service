package tw.kits.voicein.resource.ApiV1;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.*;
import javax.validation.constraints.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import tw.kits.voicein.util.MongoManager;
import org.mongodb.morphia.Datastore;
import org.bson.types.ObjectId;
import tw.kits.voicein.bean.AccountCallBean;

import tw.kits.voicein.model.User;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.Http;
import tw.kits.voicein.util.Parameter;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateOperations;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.util.ImageProceesor;
import tw.kits.voicein.util.TokenRequired;

/**
 * Accounts Resource
 *
 * @author Calvin
 */
@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v1")
public class AccountsResource {

    static final Logger LOGGER = Logger.getLogger(AccountsResource.class.getName());
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
    public Response getUserAccount(@PathParam("uuid") String uuid) {
        User user = dsObj.get(User.class, uuid);

        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.CONFIG);

        LOGGER.addHandler(consoleHandler);

        LOGGER.log(Level.CONFIG, "[Config] Get user u{0}", uuid);

        return Response.ok(user).build();
    }

    /**
     * Call
     *
     * @param uuid
     * @param callBean
     * @return response
     * @throws java.io.IOException
     */
    @POST
    @Path("/accounts/{uuid}/calls")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response makePhoneCall(@PathParam("uuid") String uuid, AccountCallBean callBean) throws IOException {
        String endPoint = Parameter.API_ROOT + Parameter.API_VER + "Call/test01/generalCallRequest/";
        String caller = callBean.getCaller();
        String callee = callBean.getCallee();
        String payload = "{\"caller\":\"%s\",\"callee\":\"%s\",\"check\":false}";

        Http http = new Http();
        System.out.println(payload);
        System.out.println(http.post(endPoint, String.format(payload, caller, callee)));
        return Response.ok().build();
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
    public Response getContactListOfAnUser(@PathParam("uuid") String uuid) {
        User user = dsObj.get(User.class, uuid);

        List<Contact> queryResult = dsObj.find(Contact.class).field("user").equal(user).asList();

        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.CONFIG);
        LOGGER.addHandler(consoleHandler);
        LOGGER.log(Level.CONFIG, "[Config] contact length {0}", queryResult.size());

        return Response.ok(queryResult).build();
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
     *
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
     *
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
     *
     * @param uuid
     * @return
     */
    @POST
    @Path("/accounts/{uuid}/qrcode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateQRCode(@PathParam("uuid") String uuid) {
        /**
         * QR Code Generator test*
         */
        User u = dsObj.get(User.class, uuid);
        String s3Bucket = "voice-in";
        String s3FilePath = String.format("qrCode/%s.png", uuid);
        String qrCodeUuid = UUID.randomUUID().toString();

        // Generate QRCode Image and Upload to S3.
        File qrCodeImage = QRCode.from(qrCodeUuid).to(ImageType.PNG).withSize(250, 250).file();
        AmazonS3 s3Client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
        s3Client.putObject(new PutObjectRequest(s3Bucket, s3FilePath, qrCodeImage));
        u.setQrCodeUuid(qrCodeUuid);
        dsObj.save(u);

        return Response.ok().build();
    }

    /**
     * This API allows client to retrieve their QRCode
     *
     * @param uuid
     * @return
     * @throws java.io.IOException
     */
    @GET
    @Path("/accounts/{uuid}/qrcode")
    @Produces("image/png")
    public Response getAccountQRCode(@PathParam("uuid") String uuid) throws IOException {
        // [Testing]
        byte[] qrCodeData;
        AmazonS3 s3Client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
        String s3Bucket = "voice-in";
        String file = String.format("qrCode/%s.png", uuid);
        GetObjectRequest request = new GetObjectRequest(s3Bucket, file);
        S3Object object = s3Client.getObject(request);
        qrCodeData = IOUtils.toByteArray(object.getObjectContent());
        /*
        try (FileOutputStream fos = new FileOutputStream("/Volumes/JetDrive/GoogleDrive/Projects/voicein/voicein-api/test.png")) {
            fos.write(qrCodeData);
            fos.close();
        }*/
        return Response.ok(qrCodeData).build();
    }

    /**
     * This API allows user to upload avatar
     *
     * @param sc
     * @param fileInputStream
     * @param header
     * @param uuid
     * @return
     * @throws java.io.IOException
     */
    @POST
    @TokenRequired
    @Path("/accounts/{uuid}/avatar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAvatar(
            @Context SecurityContext sc,
            @NotNull @FormDataParam("photo") InputStream fileInputStream,
            @NotNull @FormDataParam("photo") FormDataContentDisposition header,
            @PathParam("uuid") String uuid) throws IOException {

        String tmpDir = System.getProperty("java.io.tmpdir");
        String photoUuid = UUID.randomUUID().toString();
        File tmpFile = new File(tmpDir + File.separator + photoUuid);
        try {
            BufferedImage bri = ImageIO.read(fileInputStream);
            if (bri == null) {
                ErrorMessageBean er = new ErrorMessageBean();
                er.setErrorReason("not supported format");
                return Response.status(Status.NOT_ACCEPTABLE).entity(er).build();
            }
            ImageProceesor ip = new ImageProceesor(bri);
            ip.resize(256, 256);
            ip.saveFileWithJPGCompress(tmpFile);
            AmazonS3 s3client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
            //upload to s3
            LOGGER.log(Level.INFO, String.format("start upload to s3", tmpDir + "/" + photoUuid));
            s3client.putObject(
                    new PutObjectRequest(
                            "voice-in",
                            "userPhotos/" + photoUuid + ".jpg",
                            tmpFile
                    )
            );
            LOGGER.log(Level.INFO, String.format("file update" + "ok" + tmpDir + "/" + photoUuid));
            Key key = new Key(User.class, "accounts", sc.getUserPrincipal().getName());
            UpdateOperations<User> upo = dsObj.createUpdateOperations(User.class).set("profilePhotoId", photoUuid);
            dsObj.update(key, upo);
            LOGGER.log(Level.INFO, String.format("user info update OK"));
        } finally {
            if (tmpFile.delete()) {
                LOGGER.log(Level.INFO, String.format("del temp file OK"));
            } else {
                LOGGER.log(Level.WARNING, String.format("del temp file OK"));
            }
        }
        return Response.ok().build();

    }
}
