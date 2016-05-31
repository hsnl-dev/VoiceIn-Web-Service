package tw.kits.voicein.resource.ApiV2.account;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import tw.kits.voicein.bean.CustomQRcodeCreateBean;
import tw.kits.voicein.bean.ProviderResBean;
import tw.kits.voicein.bean.QRcodeNoProviderBean;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.Parameter;
import tw.kits.voicein.util.QRcodeType;
import tw.kits.voicein.util.TokenRequired;

@MultipartConfig(maxFileSize = 1024 * 1024 * 1)
@Path("/api/v2")
public class QRcodesResource {

    @Context
    SecurityContext mContext;
    static final Logger LOGGER = Logger.getLogger(QRcodesResource.class.getName());
//    private String tokenUser = context.getUserPrincipal().getName(); //user id of token
    ConsoleHandler consoleHandler = new ConsoleHandler();
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dataStoreObject = mongoManager.getDs();

    /**
     * This API allows client to get provider 's information by
     * qrCodeUuid(providerId) API By Henry Searching in QRCode Collection to get
     * provider 's information.
     *
     * @param uProviderId
     * @return
     */
    @GET
    @Path("/providers/{providerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIconProvider(@PathParam("providerId") String uProviderId) {
        QRcode code = dataStoreObject.get(QRcode.class, uProviderId);
        User user = code.getProvider();
        ProviderResBean prb = new ProviderResBean();
        prb.setName(user.getUserName());
        prb.setCompany(user.getCompany());
        prb.setLocation(user.getLocation());
        prb.setProfile(user.getProfile());
        prb.setAvatarId(user.getProfilePhotoId());
        prb.setCustomerName(code.getUserName());
        prb.setCustomerCompany(code.getCompany());
        prb.setCustomerLocation(code.getLocation());
        prb.setCustomerPhoneNum(code.getPhoneNumber());
        prb.setType(code.getType());
        prb.setState(code.getState());
        return Response.ok(prb).build();
    }

    // ========= Normal QRCode Section ===========
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
    public Response getAccountQRCode(@Context Request req, @PathParam("uuid") String uuid) throws IOException {
        // [Testing]
        byte[] qrCodeData;
        AmazonS3 s3Client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
        User user = dataStoreObject.get(User.class, uuid);
        EntityTag tag = new EntityTag(user.getQrCodeUuid());
        Response.ResponseBuilder responseBuilder = req.evaluatePreconditions(tag);
        if (responseBuilder != null) {
            return responseBuilder.build();
        }

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String s3Bucket = "voice-in";
        String file = String.format("%s/%s.png", Parameter.S3_QR_CODE_FOLDER ,user.getQrCodeUuid());
        LOGGER.warning("file name is " +file);
        GetObjectRequest request = new GetObjectRequest(s3Bucket, file);
        S3Object object = s3Client.getObject(request);
        qrCodeData = IOUtils.toByteArray(object.getObjectContent());

        /*
        try (FileOutputStream fos = new FileOutputStream("/Volumes/JetDrive/GoogleDrive/Projects/voicein/voicein-api/test.png")) {
            fos.write(qrCodeData);
            fos.close();
        }*/
        return Response.ok(qrCodeData).tag(tag).build();
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
        String s3FilePath = String.format("%s/%s.png",Parameter.S3_QR_CODE_FOLDER, qrCodeUuid);

        // Generate QRCode Image and Upload to S3.
        File qrCodeImage = QRCode.from(Parameter.WEB_SITE_QRCODE + qrCodeUuid).to(ImageType.PNG).withSize(250, 250).file();
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

    // ========== customize qrCode section ============
    /**
     * *
     * To list of get special QR-Code of customer
     *
     * @author Henry
     * @param uuid
     * @return
     */
    @GET
    @Path("/accounts/{uuid}/customQrcodes")
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getAccountCustomQRcodes(@PathParam("uuid") String uuid) {
        String tokenAccount = mContext.getUserPrincipal().getName();
        Key<User> key = new Key(User.class, "accounts", tokenAccount);
        List<QRcode> qrcodes = dataStoreObject.createQuery(QRcode.class)
                .field("provider")
                .equal(key)
                .field("type")
                .equal(QRcodeType.TYPE_SPECIAL)
                .asList();
        List<QRcodeNoProviderBean> res = new ArrayList();
        for (QRcode code : qrcodes) {
            res.add(new QRcodeNoProviderBean(code));
        }
        HashMap<String, Object> response = new HashMap();

        response.put("qrcodes", res);
        return Response.ok(response).build();
    }

    /**
     * *
     * To update a special QR-Code of customer
     *
     * @author Henry
     * @param uuid
     * @param qrCode
     * @param info
     * @return
     */
    @PUT
    @Path("/accounts/{uuid}/customQrcodes/{qrcodeid}")
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response modifyAccountCustomQRcodes(@PathParam("uuid") String uuid, @PathParam("qrcodeid") String qrCode,
            @Valid @NotNull CustomQRcodeCreateBean info) {
        if (!Helpers.isUserMatchToken(uuid, mContext)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        QRcode code = dataStoreObject.get(QRcode.class, qrCode);
        if (!code.getProvider().getUuid().equals(uuid)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        code.setPhoneNumber(info.getPhoneNumber());
        code.setCompany(info.getCompany());
        code.setLocation(info.getLocation());
        code.setUserName(info.getName());
        code.setUpdateAt(new Date());
        dataStoreObject.save(code);
        return Response.ok().build();
    }

    /**
     * *
     * To create special QR-Code for customer
     *
     * @author Henry
     * @param uuid
     * @param info
     * @return
     */
    @POST
    @Path("/accounts/{uuid}/customQrcodes")
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response createSpecifiedQrcodes(@PathParam("uuid") String uuid, @Valid @NotNull CustomQRcodeCreateBean info) {
        User user = dataStoreObject.get(User.class, mContext.getUserPrincipal().getName());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String s3Bucket = "voice-in";
        String qrCodeUuid = UUID.randomUUID().toString();
        String s3FilePath = String.format("%s/%s.png",Parameter.S3_QR_CODE_FOLDER, qrCodeUuid);

        // Generate QRCode Image and Upload to S3.
        File qrCodeImage = QRCode.from(Parameter.WEB_SITE_QRCODE + qrCodeUuid).to(ImageType.PNG).withSize(250, 250).file();
        AmazonS3 s3Client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
        s3Client.putObject(new PutObjectRequest(s3Bucket, s3FilePath, qrCodeImage));

        QRcode code = new QRcode();
        code.setId(qrCodeUuid);
        code.setPhoneNumber(info.getPhoneNumber());
        code.setLocation(info.getLocation());
        code.setCompany(info.getCompany());
        code.setProvider(user);
        code.setType(QRcodeType.TYPE_SPECIAL);
        code.setState(QRcodeType.STATE_ENABLED);
        Date date = new Date();
        code.setCreatedAt(date);
        code.setUpdateAt(date);
        code.setUserName(info.getName());

        dataStoreObject.save(code);
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * To delete the custom QR-Code of an customer.
     *
     * @param uuid
     * @param qrCode
     * @return
     */
    @DELETE
    @Path("/accounts/{uuid}/customQrcodes/{qrcodeid}")
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response delAccountCustomQRcodes(@PathParam("uuid") String uuid, @PathParam("qrcodeid") String qrCode) {
        if (!Helpers.isUserMatchToken(uuid, mContext)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        QRcode code = dataStoreObject.get(QRcode.class, qrCode);

        if (QRcodeType.TYPE_ACCOUNT.equals(code.getType()) || !code.getProvider().getUuid().equals(uuid)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String s3Bucket = "voice-in";
        String s3FilePath = String.format("%s/%s.png", Parameter.S3_QR_CODE_FOLDER,code.getId());
        AmazonS3 s3Client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
        s3Client.deleteObject(s3Bucket, s3FilePath);
        dataStoreObject.delete(code);

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
    @Path("/qrcodes/{uuid}/image")
    @Produces("image/png")
    public Response getQRCodeImgById(@Context Request cilentRequest, @PathParam("uuid") String uuid) throws IOException {
        byte[] qrCodeData;
        QRcode code = dataStoreObject.get(QRcode.class, uuid);

        if (code == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        EntityTag tag = new EntityTag(code.getUpdateAt().getTime() + "");
        Response.ResponseBuilder responseBuilder = cilentRequest.evaluatePreconditions(tag);
        if (responseBuilder != null) {
            return responseBuilder.build();
        }
        AmazonS3 s3Client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
        String s3Bucket = "voice-in";
        String file = String.format("%s/%s.png",Parameter.S3_QR_CODE_FOLDER, uuid);
        GetObjectRequest request = new GetObjectRequest(s3Bucket, file);
        S3Object object = s3Client.getObject(request);
        qrCodeData = IOUtils.toByteArray(object.getObjectContent());

        return Response.ok(qrCodeData).tag(tag).build();
    }
}
