package org.magneato.resources;

import io.dropwizard.views.View;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.magneato.MagneatoConfiguration;
import org.magneato.managed.ManagedElasticClient;
import org.magneato.utils.UploadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
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

    public final static String IMAGEPATH = "/library/images";
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
            UploadHandler.getMetaData(outputPath.toString());
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
                + len + "\",\"type\":\"" + mimeType + "\",\"deleteUrl\":\"/delete/"
                + subDir + fileName + "\",\"deleteType\":\"DELETE\"}]}";
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{filename : .+}")
    public String delete(@PathParam("filename") String fileName,
            @Context SecurityContext security) throws IOException {
        log.debug("delete " + fileName + " imageDir " + imageDir);

        if (imageDir == null) {
            log.warn("image directory not configured in config.yml");
            return null;
        }

        String path = imageDir + fileName;
        if (!(new File(path)).delete()) {
            log.error("could not delete " + path);
        }
        // alway jpg
        String thumbPath = imageDir + FilenameUtils.getPath(fileName) + "thumb_" + FilenameUtils.getBaseName(fileName) + ".jpg";
        if (!(new File(thumbPath)).delete()) {
            log.error("could not delete " + thumbPath);
        }
        log.debug("deleted " + path + " + " + thumbPath);

        return "???";
    }

    /**
     * Upload a gpx file, opens an editor page with the information prefilled from the gpx
     *
     * @param body
     * @param fileInputStream
     * @return
     */
    @POST
    @Path("/uploadgpx")
    @RolesAllowed({ "ADMIN", "EDITOR" })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public View uploadGPX(
            @FormDataParam("file") final FormDataBodyPart body,
            @FormDataParam("file") final InputStream fileInputStream
    ) {
        log.info("uploadGpx");
        String content = "{\"title\":\"XXX\",\"child\":false,\"activity_c\":\"Mountain Biking\",\"trip_date\":\"08/04/2015\",\"content\":\"\",\"conditions\":\"\",\"difficulty_c\":{\"rating\":\"null\"},\"technical_c\":{\"imperial\":\"false\",\"distance\":33,\"climb\":800},\"files\":[{\"name\":\"46f42e49-6c34-4052-86ce-a225efc99b87_08042015-tour-du-cret-de-chazay.gpx\",\"size\":\"498815\",\"url\":\"/library/images/46f/b87/46f42e49-6c34-4052-86ce-a225efc99b87_08042015-tour-du-cret-de-chazay.gpx\",\"thumbnailUrl\":\"/library/gpxIcon.jpg\",\"deleteUrl\":\"/delete/images/46f/b87/46f42e49-6c34-4052-86ce-a225efc99b87_08042015-tour-du-cret-de-chazay.gpx\",\"deleteType\":\"DELETE\"}],\"metadata\":{\"canonical_url\":\"tour-of-the-cret-de-chazay-route\",\"edit_template\":\"tripreport\",\"display_template\":\"tripreport\",\"create_date\":\"2015-08-05 10:24:11\",\"ip_addr\":\"178.79.148.217\",\"owner\":\"davidof\",\"groups\":[\"editors\"],\"relations\":[\"r95eec7d13e7a\"],\"perms\":11275}}";
// handle bad data...
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
        /*
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
            UploadHandler.getMetaData(outputPath.toString());
            log.debug(">>> THUMBNAME " + thumbName);
        } catch (IOException e) {
            log.warn("upload " + e.getMessage());
            // do something with errors
        }

        String url = IMAGEPATH + "/" + subDir + fileName;
        String thumbUrl = IMAGEPATH + "/" + subDir + thumbName;
        log.debug(">>> THUMBNAME URL " + thumbUrl);
        */

        return new EditView("", content, "tripreport");
    }

    @GET
    @Path("/gpxview")
    @RolesAllowed({ "ADMIN", "EDITOR" })
    public View uploadView() {
        return new FTLView("uploadgpx");
    }
}