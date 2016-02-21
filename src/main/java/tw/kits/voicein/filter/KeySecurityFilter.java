package tw.kits.voicein.filter;

import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import tw.kits.voicein.bean.ErrorMessageBean;

@Provider
@PreMatching
public class KeySecurityFilter implements ContainerRequestFilter{
    private final static Logger log = Logger.getLogger("tw.kits.voicein.filter.KeySecurityFilter");
    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        if ( crc.getRequest().getMethod().equals( "OPTIONS" ) ) {
            crc.abortWith( Response.status( Response.Status.OK ).build() );

            return;
        }
        String apiKey = crc.getHeaderString("apiKey");
        if(apiKey == null){
            ErrorMessageBean emb = new ErrorMessageBean();
            emb.setErrorReason("api key is not allowed or must not be null");
            crc.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(emb)
                        .build());

        }
              
    }
    
}
