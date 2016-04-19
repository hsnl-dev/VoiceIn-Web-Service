package org.netbeans.rest.application.config;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Calvin
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(tw.kits.voicein.filter.KeySecurityFilter.class);
        resources.add(tw.kits.voicein.filter.TokenSecurityFilter.class);
        resources.add(tw.kits.voicein.resource.ApiV1.AccountAvatarsResource.class);
        resources.add(tw.kits.voicein.resource.ApiV1.AccountContactsResource.class);
        resources.add(tw.kits.voicein.resource.ApiV1.AccountGroupsResource.class);
        resources.add(tw.kits.voicein.resource.ApiV1.AccountQRcodesResource.class);
        resources.add(tw.kits.voicein.resource.ApiV1.AccountsResource.class);
        resources.add(tw.kits.voicein.resource.ApiV1.CallingServiceResource.class);
        resources.add(tw.kits.voicein.resource.ApiV1.IconResource.class);
        resources.add(tw.kits.voicein.resource.ApiV1.SandboxResource.class);
        resources.add(tw.kits.voicein.resource.ApiV1.TokenResource.class);
        resources.add(tw.kits.voicein.resource.ApiV1.WelcomeResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.AccountAvatarsResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.AccountContactsResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.AccountGroupsResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.AccountQRcodesResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.AccountsResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.CallingServiceResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.IconResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.NotificationResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.SandboxResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.TokenResource.class);
        resources.add(tw.kits.voicein.resource.ApiV2.WelcomeResource.class);
    }
    
}
