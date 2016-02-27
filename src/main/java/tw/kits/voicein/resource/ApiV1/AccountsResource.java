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
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateOperations;
import tw.kits.voicein.bean.ErrorMessageBean;
import static tw.kits.voicein.resource.ApiV1.AccountsResource.LOGGER;
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
    private static final int AVATAR_LARGE = 256;
    private static final int AVATAR_MID = 128;
    private static final int AVATAR_SMALL = 64;

    /**
     * This API allows user to delete a user account by given UUID.
     * API By Calvin.
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
     * API By Calvin
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
     * API By Calvin
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
     * Call When user click the calling button.
     * API By Calvin
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
     * API By Calvin
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
     * API By Calvin
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
     * API By Calvin
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
     * API By Calvin
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
     * Create user's QRCode by randomized UUID.
     * API By Calvin
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
     * API By Calvin
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
     * API By Henry
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
        String photoBaseName = tmpDir + File.separator + photoUuid;
        ArrayList<File> files = new ArrayList<File>();
        int[] imageSizes = {AVATAR_LARGE, AVATAR_MID, AVATAR_SMALL};
        try {
            BufferedImage bri = ImageIO.read(fileInputStream);
            if (bri == null) {
                ErrorMessageBean er = new ErrorMessageBean();
                er.setErrorReason("not supported format");
                return Response.status(Status.NOT_ACCEPTABLE).entity(er).build();
            }
            for (int size : imageSizes) {
                ImageProceesor ip = new ImageProceesor(bri);
                File tmpFile = new File(photoBaseName + "-" + size + ".jpg");
                ip.resize(size, size).saveFileWithJPGCompress(tmpFile);
                files.add(tmpFile);
            }
            String userId = sc.getUserPrincipal().getName();
            Key key = new Key(User.class, "accounts", userId);
            User user = dsObj.get(User.class, userId);
            String oldId = user.getProfilePhotoId();
            AmazonS3 s3client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
            for (File onefile : files) {
                //upload to s3
                LOGGER.log(Level.INFO, String.format(onefile.getName()));
                s3client.putObject(
                        new PutObjectRequest(
                                "voice-in",
                                "userPhotos/" + onefile.getName(),
                                onefile
                        )
                );

            }
            if (oldId != null) {
                //delete old
                for (int size : imageSizes) {
                    LOGGER.log(Level.INFO, "Delete" + oldId + "-" + size + ".jpg");
                    s3client.deleteObject("voice-in", "userPhotos/" + oldId + "-" + size + ".jpg");

                }
            }

            LOGGER.log(Level.INFO, String.format("file update" + "ok" + tmpDir + "/" + photoUuid));
            UpdateOperations<User> upo = dsObj.createUpdateOperations(User.class).set("profilePhotoId", photoUuid);
            dsObj.update(key, upo);

            LOGGER.log(Level.INFO, String.format("user info update OK"));
        } finally {
            for (File one : files) {
                if (one.delete()) {
                    LOGGER.log(Level.INFO, String.format("del temp file OK"));
                } else {
                    LOGGER.log(Level.WARNING, String.format("del temp file Failed"));
                }
            }
        }
        return Response.ok().build();
    }

    /**
     * This API allows user to retrieve user's avatar by user's UUID.
     * API By Henry
     * @param uuid
     * @param sc
     * @param size
     * @return
     * @throws IOException
     */
    @GET
    @Path("/accounts/{uuid}/avatar")
    @Produces("image/jpg")
    public Response getAccountAvatar(@PathParam("uuid") String uuid,
            @Context SecurityContext sc,
            @QueryParam("size") String size) throws IOException {
        String avatarUuid = dsObj.get(User.class, uuid).getProfilePhotoId();
        if (avatarUuid == null) {
            Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(getAvatar(avatarUuid, size)).build();
    }

    /**
     * This API allows user to retrieve user's avatar by UUID of avatar.
     * API By Henry
     * @param uuid
     * @param sc
     * @param size
     * @return
     * @throws IOException
     */
    @GET
    @Path("/avatars/{avatarUuid}")
    @Produces("image/jpg")
    public Response getAvatarByAvId(@PathParam("avatarUuid") String uuid,
            @Context SecurityContext sc,
            @QueryParam("size") String size) throws IOException {
        User u = dsObj.createQuery(User.class).field("profilePhotoId").equal(uuid).get();
        if (u == null) {
            Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(getAvatar(uuid, size)).build();
    }

    private byte[] getAvatar(String avatarUuid, String size) throws IOException {
        AmazonS3 s3Client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
        String s3Bucket = "voice-in";
        int imgSize;
        LOGGER.log(Level.INFO, size);

        if ("large".equals(size)) {
            imgSize = AVATAR_LARGE;
        } else if ("mid".equals(size)) {
            imgSize = AVATAR_MID;
        } else {
            imgSize = AVATAR_SMALL;
        }
        String file = String.format("userPhotos/%s-%d.jpg", avatarUuid, imgSize);
        GetObjectRequest request = new GetObjectRequest(s3Bucket, file);
        S3Object object = s3Client.getObject(request);
        return IOUtils.toByteArray(object.getObjectContent());
    }
}
