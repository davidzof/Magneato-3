package utils;

import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UploadHandler {
    public static String createThumbnail(String path, String fileName, String mimeType) throws IOException {
        String thumbName = null;
        // create a thumbnail
        switch (mimeType) {
        case "image/jpeg":
        case "image/gif":
        case "image/png":
            // need to scale this
            BufferedImage img = new BufferedImage(100, 100,
                    BufferedImage.TYPE_INT_RGB);
            img.createGraphics().drawImage(
                    ImageIO.read(new File(path + fileName)).getScaledInstance(-1, 100,
                            Image.SCALE_SMOOTH), 0, 0, null);

            // always create as jpg
            thumbName = "thumb_" + FilenameUtils.getBaseName(fileName) + ".jpg";
            ImageIO.write(img, "jpg", new File(path + thumbName));
            break;
        }// switch
        return thumbName;
    }

    static String getMetaData() {
        // ImageInputStream iis = ImageIO.createImageInputStream(file);
        // Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

        return null;
    }
}
