package com.haxademic.core.net.old;

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
import com.haxademic.core.net.WebServer;

public class WebServerRequestHandler extends AbstractHandler {	
	@Override
	public void handle( String target,
			Request baseRequest,
			HttpServletRequest request,
			HttpServletResponse response ) throws IOException, ServletException
	{
		// Get request path and check to see if it looks like a file
		String requestPath = baseRequest.getPathInfo();
		
		// if path ends with a slash, append index.html
		if(requestPath.lastIndexOf("/") == requestPath.length() - 1) {
			requestPath += "index.html";
		}
		
		// get path without initial slash
		String requestPathNoSlash = requestPath.substring(1);
		if(WebServer.DEBUG == true) P.println("requestPath", requestPath);
		
		// Set response props
		response.addHeader("Access-Control-Allow-Origin", "*"); 	// Disable CORS
		response.setStatus(HttpServletResponse.SC_OK);			// set 200
		response.setContentType("text/html; charset=utf-8");		// default to text
		
		// look for static files on the www filesystem
		String filePathToCheck = WebServer.WWW_PATH + requestPathNoSlash;
		
		// check to redirect if no trailing slash, but has index.html
		if(FileUtil.fileExists(filePathToCheck + "/index.html") == true) {
			requestPath += "/";
			response.sendRedirect(requestPath);
			baseRequest.setHandled(true);
			return;
		}

		// CHECK FOR (& SERVE) STATIC FILES
		if(FileUtil.fileExists(filePathToCheck)) {
			String filePath = filePathToCheck;
			if(WebServer.DEBUG == true) P.println("Found static file:", filePath);
			if(filePath.indexOf(".html") != -1) {
				writeTextFileFromPath(response, filePath, "text/html");
			} else if(filePath.indexOf(".css") != -1) {
				writeTextFileFromPath(response, filePath, "text/css");
			} else if(filePath.indexOf(".js") != -1) {
				writeTextFileFromPath(response, filePath, "application/javascript");
			} else if(filePath.indexOf(".svg") != -1) {
				writeTextFileFromPath(response, filePath, "image/svg+xml");
			} else if(filePath.indexOf(".ttf") != -1 || filePath.indexOf(".otf") != -1) {
				writeBinaryFileFromPath(response, filePath, "application/font-sfnt");
			} else if(filePath.indexOf(".png") != -1) {
				writeBinaryFileFromPath(response, filePath, "image/png");
			} else if(filePath.indexOf(".jpg") != -1) {
				writeBinaryFileFromPath(response, filePath, "image/jpeg");
			} else if(filePath.indexOf(".gif") != -1) {
				writeBinaryFileFromPath(response, filePath, "image/gif");
			} else if(filePath.indexOf(".tga") != -1) {
				writeBinaryFileFromPath(response, filePath, "image/targa");
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
	
	protected void writeTextFileFromPath(HttpServletResponse response, String pathStr, String contentType) throws IOException {
		String fileContents = new String(Files.readAllBytes(Paths.get(pathStr)));
		response.setContentType(contentType);
		response.getWriter().println(fileContents);
	}
	
	protected void writeBinaryFileFromPath(HttpServletResponse response, String pathStr, String contentType) throws IOException {
		Path path = Paths.get(pathStr);
		byte[] imageData = Files.readAllBytes(path);
		response.setContentType(contentType);
		response.getOutputStream().write(imageData);
	}
	
	protected String handleCustomPaths(String path, String[] pathComponents) {
		// MUST OVRERRIDE
		return null;
	}
}