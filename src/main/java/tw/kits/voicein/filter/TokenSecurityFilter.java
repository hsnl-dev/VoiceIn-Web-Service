package tw.kits.voicein.filter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.security.Principal;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import tw.kits.voicein.bean.ErrorMessageBean;
import tw.kits.voicein.resource.ApiV2.PaymentResource;
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
        final String token = crc.getHeaderString("token");
        if (!Parameter.IS_SANDBOX) {
            if (token == null) {
                System.out.println("");
                ErrorMessageBean errMsg = new ErrorMessageBean();
                errMsg.setErrorReason("Token is required");
                crc.abortWith(
                        Response.status(Status.UNAUTHORIZED)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(errMsg)
                        .build()
                );
            }

            try {
                final String userid = Jwts.parser().setSigningKey(Parameter.SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
                 final Logger LOGGER = Logger.getLogger(PaymentResource.class.getName());
                 LOGGER.info(userid+"userid");
                 LOGGER.info(token+"token");
                crc.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        return new Principal() {
                            @Override
                            public String getName() {
                                return userid;
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

            } catch (JwtException e) {
                ErrorMessageBean errMsg = new ErrorMessageBean();
                errMsg.setErrorReason("Your code is not correct");
                crc.abortWith(
                        Response.status(Status.UNAUTHORIZED)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(errMsg)
                        .build()
                );
            }

        }

    }
}
