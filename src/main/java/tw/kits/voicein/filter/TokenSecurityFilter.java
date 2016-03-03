package tw.kits.voicein.filter;

import java.io.IOException;
import java.security.Principal;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.model.Token;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.Parameter;
import tw.kits.voicein.util.TokenRequired;

/**
 *
 * @author Henry
 */
@TokenRequired
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class TokenSecurityFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        Datastore ds = MongoManager.getInstatnce().getDs();
        String token = crc.getHeaderString("token");
        final Token tm2 = ds.get(Token.class, token);

        if (tm2 == null && !Parameter.IS_SANDBOX) {

            ErrorMessageBean errMsg = new ErrorMessageBean();
            errMsg.setErrorReason("Your code is not correct");
            crc.abortWith(
                    Response.status(Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(errMsg)
                    .build()
            );
        }

        crc.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return new Principal() {
                    @Override
                    public String getName() {
                        return tm2.getUser().getUuid();
                    }
                };
            }

            @Override
            public boolean isUserInRole(String string) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        });

    }
}
