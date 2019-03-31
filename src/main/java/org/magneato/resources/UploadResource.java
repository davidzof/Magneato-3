package org.magneato.resources;

import io.dropwizard.views.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

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

// https://github.com/wdawson/dropwizard-auth-example/blob/master/pom.xml
@Path("/")
public class UploadResource {
	private ManagedElasticClient repository;
	private static final int SUBDIR_SIZE = 3; // TODO user define
	private static final String GPXTYPE = "application/octet-stream";

	PageUtils pageUtils;

	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

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
		// alway jpg
		String thumbPath = imageDir + FilenameUtils.getPath(fileName)
				+ "thumb_" + FilenameUtils.getBaseName(fileName) + ".jpg";
		if (!(new File(thumbPath)).delete()) {
			log.error("could not delete " + thumbPath);
		}
		log.debug("deleted " + path + " + " + thumbPath);

		return "???";
	}

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

		// store images in a subdir based on up to the first x letters of the
		// filename, avoids putting too many files in one directory... maybe
		// there is a better idea?

		// TODO make configurable for big sites
		String subDir = FilenameUtils.getBaseName(fileName);
		if (subDir.length() > SUBDIR_SIZE) {
			subDir = fileName.substring(0, 3) + "/";
		}

		String name = imageDir + subDir + fileName;
		java.nio.file.Path outputPath = FileSystems.getDefault().getPath(name);

		// create a thumbnail
		long len = 0;
		try {
			// make the directory, if it doesn't exist
			Files.createDirectories(outputPath.getParent());

			len = Files.copy(fileInputStream, outputPath);
			System.out.println("file len " + len);
			thumbName = UploadHandler.createThumbnail(imageDir + subDir,
					fileName, mimeType);
			System.out.println("thumb " + thumbName);
			UploadHandler.getMetaData(outputPath.toString());
		} catch (IOException e) {
			log.warn("upload " + e.getMessage());
			// TODO: do something with errors
		}

		String url = IMAGEPATH + "/" + subDir + fileName;
		String thumbUrl = IMAGEPATH + "/" + subDir + thumbName;
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

		String template = "{\"title\":\"%s\",\"child\":false,\"activity_c\":\"\",\"trip_date\":\"08/04/2015\",\"difficulty_c\":{\"rating\":\"\"},\"ski_difficulty_c\":{\"rating\":\"\"},\"technical_c\":{\"imperial\":\"false\",\"distance\":%.3f,\"climb\":%d,\"descent\":%d,\"min\":%d,\"max\":%d,\"location\":{\"lat\":%s,\"lon\":%s}},%s,\"metadata\":%s}";
		String content = null;

		final String mimeType = body.getMediaType().toString();
		log.info("uploadgpx " + mimeType);
		if (!GPXTYPE.equalsIgnoreCase(mimeType)) {
			// some kind of error, redirect to error page, do we check filename
		}

		if (parent != null && !parent.isEmpty()) {
			log.debug("parent " + parent);

			String xx = repository.get(parent); // get parent, how to merge
												// this? only merge clonable
												// fields where target is empty
			// merge
		}
		// Step.1 save gpx to storage
		UploadInfo uploadInfo = saveFile(body, fileInputStream);
		if (uploadInfo != null) {
			// Step.2 parse gpx data
			GpxParser gpxParser = new GpxParser();

			System.out.println("created gpx file " + uploadInfo.getName());
			File initialFile = new File(uploadInfo.getName());
			InputStream gpxStream;
			try {
				gpxStream = new FileInputStream(initialFile);
				gpxParser.read(gpxStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				// TODO do something with the error
			}

			// Step.3 create meta data ???

			MetaData metaData = new MetaData()
					.setEditTemplate("tripreport")
					.setViewTemplate("tripreport")
					.setIPAddr(request.getRemoteAddr())
					.setCreateDate(gpxParser.getStartTime().getTime())
					.setOwner(security.getUserPrincipal().getName());

			// Step.3 update gpx data in json
			content = String.format(template, gpxParser.getName(),
					gpxParser.getDistanceKM(), (int) gpxParser.getClimb(),
					(int) gpxParser.getDescent(),
					(int) gpxParser.getMinElevation(),
					(int) gpxParser.getMaxElevation(), gpxParser.getStartLat(),
					gpxParser.getStartLon(), uploadInfo.toJson(),
					metaData.toJson());

			System.out.println("json " + content);
			// Step.4 merge with parent data

		}
		// note if content == null there is an error
		return new EditView("", content, "tripreport");
	}

	@GET
	@Path("/gpxview")
	@RolesAllowed({ "ADMIN", "EDITOR" })
	public View uploadView(@Context HttpServletRequest request) {
		String id = pageUtils.getId(request.getHeader("referer"));
		FTLView view = new FTLView("uploadgpx");
		view.setValue(id);
		;
		return view;
	}
}