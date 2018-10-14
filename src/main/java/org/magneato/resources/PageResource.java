package org.magneato.resources;

import io.dropwizard.views.View;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.magneato.service.Template;

// https://github.com/wdawson/dropwizard-auth-example/blob/master/pom.xml
/*
 * Basic CRUD stuff READ (, WRITE, DELETE, UPDATE (PUT)
 */

@Path("/")
public class PageResource {
	List<Template> templates;

	public PageResource(List<Template> replicates) {
		this.templates = replicates;
	}

	HashMap<String, String> pageStore = new HashMap<String, String>();

	/**
	 * Display a normal web page
	 * 
	 * @param uri
	 * @param security
	 * @return
	 */
	@GET
	@Path("{uri}.htm")
	@Produces(MediaType.TEXT_HTML)
	public View getAsset(@PathParam("uri") String uri,
			@Context SecurityContext security) {
		System.out.println(uri);

	    for (Template r:  templates) {
	        System.out.println("desitrnateion " + r.getDescription());
	    }

		Principal principal = security.getUserPrincipal();
		if (principal != null) {
			System.out.println(security.getUserPrincipal().getName());
		}

		String body = pageStore.get(uri);
		if (body == null) {
			// no contents, create page instead
			return new ErrorView("404-error", uri);
		}

		return new PageView(body);
	}
	
	/**
	 * Create a new page, displays a form where you can enter title, edit and display template to use, page name is optional but must not exist
	 * 
	 * - you need to be an editor to do this
	 * - you tell say which template to use
	 * - can supply page name to create
	 * /create/{name}.htm/<template>
	 * /create/<template>
	 * @param uri
	 * @param security
	 * @return
	 */
	@GET
	@Path("create")
	@Produces(MediaType.TEXT_HTML)
	public View create(@PathParam("uri") String uri,
			@Context SecurityContext security) {
		
		return new PageView(body);
	}

	
	@RolesAllowed("ADMIN")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("edit/{uri}.htm")
	public View edit(@PathParam("uri") String uri) {
		System.out.println("Hey there, It looks like you are an admin.");
		String body = pageStore.get(uri);
		if (body == null) {
		return new FormView(uri);
		}
		System.out.println("data " + body);
		return new FormView(uri, body);
	}

	@POST
	@Path("{uri}.htm")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String saveAsset(@PathParam("uri") String uri, String body) {
		System.out.println("Saving " + uri + " " + body);
		// if edit true, use alpaca edit template, else velocity
		pageStore.put(uri, body);
		return uri;
	}

	// https://gitlab.com/zloster/dropwizard-static
    //https://github.com/dropwizard-bundles/dropwizard-configurable-assets-bundle
	@POST
	@Path("upload/{uri}.htm")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String upload(@PathParam("uri") String uri, @FormDataParam("files") final InputStream fileInputStream,
	        @FormDataParam("files") final FormDataContentDisposition contentDispositionHeader) {
		String fileName = contentDispositionHeader.getFileName();
		System.out.println("filename " + fileName);

		String name = "/home/david/src/dropwizard/DWGettingStarted/assets/" + fileName;
		// TODO get from config
		java.nio.file.Path outputPath = FileSystems.getDefault().getPath("/home/david/src/dropwizard/DWGettingStarted/assets", fileName);
		System.out.println("output path " + outputPath.getFileName());

        try {
            Files.copy(fileInputStream, outputPath);
            // create thumbnail
            BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            img.createGraphics().drawImage(ImageIO.read(new File(name)).getScaledInstance(100, 100, Image.SCALE_SMOOTH),0,0,null);
            
            String thumbName = "/home/david/src/dropwizard/DWGettingStarted/assets/thumb_" + fileName;
            ImageIO.write(img, "jpg", new File(thumbName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String url = "http://localhost:8080/library/images/" + fileName;
        String thumbUrl = "http://localhost:8080/library/images/thumb_" + fileName;

        	    	
        	    	
        return "{\"files\":[{\"url\":\"" + url + "\",\"thumbnailUrl\":\"" + thumbUrl + "\",\"name\":\"" + fileName + "\",\"size\":\"12345\",\"type\":\"image/png\",\"deleteUrl\":\"delete/" + fileName + "\",\"deleteType\":\"DELETE\"}]}";
	}
}