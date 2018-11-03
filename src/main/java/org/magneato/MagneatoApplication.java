package org.magneato;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import java.net.UnknownHostException;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.magneato.core.ForbiddenExceptionMapper;
import org.magneato.core.JettyAuthServerFactory;
import org.magneato.health.EsClusterHealthCheck;
import org.magneato.health.EsIndexExistHealthCheck;
import org.magneato.managed.ManagedElasticClient;
import org.magneato.resources.HelloResource;
import org.magneato.resources.LoginResource;
import org.magneato.resources.LogoutResource;
import org.magneato.resources.PageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck.Result;

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
		// Map requests to /dashboard/${1} to be found in the class path at
		// /assets/${1}.
		bootstrap.addBundle(new ConfiguredAssetsBundle());
		bootstrap.addBundle(new ViewBundle());
		bootstrap.addBundle(new AssetsBundle("/assets/js", "/assets/js", null, "assets/js"));
	}

	@Override
	public void run(final MagneatoConfiguration configuration,
			final Environment environment) {
		try {
			final ManagedElasticClient managedClient = new ManagedElasticClient(
					configuration.getElasticSearch());
			environment.lifecycle().manage(managedClient);
			environment.healthChecks().register("ElasticSearch",
					new EsClusterHealthCheck(managedClient.getClient()));

			Result health = environment.healthChecks().runHealthCheck(
					"ElasticSearch");

			if (health.isHealthy()) {

				EsIndexExistHealthCheck indexCheck = new EsIndexExistHealthCheck(
						managedClient.getClient(), configuration
								.getElasticSearch().getIndexName());
				if (!indexCheck.isExist()) {
					// create index if not already existing
					managedClient.createIndex();
				}
				managedClient.createMapping();

				// Enable the Jersey security annotations on resources
				environment.jersey().getResourceConfig()
						.register(RolesAllowedDynamicFeature.class);

				// Register custom exception mapper to redirect 403 errors to
				// the login page
				environment.jersey().register(ForbiddenExceptionMapper.class);
				environment.jersey().register(
						new PageResource(configuration, managedClient));
				environment.jersey().register(new HelloResource());
				environment.jersey().register(new LoginResource());
				environment.jersey().register(new LogoutResource());
				environment.jersey().register(MultiPartFeature.class);
				environment.jersey().register(RolesAllowedDynamicFeature.class);

				JettyAuthServerFactory jasf = (JettyAuthServerFactory) configuration
						.getServerFactory();
				jasf.setConfiguration(configuration);
			} else {
				log.error("Elastic Search Not Found");
				System.exit(1);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
