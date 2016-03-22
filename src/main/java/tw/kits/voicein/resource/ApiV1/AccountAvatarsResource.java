/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.resource.ApiV1;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.annotation.MultipartConfig;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateOperations;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.ImageProceesor;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.Parameter;
import tw.kits.voicein.util.TokenRequired;


@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v1")
public class AccountAvatarsResource {
    private static final int AVATAR_LARGE = 256;
    private static final int AVATAR_MID = 128;
    private static final int AVATAR_SMALL = 64;
    @Context
    SecurityContext mContext;
    static final Logger LOGGER = Logger.getLogger(AccountAvatarsResource.class.getName());
//    private String tokenUser = context.getUserPrincipal().getName(); //user id of token
    ConsoleHandler consoleHandler = new ConsoleHandler();
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dataStoreObject = mongoManager.getDs();
     /**
     * This API allows client to delete a contact. API By Calvin
     *
     * @param uuid
     * @param qrCodeUuid
     * @return
     */
    @DELETE
    @Path("/accounts/{uuid}/contacts/{qrCodeUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response deleteAcontactOfAnUser(@PathParam("uuid") String uuid, @PathParam("qrCodeUuid") String qrCodeUuid) {
        User user = dataStoreObject.get(User.class, uuid);

        Contact payContact = dataStoreObject.createQuery(Contact.class).filter("qrCodeUuid =", qrCodeUuid).filter("user =", user).get();

        User provider = payContact.getProviderUser();
        Contact freeContact = dataStoreObject.createQuery(Contact.class).filter("qrCodeUuid =", qrCodeUuid).filter("user =", provider).get();

        dataStoreObject.delete(Contact.class, payContact.getId());
        dataStoreObject.delete(Contact.class, freeContact.getId());
        return Response.ok().build();
    }
    
   

    /**
     * This API allows user to upload avatar API By Henry
     *
     * @param sc
     * @param fileInputStream
     * @param header
     * @param uuid
     * @return
     * @throws java.io.IOException
     */
    @POST
    @Path("/accounts/{uuid}/avatar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
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
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(er).build();
            }
            for (int size : imageSizes) {
                ImageProceesor ip = new ImageProceesor(bri);
                File tmpFile = new File(photoBaseName + "-" + size + ".jpg");
                ip.resize(size, size).saveFileWithJPGCompress(tmpFile);
                files.add(tmpFile);
            }
            String userId = sc.getUserPrincipal().getName();
            Key key = new Key(User.class, "accounts", userId);
            User user = dataStoreObject.get(User.class, userId);
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
            UpdateOperations<User> upo = dataStoreObject.createUpdateOperations(User.class).set("profilePhotoId", photoUuid);
            dataStoreObject.update(key, upo);

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
     * This API allows user to retrieve user's avatar by UUID of avatar. API By
     * Henry
     *
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
        User u = dataStoreObject.createQuery(User.class).field("profilePhotoId").equal(uuid).get();
        if (u == null) {
            Response.status(Response.Status.NOT_FOUND).build();
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
    /**
     * This API allows user to retrieve user's avatar by user's UUID. API By
     * Henry
     *
     * @param uuid
     * @param sc
     * @param size
     * @return
     * @throws IOException
     */
    @GET
    @Path("/accounts/{uuid}/avatar")
    @Produces("image/jpg")
    @TokenRequired
    public Response getAccountAvatar(@PathParam("uuid") String uuid,
            @Context SecurityContext sc,
            @QueryParam("size") String size) throws IOException {
        String avatarUuid = dataStoreObject.get(User.class, uuid).getProfilePhotoId();
        if (avatarUuid == null) {
            Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(getAvatar(avatarUuid, size)).build();
    }

}
