package org.magneato;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;
import io.dropwizard.bundles.webjars.WebJarBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import java.net.UnknownHostException;

import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.magneato.core.ForbiddenExceptionMapper;
import org.magneato.core.JettyAuthServerFactory;
import org.magneato.health.EsClusterHealthCheck;
import org.magneato.health.EsIndexExistHealthCheck;
import org.magneato.managed.ManagedElasticClient;
import org.magneato.resources.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck.Result;

// https://github.com/spinscale/dropwizard-blog-sample/tree/master/src/main/java/services
public class MagneatoApplication extends Application<org.magneato.MagneatoConfiguration> {
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
		bootstrap.addBundle(new ConfiguredAssetsBundle());
		bootstrap.addBundle(new ViewBundle()); // ?? what does this do?
		bootstrap.addBundle(new AssetsBundle("/assets/js", "/assets/js", null, "assets/js"));
		bootstrap.addBundle(new WebJarBundle());
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
				//managedClient.createSettings();
				managedClient.createMappings();

				// Enable the Jersey security annotations on resources
				environment.jersey().getResourceConfig()
						.register(RolesAllowedDynamicFeature.class);

				// Register custom exception mapper to redirect 403 errors to
				// the login page
				environment.jersey().register(ForbiddenExceptionMapper.class);
				environment.jersey().register(
						new PageResource(configuration, managedClient));
				environment.jersey().register(
						new UploadResource(configuration, managedClient));
				environment.jersey().register(new SearchResource(managedClient));
				environment.jersey().register(new LoginResource());
				environment.jersey().register(new LogoutResource());
				environment.jersey().register(MultiPartFeature.class);
				environment.jersey().register(RolesAllowedDynamicFeature.class);

				// custom 404
				ErrorPageErrorHandler eph = new ErrorPageErrorHandler();
				eph.addErrorPage(404, "/error/404");
				environment.getApplicationContext().setErrorHandler(eph);

				JettyAuthServerFactory jasf = (JettyAuthServerFactory) configuration
						.getServerFactory();
				jasf.setConfiguration(configuration);
			} else {
				log.error("Elastic Search Not Found");
				System.exit(1);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			System.exit(1);
		}

	}
}
