package tw.kits.voicein.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import tw.kits.voicein.bean.ErrorMessageBean;

@Provider
@PreMatching
@Priority(Priorities.AUTHORIZATION)
public class KeySecurityFilter implements ContainerRequestFilter {

    private final static Logger log = Logger.getLogger("tw.kits.voicein.filter.KeySecurityFilter");
    private final static HashMap<String, String> KEYSTORE;

    static {
        log.info("initiate key store");
        KEYSTORE = new HashMap<String, String>();
        KEYSTORE.put("784a48e7-a15f-4623-916a-1bd304dc9f56", "android-client");
        KEYSTORE.put("f4c34db9-c4f8-4356-9442-51ece7adca67", "iOS-client");
        KEYSTORE.put("def278b0-4414-4914-8337-31d9a0b43082", "web-client");

    }

    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        if ("api/v1/".equals(crc.getUriInfo().getPath())) {
            return;
        }

        if (crc.getRequest().getMethod().equals("OPTIONS")) {
            crc.abortWith(Response.status(Response.Status.OK).build());
            return;
        }

        String apiKey = crc.getHeaderString("apiKey");

        if (apiKey == null) {
            ErrorMessageBean emb = new ErrorMessageBean();
            emb.setErrorReason("api key is not allowed or must not be null");
            crc.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(emb)
                    .build());

        } else if (!KEYSTORE.containsKey(apiKey)) {
            ErrorMessageBean emb = new ErrorMessageBean();
            emb.setErrorReason("api key is not allowed or must not be null");
            crc.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(emb)
                    .build());
        }

    }

}
