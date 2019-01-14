package org.magneato.resources;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.magneato.MagneatoConfiguration;
import org.magneato.managed.ManagedElasticClient;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class PageResourceTest {
    PageResource pageResource;
    private static final String content1 = "{\"_index\":\"main-index\",\"_type\":\"_doc\",\"_id\":\"r95eec7d13e7a\",\"_score\":1,\"_source\":{\"title_c\":\"Tour of the Crêt de Chazay\", \"activity_c\":\"Mountain Biking\", \"trip_date\":\"08/05/2015\", \"content\":\"<p>From the parking ride into Brignoud ...\", \"difficulty_c\":{\"rating\":\"2\"},\"technical_c\":{\"imperial\":\"false\", \"orientation\":\"Various\", \"max\":889, \"min\":225, \"distance\":24, \"climb\":750},\"files\": [{\"name\":\"a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"size\":\"46957\",\"url\":\"/library/images/a5e/e7a/a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"thumbnailUrl\":\"/library/gpxIcon.jpg\",\"deleteUrl\":\"/delete/images/a5e/e7a/a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"deleteType\":\"DELETE\"}], \"metadata\" : {\"canonical_url\":\"tour-of-the-cret-de-chazay\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2015-08-05 08:49:14\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"perms\":11275}}}";
    private static final String content2 = "{\"_index\":\"main-index\",\"_type\":\"_doc\",\"_id\":\"r95eec7d13e7b\",\"_score\":1,\"_source\":{\"title_c\":\"Tour of the Crêt de Chazay\", \"activity_c\":\"Mountain Biking\", \"trip_date\":\"08/05/2015\",  \"difficulty\":{\"rating_c\":\"2\"}, \"metadata\" : {\"canonical_url\":\"tour-of-the-cret-de-chazay\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2015-08-05 08:49:14\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"perms\":11275}}}";
    private final static ObjectMapper objectMapper = new ObjectMapper();


	@Before
	public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private PageResource createMocks() {
        ManagedElasticClient repository = Mockito.mock(ManagedElasticClient.class);
        MagneatoConfiguration configuration = Mockito.mock(MagneatoConfiguration.class);

		Mockito.when(repository.get("r95eec7d13e7a")).thenReturn(content1);
        Mockito.when(repository.get("r95eec7d13e7b")).thenReturn(content2);

        return new PageResource(configuration, repository);
	}


	@Test
	public void simpleCase() throws IOException {
		PageResource pageResource = createMocks();
        JsonNode rootNode = objectMapper.reader().readTree(content1).get("_source");
		String cloned = pageResource.cloneContent(rootNode.toString());
		System.out.println(cloned);
	}

    @Test
    public void clonedObjectElement() throws IOException {
        PageResource pageResource = createMocks();
        JsonNode rootNode = objectMapper.reader().readTree(content2).get("_source");
        String cloned = pageResource.cloneContent(rootNode.toString());
        System.out.println(cloned);
    }
}
