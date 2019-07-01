package org.magneato.resources;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.jetty.security.AbstractLoginService.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.magneato.MagneatoConfiguration;
import org.magneato.managed.ManagedElasticClient;
import org.magneato.utils.PageUtils;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageResourceTest {
	org.magneato.resources.PageResource pageResource;
	private static final String content1 = "{\"title_c\":\"Tour of the Crêt de Chazay\", \"activity_c\":\"Mountain Biking\", \"trip_date\":\"08/05/2015\", \"content\":\"<p>From the parking ride into Brignoud ...\", \"difficulty_c\":{\"rating\":\"2\"},\"technical_c\":{\"imperial\":\"false\", \"orientation\":\"Various\", \"max\":889, \"min\":225, \"distance\":24, \"climb\":750},\"files\": [{\"name\":\"a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"size\":\"46957\",\"url\":\"/library/images/a5e/e7a/a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"thumbnailUrl\":\"/library/gpxIcon.jpg\",\"deleteUrl\":\"/delete/images/a5e/e7a/a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"deleteType\":\"DELETE\"}], \"metadata\" : {\"canonical_url\":\"tour-of-the-cret-de-chazay\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2015-08-05 08:49:14\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"perms\":11275}}";
	private static final String content2 = "{\"title_c\":\"Tour of the Crêt de Chazay\", \"activity_c\":\"Mountain Biking\", \"trip_date\":\"08/05/2015\",  \"difficulty\":{\"rating_c\":\"2\"}, \"metadata\" : {\"canonical_url\":\"tour-of-the-cret-de-chazay\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2015-08-05 08:49:14\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"perms\":11275}}";
	private final static ObjectMapper objectMapper = new ObjectMapper();
	HttpServletRequest request;
	SecurityContext security;
	PageUtils pageUtils;

	@Before
	public void setUp() {
		pageUtils = new PageUtils();
		MockitoAnnotations.initMocks(this);
	}

	private PageResource createMocks() {
		ManagedElasticClient repository = Mockito
				.mock(ManagedElasticClient.class);
		MagneatoConfiguration configuration = Mockito
				.mock(MagneatoConfiguration.class);
		request = Mockito.mock(HttpServletRequest.class);
		security = Mockito.mock(SecurityContext.class);

		Mockito.when(request.getHeader("referer"))
				.thenReturn(
						"http://localhost:9090/r95eec7d13e7a/skating-to-the-cret-luisard");

		Mockito.when(repository.get("r95eec7d13e7a")).thenReturn(content1);
		Mockito.when(repository.get("r95eec7d13e7b")).thenReturn(content2);
		Principal principal = new UserPrincipal("testuser", null);
		Mockito.when(security.getUserPrincipal()).thenReturn(principal);

		
		return new PageResource(configuration, repository);
	}

	@Test
	public void simpleCase() throws IOException {
		PageResource pageResource = createMocks();
		JsonNode rootNode = objectMapper.reader().readTree(content1);
		String cloned = pageUtils.cloneContent(rootNode.toString());
		System.out.println(cloned);
	}

	@Test
	public void clonedObjectElement() throws IOException {
		PageResource pageResource = createMocks();
		JsonNode rootNode = objectMapper.reader().readTree(content2);

		String cloned = pageUtils.cloneContent(rootNode.toString());
		System.out.println("cloned " + cloned);
	}

	@Test
	public void createSimpleChildPage() throws IOException {
		PageResource pageResource = createMocks();
		EditView view = (EditView) pageResource.create(false, true,
				"simple.ftl", "display.simple.ftl", request, security);
		System.out.println("View " + view.getMetaData());
	}

	@Test
	public void createSimpleClonePage() throws IOException {
		PageResource pageResource = createMocks();
		EditView view = (EditView) pageResource.create(true, true,
				"simple.ftl", "display.simple.ftl", request, security);
		System.out.println(">>> " + view.getBody());
	}
}
