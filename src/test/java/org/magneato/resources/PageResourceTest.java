package org.magneato.resources;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageResourceTest {

	@Test
	public void standardSource() {
		String content = "{\"_index\":\"main-index\",\"_type\":\"_doc\",\"_id\":\"r95eec7d13e7a\",\"_score\":1,\"_source\":{\"title_c\":\"Tour of the Crêt de Chazay\", \"activity_c\":\"Mountain Biking\", \"trip_date\":\"08/05/2015\", \"content\":\"<p>From the parking ride into Brignoud ...\", \"difficulty_c\":{\"rating\":\"2\"},\"technical_c\":{\"imperial\":\"false\", \"orientation\":\"Various\", \"max\":889, \"min\":225, \"distance\":24, \"climb\":750},\"files\": [{\"name\":\"a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"size\":\"46957\",\"url\":\"/library/images/a5e/e7a/a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"thumbnailUrl\":\"/library/gpxIcon.jpg\",\"deleteUrl\":\"/delete/images/a5e/e7a/a5e84b9c-0bfc-4506-ac05-95eec7d13e7a_tour-du-cret-de-chazey.gpx\",\"deleteType\":\"DELETE\"}], \"metadata\" : {\"canonical_url\":\"tour-of-the-cret-de-chazay\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2015-08-05 08:49:14\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"perms\":11275}}}";

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(content).get("_source");
			Iterator<Entry<String, JsonNode>> nodes = rootNode.fields();

			while (nodes.hasNext()) {
				Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes
						.next();

				if (entry.getKey().endsWith("_c")) {
					// clone
					System.out.println("is object " + entry.getValue().isObject()); // if true, iterate if not clone
					System.out.println("\"" + entry.getKey() + "\":"
							+ entry.getValue());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
