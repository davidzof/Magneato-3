package org.magneato.resources;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.magneato.MagneatoConfiguration;
import org.magneato.managed.ManagedElasticClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.UploadHandler;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Map;

// https://github.com/wdawson/dropwizard-auth-example/blob/master/pom.xml
@Path("/")
public class UploadResource {
    private ManagedElasticClient repository;
    private static final int SUBDIR_SIZE = 3; // TODO user define

    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final static String IMAGEPATH = "/library/images";
    private String imageDir = null;

    public UploadResource(MagneatoConfiguration configuration,
            ManagedElasticClient repository) {
        this.repository = repository;

        Map<String, String> overrides = configuration.getAssetsConfiguration()
                .getOverrides();
        for (Map.Entry<String, String> entry : overrides.entrySet()) {
            if (IMAGEPATH.equals(entry.getKey())) {
                imageDir = entry.getValue() + "/";
            }
        }// for
    }

    // https://gitlab.com/zloster/dropwizard-static
    // https://github.com/dropwizard-bundles/dropwizard-configurable-assets-bundle
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String upload(
            @FormDataParam("files") final FormDataBodyPart body,
            @FormDataParam("files") final InputStream fileInputStream
    ) {
        String thumbName = null;
        final String mimeType = body.getMediaType().toString();
        String fileName = body.getContentDisposition().getFileName();
        log.debug("filename " + fileName + " imageDir " + imageDir);

        if (imageDir == null) {
            log.warn("image directory not configured in config.yml");
            return null;
        }

        // store images in a subdir based on up to the first x letters of the
        // filename, avoids putting too many files in one directory... maybe there is a better idea?
        String extension = FilenameUtils.getExtension(fileName);

        // TODO make configurable for big sites
        String subDir = FilenameUtils.getBaseName(fileName);
        if (subDir.length() > SUBDIR_SIZE) {
            subDir = fileName.substring(0, 3) + "/";
        }

        String name = imageDir + subDir + fileName;
        java.nio.file.Path outputPath = FileSystems.getDefault().getPath(
                name);

        // create a thumbnail
        long len = 0;
        try {
            // make the directory, if it doesn't exist
            Files.createDirectories(outputPath.getParent());

            len = Files.copy(fileInputStream, outputPath);
            thumbName = UploadHandler.createThumbnail(imageDir + subDir, fileName, mimeType);
            UploadHandler.getMetaData(imageDir + subDir + fileName);
            log.debug(">>> THUMBNAME " + thumbName);
        } catch (IOException e) {
            log.warn("upload " + e.getMessage());
            // do something with errors
        }

        String url = IMAGEPATH + "/" + subDir + fileName;
        String thumbUrl = IMAGEPATH + "/" + subDir + thumbName;
        log.debug(">>> THUMBNAME URL " + thumbUrl);

        return "{\"files\":[{\"url\":\"" + url + "\",\"thumbnailUrl\":\""
                + thumbUrl + "\",\"name\":\"" + fileName + "\",\"size\":\""
                + len + "\",\"type\":\"" + mimeType + "\",\"deleteUrl\":\"delete/"
                + fileName + "\",\"deleteType\":\"DELETE\"}]}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    public String delete(
            @Context SecurityContext security) throws IOException {
        log.debug("delete " );

        return "???";
    }
}