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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateOperations;
import tw.kits.voicein.bean.CustomQRcodeCreateBean;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.bean.QRcodeNoProviderBean;
import tw.kits.voicein.bean.UserContactBean;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.util.ImageProceesor;
import tw.kits.voicein.util.QRcodeType;
import tw.kits.voicein.util.TokenRequired;

/**
 * Accounts Resource
 *
 * @author Calvin
 */
@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v1")
public class AccountsResource {

    @Context
    SecurityContext context;
    static final Logger LOGGER = Logger.getLogger(AccountsResource.class.getName());
//    private String tokenUser = context.getUserPrincipal().getName(); //user id of token
    ConsoleHandler consoleHandler = new ConsoleHandler();
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dataStoreObject = mongoManager.getDs();
    private static final int AVATAR_LARGE = 256;
    private static final int AVATAR_MID = 128;
    private static final int AVATAR_SMALL = 64;

    private void initLogger() {
        LOGGER.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.CONFIG);
        LOGGER.addHandler(consoleHandler);
    }

    // Helpers methods.
    private boolean isAllowedToCall(Contact contact) {
        String availableStartTime;
        String availableEndTime;
        User provider = contact.getUser();
        boolean isEnable = contact.getIsEnable();

        initLogger();

        if (contact.getIsHigherPriorityThanGlobal()) {
            availableStartTime = contact.getAvailableStartTime();
            availableEndTime = contact.getAvailableEndTime();
        } else {
            availableStartTime = provider.getAvailableStartTime();
            availableEndTime = provider.getAvailableEndTime();
        }

        // Get current time.
        Date currentTimeStamp = new Date();
        // In 24 type.
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTimeInString = sdf.format(currentTimeStamp);

        boolean isAfter = currentTimeInString.compareTo(availableStartTime) >= 0;
        boolean isBefore = availableEndTime.compareTo(currentTimeInString) >= 0;

        LOGGER.log(Level.CONFIG, "{0} {1}", new Object[]{availableStartTime, availableEndTime});

        if (isEnable) {
            // If the contact is isEnable, check the available time.

            return isAfter && isBefore;
        } else {
            // If the contact is Disable, the call is not allowed.
            return isEnable;
        }
    }


    /**
     * This API allows client to retrieve user's full informations. API By
     * Calvin
     *
     * @param uuid
     * @return User
     */
    @GET
    @Path("/accounts/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getUserAccount(@PathParam("uuid") String uuid) {
        User user = dataStoreObject.get(User.class, uuid);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        initLogger();

        LOGGER.log(Level.CONFIG, "Get User u{0}", uuid);

        return Response.ok(user).build();
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
        User modifiedUser = dataStoreObject.get(User.class, uuid);

        user.setUuid(uuid);
        user.setProfilePhotoId(modifiedUser.getProfilePhotoId());
        user.setQrCodeUuid(modifiedUser.getQrCodeUuid());

        dataStoreObject.save(user);

        initLogger();
        LOGGER.log(Level.CONFIG, "Update User u{0}", user);

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
        dataStoreObject.delete(User.class, uuid);

        initLogger();

        LOGGER.log(Level.CONFIG, "Delete User u{0}", uuid);

        return Response.ok().build();
    }

    /**
     * Call When user click the calling button. API By Calvin
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

        initLogger();

        Contact contactToCall = dataStoreObject.createQuery(Contact.class).filter("user =", user).filter("qrCodeUuid", qrCodeUuid).get();
        contactToCall = dataStoreObject.createQuery(Contact.class)
                .filter("user =", contactToCall.getProviderUser())
                .filter("qrCodeUuid =", qrCodeUuid).get();

        LOGGER.log(Level.CONFIG, " ContactToCall {0}", contactToCall);
        LOGGER.log(Level.CONFIG, " qrCodeUuid {0}", qrCodeUuid);

        if (isAllowedToCall(contactToCall)) {
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

    /**
     * This API allows user to get their contact list. API By Calvin
     *
     * @param uuid
     * @return
     */
    @GET
    @Path("/accounts/{uuid}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getContactListOfAnUser(@PathParam("uuid") String uuid) {
        User user = dataStoreObject.get(User.class, uuid);

        List<Contact> contactList = dataStoreObject.find(Contact.class).field("user").equal(user).asList();

        initLogger();

        LOGGER.log(Level.CONFIG, "Contact Length {0}", contactList.size());

        List<UserContactBean> userList = new ArrayList();
        UserContactBean userContactBean;

        for (Contact contact : contactList) {
            userContactBean = new UserContactBean();
            User provider = contact.getProviderUser();
            Contact providerContact = dataStoreObject.find(Contact.class).filter("user =", provider).filter("qrCodeUuid =", contact.getQrCodeUuid()).get();

            userContactBean.setCompany(provider.getCompany());
            userContactBean.setUserName(provider.getUserName());
            userContactBean.setLocation(provider.getLocation());
            userContactBean.setCompany(provider.getCompany());
            userContactBean.setProfile(provider.getProfile());
            userContactBean.setPhoneNumber(provider.getPhoneNumber());
            userContactBean.setProviderIsEnable(isAllowedToCall(providerContact));
            userContactBean.setProfilePhotoId(provider.getProfilePhotoId());
            
            userContactBean.setAvailableEndTime(contact.getAvailableEndTime());
            userContactBean.setAvailableStartTime(contact.getAvailableStartTime());
            userContactBean.setChargeType(contact.getChargeType());
            userContactBean.setIsEnable(contact.getIsEnable());
            userContactBean.setCustomerIcon(contact.getCustomerIcon());
            userContactBean.setNickName(contact.getNickName());
            userContactBean.setQrCodeUuid(contact.getQrCodeUuid());
            userContactBean.setIsHigherPriorityThanGlobal(contact.getIsHigherPriorityThanGlobal());

            if (providerContact.getIsHigherPriorityThanGlobal()) {
                userContactBean.setProviderAvailableEndTime(providerContact.getAvailableEndTime());
                userContactBean.setProviderAvailableStartTime(providerContact.getAvailableStartTime());
            } else {
                userContactBean.setProviderAvailableEndTime(provider.getAvailableEndTime());
                userContactBean.setProviderAvailableStartTime(provider.getAvailableStartTime());
            }

            userList.add(userContactBean);
        }

        return Response.ok(userList).build();
    }

    /**
     * This API allows client user to update a contact. API By Calvin
     *
     * @param uuid
     * @param qrCodeUuid
     * @param nickName
     * @param availableStartTime
     * @param availableEndTime
     * @param isEnable
     * @param isHigherPriorityThanGlobal
     * @return
     */
    @PUT
    @Path("/accounts/{uuid}/contacts/{qrCodeUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response updateAcontactOfAnUser(
            @PathParam("uuid") String uuid,
            @PathParam("qrCodeUuid") String qrCodeUuid,
            @QueryParam("nickName") String nickName,
            @QueryParam("availableStartTime") String availableStartTime,
            @QueryParam("availableEndTime") String availableEndTime,
            @QueryParam("isEnable") String isEnable,
            @QueryParam("isHigherPriorityThanGlobal") String isHigherPriorityThanGlobal
    ) {
        User u = dataStoreObject.get(User.class, uuid);
        Contact modifiedContact = dataStoreObject.createQuery(Contact.class).filter("qrCodeUuid =", qrCodeUuid).filter("user =", u).get();

        initLogger();

        LOGGER.log(Level.CONFIG, "Save A Contact.{0}", nickName);

        if (nickName != null) {
            modifiedContact.setNickName(nickName);
        }

        if (isEnable != null) {
            if (isEnable.equalsIgnoreCase("true")) {
                modifiedContact.setIsEnable(true);
            } else {
                modifiedContact.setIsEnable(false);
            }

        }

        if (isHigherPriorityThanGlobal != null) {
            if (isHigherPriorityThanGlobal.equalsIgnoreCase("true")) {
                modifiedContact.setIsHigherPriorityThanGlobal(true);
            } else {
                modifiedContact.setIsHigherPriorityThanGlobal(false);
            }
        }

        if (availableStartTime != null) {
            modifiedContact.setAvailableStartTime(availableStartTime);
        }

        if (availableEndTime != null) {
            modifiedContact.setAvailableEndTime(availableEndTime);
        }

        dataStoreObject.save(modifiedContact);
        return Response.ok().build();
    }

    /**
     * This API allows user to add a contact. API By Calvin
     *
     * @param uuid
     * @param qrCodeUuid
     * @param contact
     * @return
     */
    @POST
    @Path("/accounts/{uuid}/contacts/{qrCodeUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response createNewContactOfAnUser(@PathParam("uuid") String uuid, @PathParam("qrCodeUuid") String qrCodeUuid, @NotNull @Valid Contact contact) {
        User owner = dataStoreObject.get(User.class, uuid);
        List<User> providers = dataStoreObject.createQuery(User.class).field("qrCodeUuid").equal(qrCodeUuid).asList();

        initLogger();

        LOGGER.log(Level.CONFIG, "Save A Contact.");

        List<Contact> contacts = dataStoreObject.createQuery(Contact.class).filter("qrCodeUuid =", qrCodeUuid).filter("user =", owner).asList();

        if (contacts.size() > 0) {
            // Owner has already add the provider as friend.
            return Response.notModified().build();
        }

        // user.size() must be 1.
        if (providers.size() == 1) {
            User provider = providers.get(0);
            if (contact.getChargeType() != 0) {
                // the contact of the scanner side.
                contact.setUser(owner);
                contact.setProviderUser(provider);
                contact.setQrCodeUuid(qrCodeUuid);
                contact.setIsEnable(true);
                contact.setChargeType(1);
                contact.setAvailableStartTime("00:00");
                contact.setAvailableEndTime("23:59");
                dataStoreObject.save(contact);

                // the contact of the provider side.
                contact.setId(new ObjectId());
                contact.setUser(provider);
                contact.setProviderUser(owner);
                contact.setNickName("");
                contact.setChargeType(2);

                dataStoreObject.save(contact);
            } else {
                // icon
                contact.setUser(provider);
                contact.setProviderUser(owner);
                contact.setQrCodeUuid(qrCodeUuid);
                contact.setIsEnable(true);
                contact.setChargeType(0);
                dataStoreObject.save(contact);
            }

            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

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
     * This API allows client to retrieve their QRCode API By Calvin
     *
     * @param uuid
     * @return
     * @throws java.io.IOException
     */
    @GET
    @Path("/accounts/{uuid}/qrcode")
    @Produces("image/png")
    @TokenRequired
    public Response getAccountQRCode(@PathParam("uuid") String uuid) throws IOException {
        // [Testing]
        byte[] qrCodeData;
        AmazonS3 s3Client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
        User user = dataStoreObject.get(User.class, uuid);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        String s3Bucket = "voice-in";
        String file = String.format("qrCode/%s.png", user.getQrCodeUuid());
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
     * Create user's QRCode by randomized UUID. API By Calvin
     *
     * @param uuid
     * @return
     */
    @POST
    @Path("/accounts/{uuid}/qrcode")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response generateQRCode(@PathParam("uuid") String uuid) {
        /**
         * QR Code Generator test*
         */
        User u = dataStoreObject.get(User.class, uuid);

        if (u.getQrCodeUuid() != null) {
            return Response.notModified().build();
        }

        String s3Bucket = "voice-in";
        String qrCodeUuid = UUID.randomUUID().toString();
        String s3FilePath = String.format("qrCode/%s.png", qrCodeUuid);

        // Generate QRCode Image and Upload to S3.
        File qrCodeImage = QRCode.from(qrCodeUuid).to(ImageType.PNG).withSize(250, 250).file();
        AmazonS3 s3Client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
        s3Client.putObject(new PutObjectRequest(s3Bucket, s3FilePath, qrCodeImage));
        u.setQrCodeUuid(qrCodeUuid);
        dataStoreObject.save(u);
        // save info to qrcode
        QRcode code = new QRcode();
        code.setProvider(u);
        code.setCreatedAt(new Date());
        code.setUpdateAt(code.getCreatedAt());
        code.setState(QRcodeType.STATE_ENABLED);
        code.setId(qrCodeUuid);
        code.setType(QRcodeType.TYPE_ACCOUNT);
        dataStoreObject.save(code);

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
            Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(getAvatar(avatarUuid, size)).build();
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
