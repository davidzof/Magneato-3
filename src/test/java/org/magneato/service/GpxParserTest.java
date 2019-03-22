package org.magneato.service;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

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
        Assert.assertEquals(5207, gpxParser.getTotalPoints());
        Assert.assertEquals(508, (int) gpxParser.getTotalAscent());
        Assert.assertEquals(25495, (int) gpxParser.getTotalDistance());

    }

}
