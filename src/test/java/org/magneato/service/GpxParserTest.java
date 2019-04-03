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


	@Test
	public void testRead2() throws IOException {
		FileInputStream fin = new FileInputStream("src/test/barioz.gpx");
		gpxParser.read(fin);
		Assert.assertEquals("Barioz", gpxParser.getName());

		Assert.assertEquals(758, (int) gpxParser.getClimb());
		Assert.assertEquals(755, (int) gpxParser.getDescent());
		Assert.assertEquals(1830, (int) gpxParser.getMaxElevation());
		Assert.assertEquals(1426, (int) gpxParser.getMinElevation());
		Assert.assertEquals("45.3254650", gpxParser.getStartLat());
		Assert.assertEquals("6.0471780", gpxParser.getStartLon());
		Assert.assertEquals(1553766060000L, gpxParser.getStartTime().getTime()
		);
		Assert.assertEquals(23094, (int) gpxParser.getDistance());

	}


}
