package org.magneato.utils;

import org.junit.Test;

public class StringHelperTest {
	@Test
	public void getSnippet() {
		String content = "<p>Inspite of the recent snowfall  snow-depths remain&nbsp; below average..<br><br>It has not snowed since before Christmas <br></p>";
		String result = StringHelper.getSnippet(content, 20);
		System.out.println(result);
	}

    @Test
    public void longSnippet() {
        String content = "<p>Inspite of the recent snowfall  snow-depths remain&nbsp; below average..<br><br>It has not snowed since before Christmas <br></p>";
        String result = StringHelper.getSnippet(content, 20);
        System.out.println(result);
    }

}
