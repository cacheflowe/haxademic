package com.haxademic.core.net;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

public class WebServerRequestHandler extends AbstractHandler
{
	@Override
	public void handle( String target,
			Request baseRequest,
			HttpServletRequest request,
			HttpServletResponse response ) throws IOException,
	ServletException
	{
		// Print request path
		// P.println("getHttpURI()", baseRequest.getHttpURI());
		String requestPath = baseRequest.getPathInfo();

		// if path ends with a slash, append index.html
		if(requestPath.lastIndexOf("/") == requestPath.length() - 1) {
			requestPath += "index.html";
		}
		
		// get path without initial slash
		String requestPathNoSlash = requestPath.substring(1);
		if(WebServer.DEBUG == true) P.println("requestPath", requestPath);

		
		// Set response props
		response.addHeader("Access-Control-Allow-Origin", "*"); // Disable CORS
		response.setStatus(HttpServletResponse.SC_OK);			// set 200
		response.setContentType("text/html; charset=utf-8");	// default to text
		
		// look for static files on the www filesystem
		String fileCheck = FileUtil.getHaxademicWebPath() + requestPathNoSlash;
		
		// CHECK FOR (& SERVE) STATIC FILES
		if(FileUtil.fileExists(fileCheck)) {
			if(WebServer.DEBUG == true) P.println("Found static file:", fileCheck);
			// RETURN STATIC HTML/TEXT FILES
			if(fileCheck.indexOf(".html") != -1 || fileCheck.indexOf(".css") != -1 || fileCheck.indexOf(".js") != -1 || fileCheck.indexOf(".svg") != -1) {
				String fileContents = new String(Files.readAllBytes(Paths.get(fileCheck)));
				if(fileCheck.indexOf(".css") != -1) response.setContentType("text/css");
				if(fileCheck.indexOf(".js") != -1) response.setContentType("application/javascript");
				if(fileCheck.indexOf(".html") != -1) response.setContentType("text/html");
				if(fileCheck.indexOf(".svg") != -1) response.setContentType("image/svg+xml");
				response.getWriter().println(fileContents);
			// RETURN TTF/OTF FILES
			} else if(fileCheck.indexOf(".ttf") != -1 || fileCheck.indexOf(".otf") != -1) {
				Path path = Paths.get(fileCheck);
				byte[] binaryData = Files.readAllBytes(path);
				response.setContentType("application/font-sfnt");
				response.getOutputStream().write(binaryData);
			// RETURN STATIC PNG FILES
			} else if(fileCheck.indexOf(".png") != -1) {
				Path path = Paths.get(fileCheck);
				byte[] imageData = Files.readAllBytes(path);
				response.setContentType("image/png");
				response.getOutputStream().write(imageData);
			// RETURN STATIC JPG FILES
			} else if(fileCheck.indexOf(".jpg") != -1) {
				Path path = Paths.get(fileCheck);
				byte[] imageData = Files.readAllBytes(path);
				response.setContentType("image/jpeg");
				response.getOutputStream().write(imageData);
			// RETURN STATIC TGA FILES
			} else if(fileCheck.indexOf(".tga") != -1) {
				Path path = Paths.get(fileCheck);
				byte[] imageData = Files.readAllBytes(path);
				response.setContentType("image/targa");
				response.getOutputStream().write(imageData);
			}
		} else {
			String[] pathComponents = requestPathNoSlash.split("/");
			String result = handleCustomPaths(requestPath, pathComponents);
			
			if(result != null) {
				response.getWriter().println(result);
			} else {
				response.getWriter().println("{\"log\": \"No Response\"}");
			}
		}
		// Inform jetty that this request has now been handled
		baseRequest.setHandled(true);
	}
	
	protected String handleCustomPaths(String path, String[] pathComponents) {
		// MUST OVRERRIDE
		return null;
	}
}