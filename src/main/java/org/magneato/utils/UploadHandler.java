package org.magneato.utils;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author David GEORGE
 */
public class UploadHandler {
	/**
	 * Make a thumbnail image to display in editor and web pages
	 */
	public static String createThumbnail(String path, String fileName,
			String mimeType) throws IOException {
		String thumbName = null;
		// create a thumbnail
		switch (mimeType) {
		case "image/jpeg":
		case "image/gif":
		case "image/png":
			// always create as jpg
			thumbName = "thumb_" + FilenameUtils.getBaseName(fileName) + ".jpg";
			BufferedImage img = new BufferedImage(100, 100,
					BufferedImage.TYPE_INT_RGB);
			img.createGraphics().drawImage(
					ImageIO.read(new File(path + "/" + fileName))
							.getScaledInstance(-1, 100, Image.SCALE_SMOOTH), 0,
					0, null);

			ImageIO.write(img, "jpg", new File(path + "/" + thumbName));
			break;
		case "application/octet-stream":
			thumbName = "/library/gpxIcon.jpg";
			break;
		}// switch

		return thumbName;
	}

	public static String getMetaData(String filePath) throws IOException {
		File file = new File(filePath);
		try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				// attach source to reader
				reader.setInput(iis, true);
				// read metadata of first image
				IIOMetadata metadata = reader.getImageMetadata(0);

				String[] names = metadata.getMetadataFormatNames();
				int length = names.length;
				for (int i = 0; i < length; i++) {
					displayMetadata(metadata.getAsTree(names[i]));
				}

			}
		}

		return null;
	}

	static void displayMetadata(Node root) {
		displayMetadata(root, 0);
	}

	static void indent(int level) {
		for (int i = 0; i < level; i++)
			System.out.print("    ");
	}

	static void displayMetadata(Node node, int level) {
		// print open tag of element
		indent(level);
		System.out.print("<" + node.getNodeName());
		NamedNodeMap map = node.getAttributes();
		if (map != null) {

			// print attribute values
			int length = map.getLength();
			for (int i = 0; i < length; i++) {
				Node attr = map.item(i);
			}
		}

		Node child = node.getFirstChild();
		if (child == null) {
			// no children, so close element and return
			System.out.println("/>");
			return;
		}

		// children, so close current tag
		System.out.println(">");
		while (child != null) {
			// print children recursively
			displayMetadata(child, level + 1);
			child = child.getNextSibling();
		}

		// print close tag of element
		indent(level);
		System.out.println("</" + node.getNodeName() + ">");
	}
}
