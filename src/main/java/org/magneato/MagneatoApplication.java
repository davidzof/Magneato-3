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
import org.magneato.service.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

// https://github.com/spinscale/dropwizard-blog-sample/tree/master/src/main/java/services
public class MagneatoApplication extends Application<MagneatoConfiguration> {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

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
        Repository es = null;
        try {
            // TODO Move this code to a managed service, or something
            es = new Repository(configuration.getElasticSearch().getClusterName(), configuration.getElasticSearch().getHostname(), configuration.getElasticSearch().getPort());
            if (es != null && es.isHealthy()) {
                if (es.isIndexRegistered(configuration.getElasticSearch().getIndexName())) {
                    // create index if not already existing
               //     es.createIndex(configuration.getElasticSearch().getIndexName(), configuration.getElasticSearch().getNumberOfShards(), configuration.getElasticSearch().getNumberOfReplicas());
                }

                // Enable the Jersey security annotations on resources
                environment.jersey().getResourceConfig().register(RolesAllowedDynamicFeature.class);

                // Register custom exception mapper to redirect 403 errors to the login page
                environment.jersey().register(ForbiddenExceptionMapper.class);

                environment.jersey().register(new PageResource(configuration, es));
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
            } else {
                log.error("Elastic Search Not Found");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
