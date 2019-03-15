package org.magneato.resources;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.magneato.managed.ManagedElasticClient;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class PageViewTest {
	org.magneato.resources.PageResource pageResource;
	private static final String content = "{\"title\":\"Champagne Chartreuse\",\"activity_c\":\"Ski Touring\",\"trip_date\":\"03/03/2019\",\"content\":\"<p>Powder in March is pretty short lives, here today, gone tomorrow. It snowed lightly on Friday and Saturday night so I decided to take advantage of the good weather on Sunday to do a rapid Couloir en Virgule. I didn't have high hopes for the couloir but the Vallon de Marcieu at the top holds cold snow well on its west flank and that is exactly what I found. The depths varied, probably around 10cm of snow at 1800 meters but there were accumulations of 20cm in places.</p><p>The top of the couloir was well filled, it was possible to ski right from the entrance as the fences are buried. I took the small steep side couloir at the bottom but not really sure this was better than the main couloir, the snow was soft at this point. Conditions improved in the woods with the fresh snow smoothing out any imperfections.<br></p>\",\"conditions\":\"<p>Marcieu: 10-15cm of fresh snow that had fallen without much wind. Accumulations of snow in places giving good skiing.</p><p>Couloir: Fresh snow</p><p>Forest: Fresh snow but a bit soft</p><p>Path: some bare patches appearing<br></p><p>Pistes: Still frozen in places at 9.30am<br></p>\",\"ski_difficulty_c\":{\"rating\":\"4.1\",\"bra\":\"2\",\"snowline\":1000},\"technical_c\":{\"max\":1860,\"min\":1060,\"distance\":6.65,\"climb\":800,\"location\":{\"lat\":\"45.356037\",\"lon\":\"5.91864\"},\"orientation\":\"East\"},\"files\":[{\"name\":\"Couloir_Virgule_freshies.gpx\",\"size\":\"177113\",\"url\":\"/library/images/Cou/Couloir_Virgule_freshies.gpx\",\"thumbnailUrl\":\"/library/images/Cou/null\",\"deleteUrl\":\"/delete/Cou/Couloir_Virgule_freshies.gpx\",\"deleteType\":\"DELETE\"},{\"name\":\"3marvirgule.jpg\",\"size\":\"381722\",\"url\":\"/library/images/3ma/3marvirgule.jpg\",\"thumbnailUrl\":\"/library/images/3ma/thumb_3marvirgule.jpg\",\"deleteUrl\":\"/delete/3ma/3marvirgule.jpg\",\"deleteType\":\"DELETE\"},{\"name\":\"3marvirgule3.jpg\",\"size\":\"447376\",\"url\":\"/library/images/3ma/3marvirgule3.jpg\",\"thumbnailUrl\":\"/library/images/3ma/thumb_3marvirgule3.jpg\",\"deleteUrl\":\"/delete/3ma/3marvirgule3.jpg\",\"deleteType\":\"DELETE\"},{\"name\":\"3marvirgule2.jpg\",\"size\":\"398123\",\"url\":\"/library/images/3ma/3marvirgule2.jpg\",\"thumbnailUrl\":\"/library/images/3ma/thumb_3marvirgule2.jpg\",\"deleteUrl\":\"/delete/3ma/3marvirgule2.jpg\",\"deleteType\":\"DELETE\"}],\"metadata\":{\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2019-03-03 09:12:27\",\"ip_addr\":\"127.0.0.1\",\"owner\":\"davidof\",\"canonical_url\":\"champagne-chartreuse\",\"perms\":11275,\"relations\":[\"rb9c8be8816f5\"],\"groups\":[\"editors\"]}}";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	private PageView createMocks() {
		ManagedElasticClient repository = Mockito
				.mock(ManagedElasticClient.class);

		// change this - support search
		Mockito.when(repository.get("r95eec7d13e7a")).thenReturn(content);

		return new PageView(content, "", repository,
				"http://localhost:9090/r95eec7d13e7a/skating-to-the-cret-luisard");
	}

	@Test
	public void testTitle() throws IOException {
		PageView view = createMocks();

		Assert.assertEquals("Champagne Chartreuse", view.getJson().get("title")
				.asText());

	}

	@Test
	public void testFirstImage() throws IOException {
		PageView view = createMocks();

		Assert.assertEquals("/library/images/3ma/3marvirgule.jpg",
				view.getFirst("image"));
	}

	@Test
	public void testFirstNoExist() throws IOException {
		PageView view = createMocks();

		Assert.assertEquals(null,
				null);
	}

	@Test
	public void testImageFiles() throws IOException {
		PageView view = createMocks();

		List<String> files = view.getFiles("image");
		Assert.assertEquals(files.size(), 3);
	}

	@Test
	public void testFirstGpx() throws IOException {
		PageView view = createMocks();

		Assert.assertEquals("/library/images/Cou/Couloir_Virgule_freshies.gpx",
				view.getFirst("application/gpx"));
	}
}
