package org.magneato.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Test;

import java.io.IOException;

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


    @Test
    public void imageTagParsingExact() {
        String content = "<p>Inspite of the recent snowfall{image:0}  snow-depths remain&nbsp; below average..<br>{image:1}<br>It has not snowed since before Christmas <br></p>";
        String json = "{\"title\":\"Couloir Virgule before Carmen\",\"child\":false,\"activity_c\":\"Ski Touring\",\"trip_date\":\"01/01/2018\",\"content\":\"<p><p>The weather service announced a window from 10 to 2 pm, just before the arrival of storm Carmen. It had snowed overnight but only about 15cm. With the heavy rain at the weekend the snow depths have reduced from 70 to 30cm at 1000m but still enough to tour as it is relatively dense but somewhat discontinuous in the Chartreuse forests. The ski lift at the Col de Marcieu was not running due to a technical fault so the ski runs had not been pisted, some of the best skiing of the tour. The trail was made to the summit. I was a bit slow today, 1h10 for the 800 meter climb. In the cirque of the alpe du Seuil a slab had descended the day before yesterday and there was a purge in the middle of the bowl covering the tracks - so probably around midday.</p><p>The couloir already had 1 track, the snow was a bit dense to ski easily but not enough to cover the icy base and the &quot;staircases&quot; left by previous skiers. The side slopes and forest offered some good skiing - although neither I nor the skier in front had attempted to go too far onto these avalanche prone areas.</p></p><p><strong>Weather: </strong>Sun, turning stormy, rain at 15h</p><p><strong>Access: </strong>Snow at the pass</p><p><strong>Country: </strong>France<strong> Area: </strong>Chartreuse<strong> Trailhead: </strong>Col de Marcieu</p>\",\"ski_difficulty_c\":{\"bra\":\"2\",\"snowline\":1040},\"technical_c\":{\"imperial\":\"false\",\"distance\":6.3,\"climb\":800},\"files\":[{\"name\":\"54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"size\":\"397567\",\"url\":\"/library/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"thumbnailUrl\":\"/library/images/542/4e8/thumb_54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"deleteUrl\":\"/delete/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"deleteType\":\"DELETE\"},{\"name\":\"54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"size\":\"440751\",\"url\":\"/library/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"thumbnailUrl\":\"/library/images/542/4e8/thumb_54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"deleteUrl\":\"/delete/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"deleteType\":\"DELETE\"}],\"metadata\":{\"canonical_url\":\"aulp-du-seuil-couloir-en-virgule.18471522013-route\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2018-01-01 23:42:58\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"relations\":[\"rb9c8be8816f5\"],\"perms\":11275}}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.reader().readTree(json);
            String result =StringHelper.parseTags(content, jsonNode);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void imageTagParsingOutOfRange() {
        String content = "<p>Inspite of the recent snowfall{image:0}  snow-depths remain&nbsp; below average..<br>{image:10}<br>It has not snowed since before Christmas <br></p>";
        String json = "{\"title\":\"Couloir Virgule before Carmen\",\"child\":false,\"activity_c\":\"Ski Touring\",\"trip_date\":\"01/01/2018\",\"content\":\"<p><p>The weather service announced a window from 10 to 2 pm, just before the arrival of storm Carmen. It had snowed overnight but only about 15cm. With the heavy rain at the weekend the snow depths have reduced from 70 to 30cm at 1000m but still enough to tour as it is relatively dense but somewhat discontinuous in the Chartreuse forests. The ski lift at the Col de Marcieu was not running due to a technical fault so the ski runs had not been pisted, some of the best skiing of the tour. The trail was made to the summit. I was a bit slow today, 1h10 for the 800 meter climb. In the cirque of the alpe du Seuil a slab had descended the day before yesterday and there was a purge in the middle of the bowl covering the tracks - so probably around midday.</p><p>The couloir already had 1 track, the snow was a bit dense to ski easily but not enough to cover the icy base and the &quot;staircases&quot; left by previous skiers. The side slopes and forest offered some good skiing - although neither I nor the skier in front had attempted to go too far onto these avalanche prone areas.</p></p><p><strong>Weather: </strong>Sun, turning stormy, rain at 15h</p><p><strong>Access: </strong>Snow at the pass</p><p><strong>Country: </strong>France<strong> Area: </strong>Chartreuse<strong> Trailhead: </strong>Col de Marcieu</p>\",\"ski_difficulty_c\":{\"bra\":\"2\",\"snowline\":1040},\"technical_c\":{\"imperial\":\"false\",\"distance\":6.3,\"climb\":800},\"files\":[{\"name\":\"54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"size\":\"397567\",\"url\":\"/library/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"thumbnailUrl\":\"/library/images/542/4e8/thumb_54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"deleteUrl\":\"/delete/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"deleteType\":\"DELETE\"},{\"name\":\"54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"size\":\"440751\",\"url\":\"/library/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"thumbnailUrl\":\"/library/images/542/4e8/thumb_54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"deleteUrl\":\"/delete/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"deleteType\":\"DELETE\"}],\"metadata\":{\"canonical_url\":\"aulp-du-seuil-couloir-en-virgule.18471522013-route\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2018-01-01 23:42:58\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"relations\":[\"rb9c8be8816f5\"],\"perms\":11275}}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.reader().readTree(json);
            String result =StringHelper.parseTags(content, jsonNode);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void imageTagParsingDuplicate() {
        String content = "<p>Inspite of the recent snowfall{image:1}  snow-depths remain&nbsp; below average..<br>{image:1}<br>It has not snowed since before Christmas <br></p>";
        String json = "{\"title\":\"Couloir Virgule before Carmen\",\"child\":false,\"activity_c\":\"Ski Touring\",\"trip_date\":\"01/01/2018\",\"content\":\"<p><p>The weather service announced a window from 10 to 2 pm, just before the arrival of storm Carmen. It had snowed overnight but only about 15cm. With the heavy rain at the weekend the snow depths have reduced from 70 to 30cm at 1000m but still enough to tour as it is relatively dense but somewhat discontinuous in the Chartreuse forests. The ski lift at the Col de Marcieu was not running due to a technical fault so the ski runs had not been pisted, some of the best skiing of the tour. The trail was made to the summit. I was a bit slow today, 1h10 for the 800 meter climb. In the cirque of the alpe du Seuil a slab had descended the day before yesterday and there was a purge in the middle of the bowl covering the tracks - so probably around midday.</p><p>The couloir already had 1 track, the snow was a bit dense to ski easily but not enough to cover the icy base and the &quot;staircases&quot; left by previous skiers. The side slopes and forest offered some good skiing - although neither I nor the skier in front had attempted to go too far onto these avalanche prone areas.</p></p><p><strong>Weather: </strong>Sun, turning stormy, rain at 15h</p><p><strong>Access: </strong>Snow at the pass</p><p><strong>Country: </strong>France<strong> Area: </strong>Chartreuse<strong> Trailhead: </strong>Col de Marcieu</p>\",\"ski_difficulty_c\":{\"bra\":\"2\",\"snowline\":1040},\"technical_c\":{\"imperial\":\"false\",\"distance\":6.3,\"climb\":800},\"files\":[{\"name\":\"54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"size\":\"397567\",\"url\":\"/library/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"thumbnailUrl\":\"/library/images/542/4e8/thumb_54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"deleteUrl\":\"/delete/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"deleteType\":\"DELETE\"},{\"name\":\"54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"size\":\"440751\",\"url\":\"/library/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"thumbnailUrl\":\"/library/images/542/4e8/thumb_54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"deleteUrl\":\"/delete/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"deleteType\":\"DELETE\"}],\"metadata\":{\"canonical_url\":\"aulp-du-seuil-couloir-en-virgule.18471522013-route\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2018-01-01 23:42:58\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"relations\":[\"rb9c8be8816f5\"],\"perms\":11275}}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.reader().readTree(json);
            String result =StringHelper.parseTags(content, jsonNode);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void imageTagFormats() {
        String content = "<p>Inspite of the recent snowfall{image:}  snow-depths remain&nbsp; below average..<br>{image} {image:<br>It has not snowed since before Christmas <br></p>";
        String json = "{\"title\":\"Couloir Virgule before Carmen\",\"child\":false,\"activity_c\":\"Ski Touring\",\"trip_date\":\"01/01/2018\",\"content\":\"<p><p>The weather service announced a window from 10 to 2 pm, just before the arrival of storm Carmen. It had snowed overnight but only about 15cm. With the heavy rain at the weekend the snow depths have reduced from 70 to 30cm at 1000m but still enough to tour as it is relatively dense but somewhat discontinuous in the Chartreuse forests. The ski lift at the Col de Marcieu was not running due to a technical fault so the ski runs had not been pisted, some of the best skiing of the tour. The trail was made to the summit. I was a bit slow today, 1h10 for the 800 meter climb. In the cirque of the alpe du Seuil a slab had descended the day before yesterday and there was a purge in the middle of the bowl covering the tracks - so probably around midday.</p><p>The couloir already had 1 track, the snow was a bit dense to ski easily but not enough to cover the icy base and the &quot;staircases&quot; left by previous skiers. The side slopes and forest offered some good skiing - although neither I nor the skier in front had attempted to go too far onto these avalanche prone areas.</p></p><p><strong>Weather: </strong>Sun, turning stormy, rain at 15h</p><p><strong>Access: </strong>Snow at the pass</p><p><strong>Country: </strong>France<strong> Area: </strong>Chartreuse<strong> Trailhead: </strong>Col de Marcieu</p>\",\"ski_difficulty_c\":{\"bra\":\"2\",\"snowline\":1040},\"technical_c\":{\"imperial\":\"false\",\"distance\":6.3,\"climb\":800},\"files\":[{\"name\":\"54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"size\":\"397567\",\"url\":\"/library/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"thumbnailUrl\":\"/library/images/542/4e8/thumb_54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"deleteUrl\":\"/delete/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_npvv4x3sgwjuhledg7wrvhkga4abrl0huttwmkymlc-2048x1152.jpg\",\"deleteType\":\"DELETE\"},{\"name\":\"54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"size\":\"440751\",\"url\":\"/library/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"thumbnailUrl\":\"/library/images/542/4e8/thumb_54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"deleteUrl\":\"/delete/images/542/4e8/54237bf6-14da-40e9-b0f0-b9e094ee94e8_wjdjjatnk-douaoxvgbj8bewg93sggvtripvtoz6e-2048x1152.jpg\",\"deleteType\":\"DELETE\"}],\"metadata\":{\"canonical_url\":\"aulp-du-seuil-couloir-en-virgule.18471522013-route\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2018-01-01 23:42:58\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"relations\":[\"rb9c8be8816f5\"],\"perms\":11275}}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.reader().readTree(json);
            String result =StringHelper.parseTags(content, jsonNode);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
