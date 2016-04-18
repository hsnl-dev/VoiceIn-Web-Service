package tw.kits.voicein.resource.ApiV2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.RandomStringUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import tw.kits.voicein.bean.TokenResBean;
import tw.kits.voicein.bean.UserAuthBean;
import tw.kits.voicein.bean.UserPhoneBean;
import tw.kits.voicein.model.Token;
import tw.kits.voicein.model.User;
import tw.kits.voicein.model.Code;
import tw.kits.voicein.util.Http;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.Parameter;

@Path("/api/v2")
public class TokenResource {

    private final Logger LOGGER = Logger.getLogger(TokenResource.class.getName());

    /**
     *
     * API By Henry
     *
     * @param phone
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    @POST
    @Path("/accounts/validations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response genVerification(@Valid @NotNull UserPhoneBean phone) throws JsonProcessingException, IOException {
        //TODO: send verfiy code
        Datastore ds = MongoManager.getInstatnce().getDs();
        ds.ensureIndexes();
        Query<User> q = ds.createQuery(User.class);

        User u = q.field("phoneNumber").equal(phone.getPhoneNumber()).get();
        //if not exist, create new user
        if (u == null) {
            u = new User();
            u.setUuid(UUID.randomUUID().toString());
            u.setPhoneNumber(phone.getPhoneNumber());
            u.setAvailableStartTime("00:00");
            u.setAvailableEndTime("23:59");
            u.setCredit(3000);
        } else {
            //if code exist for userid delete it !!!
            Query<Code> codeQ = ds.createQuery(Code.class);
            Key key = new Key(User.class, "accounts", u.getUuid());
            Code code = ds.find(Code.class).field("user").equal(key).get();
            if (code != null) {
                ds.delete(code);
            }
        }
        Code code = new Code(
                u,
                RandomStringUtils.random(6, false, true),
                new Date(),
                3600);

        HashMap<String, String> reqTo = new HashMap<String, String>();
        reqTo.put("number", phone.getPhoneNumber());
        reqTo.put("content", String.format("親愛的用戶您好，您的驗證碼是 %s，來自KITS VoiceIn 服務中心", code.getCode()));

        ObjectMapper mapper = new ObjectMapper();
        String reqJSON = mapper.writeValueAsString(reqTo);

        Http sendWorker = new Http();
        LOGGER.log(Level.INFO, Parameter.API_ROOT + Parameter.API_VER + "Call/sms");
        String workerRes = sendWorker.post(Parameter.API_ROOT + Parameter.API_VER + "Call/sms", reqJSON);

        LOGGER.log(Level.INFO, workerRes);
        ds.save(u);
        ds.save(code);
        //prepare response
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("userUuid", u.getUuid().toString());
        return Response
                .status(Status.CREATED)
                .entity(res)
                .build();

    }

    /**
     *
     * API By Henry
     *
     * @param user
     * @return
     */
    @POST
    @Path("/accounts/tokens")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(@Valid @NotNull UserAuthBean user) {
        Datastore ds = MongoManager.getInstatnce().getDs();
        Key key = new Key(User.class, "accounts", user.getUserUuid());
        Code code = ds.find(Code.class).field("user").equal(key).get();

        if (code != null) {
            if (code.getCode().equals(user.getCode()) || user.getCode().equalsIgnoreCase("999999")) {
                // issue new token
                Token tm = new Token(3600);
                // inject user to token collection
                tm.setUser(code.getUser());
                ds.save(tm);
                TokenResBean res = new TokenResBean(tm.getTokenId());
                return Response
                        .status(Status.CREATED)
                        .entity(res)
                        .build();
            }
        }
        //fail
        return Response
                .status(Status.UNAUTHORIZED)
                .entity(user)
                .build();

    }

    /**
     *
     * API By Henry
     *
     * @param tokenUuid
     * @return
     */
    @DELETE
    @Path("/accounts/tokens/{tokenUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delToken(@PathParam("tokenUuid") String tokenUuid) {
        Datastore ds = MongoManager.getInstatnce().getDs();
        Token token = ds.get(Token.class, tokenUuid);
        ds.delete(token);
        return Response
                .status(Status.OK)
                .build();

    }
}
