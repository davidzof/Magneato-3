package org.magneato.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import io.dropwizard.views.View;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.magneato.MagneatoConfiguration;
import org.magneato.managed.ManagedElasticClient;
import org.magneato.service.GpxParser;
import org.magneato.service.MetaData;
import org.magneato.utils.PageUtils;
import org.magneato.utils.UploadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// https://github.com/wdawson/dropwizard-auth-example/blob/master/pom.xml
@Path("/")
public class UploadResource {
    private ManagedElasticClient repository;
    private static final int SUBDIR_SIZE = 3; // TODO user define
    private static final String GPXTYPE = "application/octet-stream";
    private final ObjectMapper mapper = new ObjectMapper();
    // Map to store 62 possible characters
    private final static String mapInitiliazer = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final static char[] map = mapInitiliazer.toCharArray();

    PageUtils pageUtils;

    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    // add to config
    public final static String IMAGEPATH = "/library/images";
    private String imageDir = null;

    public UploadResource(MagneatoConfiguration configuration,
            ManagedElasticClient repository) {
        this.repository = repository;
        this.pageUtils = new PageUtils();

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
    public String upload(@FormDataParam("files") final FormDataBodyPart body,
            @FormDataParam("files") final InputStream fileInputStream) {
        UploadInfo uploadInfo = saveFile(body, fileInputStream);
        if (uploadInfo != null) {
            return "{" + uploadInfo.toJson() + "}";
        }
        return ""; // TODO some kind of error
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
        // always jpg
        String thumbPath = imageDir + FilenameUtils.getPath(fileName)
                + "thumb_" + FilenameUtils.getBaseName(fileName) + ".jpg";
        if (!(new File(thumbPath)).delete()) {
            log.error("could not delete " + thumbPath);
        }
        log.debug("deleted " + path + " + " + thumbPath);

        return "???";
    }

    /*
     * Stores upload under random directory + filename
     */
    private UploadInfo saveFile(final FormDataBodyPart body,
            final InputStream fileInputStream) {
        String thumbName = null;
        final String mimeType = body.getMediaType().toString();
        String fileName = body.getContentDisposition().getFileName();
        log.debug("saveFile: filename " + fileName + " imageDir " + imageDir);

        if (imageDir == null) {
            log.warn("image directory not configured in config.yml");
            return null; // TODO some kind of error
        }

        // Create short filename based on current time millis, handle possible dupes
        // clean this code up!!!
        java.nio.file.Path outputPath = null;
        int version = 0;
        String subDir; // 1st level subdir where upload is to be stored
        String name; // full path to upload
        String ext; // upload extension
        do {
            ext = FilenameUtils.getExtension(fileName);
            fileName = idToShortURL(System.currentTimeMillis());
            if (fileName.length() > SUBDIR_SIZE) {
                subDir = fileName.substring(0, 3) + "/";
                fileName = fileName.substring(3);
                if (version++ > 0) {
                    fileName = fileName + "-" + version;
                }
            } else {
                subDir = "";
            }

            name = imageDir + subDir + fileName + "." + ext;
            outputPath = FileSystems.getDefault().getPath(name);
        } while (outputPath != null && Files.exists(outputPath));

        long len = 0;
        try {
            // make the directory, if it doesn't exist
            Files.createDirectories(outputPath.getParent());
            len = Files.copy(fileInputStream, outputPath);
            
            // we copy from source to destination but source doesn't exist?
             thumbName = UploadHandler.createThumbnail(outputPath.getParent().toString(), outputPath.getFileName().toString(), mimeType);
        } catch (IOException e) {
            log.warn("problem uploading  file " + e.getMessage());
            // need to abort here
        }

        String url = IMAGEPATH + "/" + subDir + fileName + "." + ext;
        String thumbUrl;
        if (thumbName.startsWith("/")) {
            thumbUrl = thumbName;
        } else {
            thumbUrl = IMAGEPATH + "/" + subDir + thumbName;
        }
        UploadInfo uploadInfo = new UploadInfo(fileName, len, url, thumbUrl,
                mimeType, subDir, name);

        return uploadInfo;
    }

    /**
     * Upload a gpx file, opens an editor page with the information prefilled
     * from the gpx
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
    public View uploadGPX(@QueryParam("parent") String parent,
            @FormDataParam("file") final FormDataBodyPart body,
            @FormDataParam("file") final InputStream fileInputStream,
            @Context HttpServletRequest request,
            @Context SecurityContext security) {

    	// TODO move to config
        String template = "{\"title\":\"%1$s\",\"child\":false,\"activity_c\":\"\",\"trip_date\":\"%11$s\",\"difficulty_c\":{\"rating\":\"\"},\"ski_difficulty_c\":{\"rating\":\"\"},\"technical_c\":{\"imperial\":\"false\",\"orientation\":\"\",\"distance\":%2$.3f,\"climb\":%3$d,\"descent\":%4$d,\"min\":%5$d,\"max\":%6$d,\"location\":{\"lat\":%7$s,\"lon\":%8$s}},%9$s,\"metadata\":%10$s}";
        String content = null;

        final String mimeType = body.getMediaType().toString();
        log.info("uploadgpx " + mimeType);
        if (!GPXTYPE.equalsIgnoreCase(mimeType)) {
            return new FTLView("error", "File is not a GPX");
        }

        // Step.1 save gpx to storage
        // http://localhost:9090/library/images/Ref//library/gpxIcon.jpg
        UploadInfo uploadInfo = saveFile(body, fileInputStream);
        if (uploadInfo != null) {
            // Step.2 parse gpx data
            GpxParser gpxParser = new GpxParser();

            File initialFile = new File(uploadInfo.getName());
            InputStream gpxStream;
            try {
                gpxStream = new FileInputStream(initialFile);
                gpxParser.read(gpxStream);
            } catch (FileNotFoundException e) {
                return new FTLView("error", "Cannot find GPX file " + uploadInfo.getName());
            }

            // Step.3 create meta data ???
            // TODO edit/view template should be parameter
            MetaData metaData = new MetaData()
                    .setEditTemplate("tripreport")
                    .setViewTemplate("tripreport")
                    .setIPAddr(request.getRemoteAddr())
                    .setCreateDate(gpxParser.getStartTime().getTime())
                    .setOwner(security.getUserPrincipal().getName());
            metaData.addRelation(parent);

            // Step.4 update gpx data in json
            // note all values should return empty string if they cannot be determined, let the clonage take over if neccessary
            // named parameters
            content = String.format(template, gpxParser.getName(),
                    gpxParser.getDistanceKM(), (int) gpxParser.getClimb(),
                    (int) gpxParser.getDescent(),
                    (int) gpxParser.getMinElevation(),
                    (int) gpxParser.getMaxElevation(), gpxParser.getStartLat(),
                    gpxParser.getStartLon(), uploadInfo.toJson(),
                    metaData.toJson(), gpxParser.getDate());

            if (parent != null && !parent.isEmpty()) {
                String parentJSON = repository.get(parent);
                log.debug("parent " + parentJSON);
                try {
                    JsonNode root = mapper.readTree(parentJSON);
                    JsonNode contentTree = mapper.readTree(content);
                    HashMap<String, String> clonableItems = addKeys("", root, false);
                    for (Map.Entry<String, String> entry : clonableItems.entrySet()) {
                        setTokenValue(contentTree, entry.getKey(), entry.getValue());
                    }//

                    content = contentTree.toString();
                } catch (IOException e) {
                    log.error("Couldn't read parent JSON {1}", e.getMessage());
                    // just ignore, we've got gpx data
                    // error coudl be content !, check this
                }

            }

            // Step.4 merge with parent data

        } else {
            return new FTLView("error", "Unable to process GPX file");
        }
        // note if content == null there is an error
        return new EditView("", content, "tripreport");
    }

    @GET
    @Path("/gpxview")
    @RolesAllowed({ "ADMIN", "EDITOR" })
    public View uploadView(@Context HttpServletRequest request) {
        String id = pageUtils.getId(request.getHeader("referer"));
        FTLView view = new FTLView("uploadgpx", id);

        return view;
    }

    private void setTokenValue(JsonNode root, String path, String value) {
        String[] keys = path.split("/");
        for (int i = 1; i < keys.length; i++) {
            String key = keys[i];
            JsonNode nextToken = root.get(key);
            if (nextToken == null) {
                return;
            } else if (nextToken.isValueNode()) {
                if (i + 1 == keys.length) {
                    break;
                } else {
                    return;
                }
            }
            root = nextToken;
        }// for
        ((ObjectNode) root).put(keys[keys.length - 1], value);
    }

    private HashMap<String, String> addKeys(String path, JsonNode jsonNode,
            boolean cloneable) {
        HashMap<String, String> clonable = new HashMap<String, String>();

        if (jsonNode.isObject()) {
            // recurse
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                String key = entry.getKey();
                if (cloneable == true || key.endsWith("_c")) {
                    clonable.putAll(addKeys(path + "/" + entry.getKey(),
                            entry.getValue(), true));
                } else {
                    clonable.putAll(addKeys(path + "/" + entry.getKey(),
                            entry.getValue(), false));
                }
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            /*
             * Not Implemented System.out.println("[" + path ); for (int i = 0;
             * i < arrayNode.size(); i++) {
             * System.out.println(arrayNode.get(i));
             *
             * } System.out.println("]" );
             */
        } else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            if (cloneable) {
                clonable.put(path, valueNode.asText());
            }
        }

        return clonable;
    }

    // Function to generate a short url from integer ID
    String idToShortURL(long  id) {

        StringBuilder shortName = new StringBuilder();

        // Convert given integer id to a base 62 number
        while (id > 0) {
            // use above map to store actual character
            // in short url
            int i =  (int)(id % 62);

            char c = map[i];
            shortName.append(c);
            id = id/62;
        }

        return shortName.toString();
    }

}