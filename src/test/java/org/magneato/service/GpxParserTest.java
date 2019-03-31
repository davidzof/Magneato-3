package org.magneato.service;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GpxParserTest {
	GpxParser gpxParser;

	@Before
	public void setUp() {
		gpxParser = new GpxParser();
	}

	@Test
	public void testRead() throws IOException {
		FileInputStream fin = new FileInputStream("src/test/test.gpx");
		gpxParser.read(fin);
		Assert.assertEquals("Bois Barbu Odyssey", gpxParser.getName());

		Assert.assertEquals(702, (int) gpxParser.getClimb());
		Assert.assertEquals(700, (int) gpxParser.getDescent());
		Assert.assertEquals(1544, (int) gpxParser.getMaxElevation());
		Assert.assertEquals(1132, (int) gpxParser.getMinElevation());
		Assert.assertEquals("45.0603640", gpxParser.getStartLat());
		Assert.assertEquals("5.5223030", gpxParser.getStartLon());
		Assert.assertEquals(1553165761000L, gpxParser.getStartTime().getTime()
				);
		Assert.assertEquals(25567, (int) gpxParser.getDistance());

	}

}
