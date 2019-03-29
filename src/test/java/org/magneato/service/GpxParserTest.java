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
        
        
        Assert.assertEquals(5207, gpxParser.getTotalPoints());
        Assert.assertEquals(793, (int) gpxParser.getTotalAscent());
        Assert.assertEquals(793, (int) gpxParser.getTotalDescent());
        Assert.assertEquals(25602, (int) gpxParser.getTotalDistance());

    }

}
