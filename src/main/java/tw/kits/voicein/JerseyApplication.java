package tw.kits.voicein;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;


import org.glassfish.jersey.server.ServerProperties;
import org.jboss.logging.Logger;
import tw.kits.voicein.filter.KeySecurityFilter;
import tw.kits.voicein.filter.TokenSecurityFilter;
import tw.kits.voicein.util.Parameter;

public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {

        register(KeySecurityFilter.class);
        register(JacksonFeature.class);
        register(TokenSecurityFilter.class);
        register(MultiPartFeature.class);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        packages("tw.kits.voicein.resource.ApiV1");
        packages("tw.kits.voicein.resource.ApiV2");
        Logger logger = Logger.getLogger(JerseyApplication.class.getSimpleName());
        logger.log(Logger.Level.INFO, Parameter.DB_URI);
        logger.log(Logger.Level.INFO, Parameter.DB_NAME);
        logger.log(Logger.Level.INFO, Parameter.IS_SANDBOX);

    }
}
