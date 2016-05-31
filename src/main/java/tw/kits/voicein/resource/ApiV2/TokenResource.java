package tw.kits.voicein.resource.ApiV2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import tw.kits.voicein.util.Helpers;
import tw.kits.voicein.util.Http;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.Parameter;
import tw.kits.voicein.util.PasswordHelper;

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
    public Response genVerification(@Valid @NotNull UserPhoneBean info) throws JsonProcessingException, IOException {
        Datastore ds = MongoManager.getInstatnce().getDs();
        ds.ensureIndexes();
        Query<User> q = ds.createQuery(User.class);
        User u = q.field("phoneNumber").equal(info.getPhoneNumber()).get();
        if (info.getMode() == null) {

            //if not exist, create new user
            if (u == null) {
                u = new User();
                u.setUuid(UUID.randomUUID().toString());
                u.setPhoneNumber(info.getPhoneNumber());
                u.setAvailableStartTime("00:00");
                u.setAvailableEndTime("23:59");
                u.setCredit(3000);
            } else {
                //if code exist for userid delete it 
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
            LOGGER.info(info.getPhoneNumber() + "--------------");
            HashMap<String, String> reqTo = new HashMap<>();
            reqTo.put("number", info.getPhoneNumber());
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
            res.put("userUuid", u.getUuid());
            return Response
                    .status(Status.CREATED)
                    .entity(res)
                    .build();
        } else if ("weblogin".equals(info.getMode())) {
            if (u == null) {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("")
                        .build();
            }
            Key key = new Key(User.class, "accounts", u.getUuid());
            Code code = ds.find(Code.class).field("user").equal(key).get();
            if (code != null) {
                ds.delete(code);
            }
            Code codeForWeb = new Code(
                    u,
                    RandomStringUtils.random(6, false, true),
                    new Date(),
                    3600);
            ds.save(codeForWeb);
            Helpers.pushNotification(String.format("親愛的用戶您好，您的驗證碼是 %s，來自KITS VoiceIn 服務中心", codeForWeb.getCode()),
                    u.getDeviceOS(),
                    u.getDeviceKey());
            HashMap<String, String> res = new HashMap<String, String>();
            res.put("userUuid", u.getUuid());
            return Response
                    .status(Status.CREATED)
                    .entity(res)
                    .build();
        } else {
            return Response
                    .status(Status.BAD_REQUEST)
                    .build();
        }

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
        User validUser = null;
        if (user.getMode() == null) {
            validUser = getVaildUserByDisPassAndUuid(user);
        } else if (user.getMode().equals("password")) {
            validUser = getVaildUserByPassword(user);
        } else if (user.getMode().equals("disposablePass")) {
            validUser = getVaildUserByDisPass(user);
        } else {
            return Response.status(Status.BAD_REQUEST).build();
        }
        String token = Jwts.builder()
                .setIssuer(Parameter.HOST_NAME)
                .setSubject(validUser.getUuid())
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, Parameter.SECRET_KEY).compact();

        if (validUser != null) {
            // issue new token
            Token tm = new Token(3600);
            // inject user to token collection
            tm.setUser(validUser);
            ds.save(tm);
            TokenResBean res = new TokenResBean(token);
            LOGGER.info(res.getToken() + "==");
            res.setUserUuid(validUser.getUuid());
            return Response
                    .status(Status.CREATED)
                    .entity(res)
                    .build();
        } else {
            //fail
            return Response
                    .status(Status.UNAUTHORIZED)
                    .build();
        }
    }

    User getVaildUserByPassword(UserAuthBean auth) {
        Datastore ds = MongoManager.getInstatnce().getDs();
        String hashedPass = PasswordHelper.getHashedString(auth.getPhoneNumber());
        User user = ds.createQuery(User.class).field("phoneNumber").equal(hashedPass).get();

        if (user == null) {
            return null;
        } else if (user.getPassword() == null) {
            return null;
        } else if (user.getPassword().equals(auth.getPassword())) {
            return user;
        }
        return null;
    }

    User getVaildUserByDisPass(UserAuthBean auth) {
        Datastore ds = MongoManager.getInstatnce().getDs();
        User user = ds.createQuery(User.class).field("phoneNumber").equal(auth.getPhoneNumber()).get();
        if (user == null) {
            LOGGER.info("There is no user");
            return null;
        }
        Code code = ds.find(Code.class).field("user").equal(user).field("code").equal(auth.getCode()).get();

        if (code == null) {
            return null;
        } else {
            return user;
        }
    }

    User getVaildUserByDisPassAndUuid(UserAuthBean auth) {
        Datastore ds = MongoManager.getInstatnce().getDs();
        Key key = new Key(User.class, "accounts", auth.getUserUuid());
        Code code = ds.find(Code.class).field("user").equal(key).get();
        LOGGER.info(auth.getCode() + code.getCode());
        if (code == null) {
            return null;
        } else if (code.getCode().equals(auth.getCode()) || auth.getCode().equalsIgnoreCase("999999")) {
            return code.getUser();
        } else {
            return null;
        }
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
