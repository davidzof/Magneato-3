package org.magneato.resources;

import io.dropwizard.bundles.assets.AssetsConfiguration;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

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

public class UploadResourceTest {
	org.magneato.resources.UploadResource uploadResource;
	private static final String content1 = "{\"title_c\":\"Tour of the Crêt de Chazay\", \"activity_c\":\"Mountain Biking\", \"trip_date\":\"08/05/2015\", \"content\":\"<p>From the parking ride into Brignoud ...\", \"difficulty_c\":{\"rating\":\"2\"},\"technical_c\":{\"imperial\":\"false\", \"orientation\":\"Various\", \"max\":889, \"min\":225, \"distance\":24, \"climb\":750},\"files\": [{\"name\":\"a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"size\":\"46957\",\"url\":\"/library/images/a5e/e7a/a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"thumbnailUrl\":\"/library/gpxIcon.jpg\",\"deleteUrl\":\"/delete/images/a5e/e7a/a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"deleteType\":\"DELETE\"}], \"metadata\" : {\"canonical_url\":\"tour-of-the-cret-de-chazay\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2015-08-05 08:49:14\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"perms\":11275}}";
	private static final String content2 = "{\"title_c\":\"Tour of the Crêt de Chazay\", \"activity_c\":\"Mountain Biking\", \"trip_date\":\"08/05/2015\",  \"difficulty\":{\"rating_c\":\"2\"}, \"metadata\" : {\"canonical_url\":\"tour-of-the-cret-de-chazay\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2015-08-05 08:49:14\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"perms\":11275}}";
	private static final String content = "{\"title\":\"Champagne Chartreuse\",\"activity_c\":\"Ski Touring\",\"trip_date\":\"03/03/2019\",\"content\":\"<p>Powder in March is pretty short lives, here today, gone tomorrow. It snowed lightly on Friday and Saturday night so I decided to take advantage of the good weather on Sunday to do a rapid Couloir en Virgule. I didn't have high hopes for the couloir but the Vallon de Marcieu at the top holds cold snow well on its west flank and that is exactly what I found. The depths varied, probably around 10cm of snow at 1800 meters but there were accumulations of 20cm in places.</p><p>The top of the couloir was well filled, it was possible to ski right from the entrance as the fences are buried. I took the small steep side couloir at the bottom but not really sure this was better than the main couloir, the snow was soft at this point. Conditions improved in the woods with the fresh snow smoothing out any imperfections.<br></p>\",\"conditions\":\"<p>Marcieu: 10-15cm of fresh snow that had fallen without much wind. Accumulations of snow in places giving good skiing.</p><p>Couloir: Fresh snow</p><p>Forest: Fresh snow but a bit soft</p><p>Path: some bare patches appearing<br></p><p>Pistes: Still frozen in places at 9.30am<br></p>\",\"ski_difficulty_c\":{\"rating\":\"4.1\",\"bra\":\"2\",\"snowline\":1000},\"technical_c\":{\"max\":1860,\"min\":1060,\"distance\":6.65,\"climb\":800,\"location\":{\"lat\":\"45.356037\",\"lon\":\"5.91864\"},\"orientation\":\"East\"},\"files\":[{\"name\":\"Couloir_Virgule_freshies.gpx\",\"size\":\"177113\",\"url\":\"/library/images/Cou/Couloir_Virgule_freshies.gpx\",\"thumbnailUrl\":\"/library/images/Cou/null\",\"deleteUrl\":\"/delete/Cou/Couloir_Virgule_freshies.gpx\",\"deleteType\":\"DELETE\"},{\"name\":\"3marvirgule.jpg\",\"size\":\"381722\",\"url\":\"/library/images/3ma/3marvirgule.jpg\",\"thumbnailUrl\":\"/library/images/3ma/thumb_3marvirgule.jpg\",\"deleteUrl\":\"/delete/3ma/3marvirgule.jpg\",\"deleteType\":\"DELETE\"},{\"name\":\"3marvirgule3.jpg\",\"size\":\"447376\",\"url\":\"/library/images/3ma/3marvirgule3.jpg\",\"thumbnailUrl\":\"/library/images/3ma/thumb_3marvirgule3.jpg\",\"deleteUrl\":\"/delete/3ma/3marvirgule3.jpg\",\"deleteType\":\"DELETE\"},{\"name\":\"3marvirgule2.jpg\",\"size\":\"398123\",\"url\":\"/library/images/3ma/3marvirgule2.jpg\",\"thumbnailUrl\":\"/library/images/3ma/thumb_3marvirgule2.jpg\",\"deleteUrl\":\"/delete/3ma/3marvirgule2.jpg\",\"deleteType\":\"DELETE\"}],\"metadata\":{\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2019-03-03 09:12:27\",\"ip_addr\":\"127.0.0.1\",\"owner\":\"davidof\",\"canonical_url\":\"champagne-chartreuse\",\"perms\":11275,\"relations\":[\"rb9c8be8816f5\"],\"groups\":[\"editors\"]}}";

	private final static ObjectMapper objectMapper = new ObjectMapper();
	HttpServletRequest request;
	SecurityContext security;
	PageUtils pageUtils;

	@Before
	public void setUp() {
		pageUtils = new PageUtils();
		MockitoAnnotations.initMocks(this);
	}

	private UploadResource createMocks() {
		ManagedElasticClient repository = Mockito
				.mock(ManagedElasticClient.class);
		MagneatoConfiguration configuration = Mockito
				.mock(MagneatoConfiguration.class);
		AssetsConfiguration assetsConfiguration = Mockito
				.mock(AssetsConfiguration.class);
		request = Mockito.mock(HttpServletRequest.class);
		security = Mockito.mock(SecurityContext.class);

		Mockito.when(request.getHeader("referer"))
				.thenReturn(
						"http://localhost:9090/r95eec7d13e7a/skating-to-the-cret-luisard");

		
		Map<String, String> overrides = new HashMap<String, String>();
		overrides.put(UploadResource.IMAGEPATH, "imagedir");
		Mockito.when(configuration.getAssetsConfiguration())
		.thenReturn(assetsConfiguration);
		Mockito.when(assetsConfiguration.getOverrides())
				.thenReturn(overrides);
		Mockito.when(repository.get("r95eec7d13e7a")).thenReturn(content1);
		Mockito.when(repository.get("r95eec7d13e7b")).thenReturn(content2);
		Principal principal = new UserPrincipal("testuser", null);
		Mockito.when(security.getUserPrincipal()).thenReturn(principal);

		return new UploadResource(configuration, repository);
	}

	@Test
	public void testIdtoShortName() {
		UploadResource uR = createMocks();
		long time = System.currentTimeMillis();
		String s = uR.idToShortURL(time);
		System.out.println(s);
	}
}
