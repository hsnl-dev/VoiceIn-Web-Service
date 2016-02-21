package tw.kits.voicein;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

import org.glassfish.jersey.server.ServerProperties;
import tw.kits.voicein.filter.KeySecurityFilter;

public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {
        // property(JspMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/classes");
        register(KeySecurityFilter.class);
        register(JspMvcFeature.class);
        register(JacksonFeature.class);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        packages("tw.kits.voicein.resource");
        
    
    }
}
