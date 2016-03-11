package tw.kits.voicein.resource.ApiV1;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.Parameter;
import tw.kits.voicein.util.TokenRequired;

/**
 * This apis is to access qrcode icons
 * @author Henry
 */
@Path("/api/v1")
public class QRcodeResource {
    private final Datastore dsObj = MongoManager.getInstatnce().getDs();
    /***
     * get qrcode info
     * @author Henry
     * @param qrUuid
     * @return 
     */
    @GET
    @Path("/qrcodes/{qruuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQrcodeInfo(@PathParam("qruuid")String qrUuid){
        QRcode code = dsObj.get(QRcode.class, qrUuid);
        if(code==null){
            //following code is to old Account QRcode!! 
            User user = dsObj.createQuery(User.class).field("qrCodeUuid").equal(qrUuid).get();
            if(user==null){
                System.out.println("sdadsda");
               return Response.status(Response.Status.NOT_FOUND).build();
            }else{ 
               QRcode userCode = new QRcode();
               userCode.setProvider(user);
               return Response.ok(userCode).build();
            }
        }
        return Response.ok(code).build();
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
    public Response getQRCodeImgById(@PathParam("uuid") String uuid) throws IOException {
        byte[] qrCodeData;
        AmazonS3 s3Client = new AmazonS3Client(Parameter.AWS_CREDENTIALS);
        String s3Bucket = "voice-in";
        String file = String.format("qrCode/%s.png", uuid);
        GetObjectRequest request = new GetObjectRequest(s3Bucket, file);
        S3Object object = s3Client.getObject(request);
        qrCodeData = IOUtils.toByteArray(object.getObjectContent());
    
        return Response.ok(qrCodeData).build();
    }
}
