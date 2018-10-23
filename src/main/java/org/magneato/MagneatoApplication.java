package org.magneato;

import io.dropwizard.Application;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.magneato.core.ForbiddenExceptionMapper;
import org.magneato.core.JettyAuthServerFactory;
import org.magneato.resources.HelloResource;
import org.magneato.resources.LoginResource;
import org.magneato.resources.LogoutResource;
import org.magneato.resources.PageResource;

// https://github.com/spinscale/dropwizard-blog-sample/tree/master/src/main/java/services
public class MagneatoApplication extends Application<MagneatoConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MagneatoApplication().run(args);
    }

    @Override
    public String getName() {
        return "Magneato CMS";
    }

    @Override
    public void initialize(final Bootstrap<MagneatoConfiguration> bootstrap) {
        // Map requests to /dashboard/${1} to be found in the class path at /assets/${1}.
        bootstrap.addBundle(new ConfiguredAssetsBundle());
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(final MagneatoConfiguration configuration,
                    final Environment environment) {
    	// Enable the Jersey security annotations on resources
    	environment.jersey().getResourceConfig().register(RolesAllowedDynamicFeature.class);

    	// Register custom exception mapper to redirect 403 errors to the login page
    	environment.jersey().register(ForbiddenExceptionMapper.class);

    	environment.jersey().register(new PageResource(configuration));
    	environment.jersey().register(new HelloResource());
        environment.jersey().register(new LoginResource());
        environment.jersey().register(new LogoutResource());
        
        environment.jersey().register(MultiPartFeature.class);

/*    	environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                    .setAuthenticator(new ExampleAuthenticator())
                    .setAuthorizer(new ExampleAuthorizer())
                    .setRealm("SUPER SECRET STUFF")
                    .buildAuthFilter()));*/
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        
        JettyAuthServerFactory jasf = (JettyAuthServerFactory) configuration.getServerFactory();
        jasf.setConfiguration(configuration);

        //If you want to use @Auth to inject a custom Principal type into your resource
        //environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

    }
}
