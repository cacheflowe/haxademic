package com.haxademic.core.net;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.Base64Image;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.ScreenUtil;
import com.haxademic.core.system.DateUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class DashboardPoster 
implements IJsonRequestCallback {

	protected String projectName = "haxademic";
	protected String serverPostPath = "http://localhost/haxademic/www/dashboard/";
	protected JsonRequest postJSON;
	
	protected PGraphics imagePG;
	protected PGraphics screenshotPG;
	protected PImage image;
	protected BufferedImage screenshot;
	protected float imageScale = 1;
	protected float screenshotScale = 1;
	
	protected LinkedHashMap<String, String> appCustomInfo = new LinkedHashMap<String, String>();
	
	protected int postInterval = 60 * 60 * 1000;
	protected int lastPostTime = 0;
	public static boolean firstPost = true; // static in case of multiple instances
	
	protected boolean debug = false;

	// TODO:
	// - Before restarting, attempt to hit a URL that sends an email? Make this configurable
	// - Send an email on poor framerate performance [optional]
	// - Send an email daily with stats?? This should be a different object that includes user sessions, average framerate, system reports, etc. Maybe this negates the need to the drop in performance email 

	public DashboardPoster(String projectName, String serverPostPath) {
		this(projectName, serverPostPath, 60 * 60, 0.5f, 0.5f);
	}
	
	public DashboardPoster(String projectName, String serverPostPath, int intervalSeconds, float imageScale, float screenshotScale) {
		this.projectName = projectName;
		this.serverPostPath = serverPostPath;
		this.postInterval = intervalSeconds * 1000;
		lastPostTime = -postInterval + 20 * 1000;		// first post should be 20 seconds after start
		this.imageScale = imageScale;
		this.screenshotScale = screenshotScale;
		P.p.registerMethod("post", this);
		
		postJSON = new JsonRequest(serverPostPath);
	}
	
	// app frame loop
	public void post() {
		if(P.p.millis() > lastPostTime + postInterval) {
			// PostJSON.DEBUG = true;
			lastPostTime = P.p.millis();
			if(image != null && imagePG != null) ImageUtil.copyImage(image, imagePG);
			takeThreadedScreenshot();
		}
		checkQueuedScreenshot();
	}
	
	public void setImage(PImage img) {
		image = img;
		if(imagePG == null) {
			imagePG = PG.newPG(P.round(img.width * imageScale), P.round(img.height * imageScale));
			imagePG.beginDraw();
			imagePG.background(255, 0, 0);
			imagePG.endDraw();
		}
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public void setCustomValue(String key, String val) {
		appCustomInfo.put(key, val);
	}
	
	protected void submitJSON(BufferedImage img1, BufferedImage img2) {
		// ADD TEXT DATA TO JSON ---------------------------
		// build JSON object & set basic stats
        JSONObject jsonOut = new JSONObject();
        jsonOut.setString("project", projectName);
        jsonOut.setString("frameCount", P.p.frameCount + "");
        jsonOut.setString("uptime", DateUtil.timeFromSeconds(P.p.millis() / 1000, true) + "");
        jsonOut.setString("frameRate", P.round(P.p.frameRate)+"");
        jsonOut.setString("resolution", P.p.width + "x" + P.p.height);

        // flag for first post after launch
        if(firstPost) jsonOut.setBoolean("relaunch", true);
        firstPost = false;
        
        // add custom data
        JSONObject customProps = new JSONObject();
		for (Map.Entry<String, String> item : appCustomInfo.entrySet()) {
		    String key = item.getKey();
		    String value = item.getValue();
		    if(key != null && value != null) {
		    	customProps.setString(key, value);
		    }
		}
		jsonOut.setJSONObject("custom", customProps);
        
        // ADD IMAGE DATA TO JSON ---------------------------
        // add images to json
		String base64Img = "";
		String base64Screenshot = "";
		try {
			// send a scaled-down image from the app
			if(img1 != null) base64Img = Base64Image.encodeNativeImageToBase64(img1, "png");
			base64Screenshot = Base64Image.encodeNativeImageToBase64(img2, "png");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(img1 != null) jsonOut.setString("imageBase64", base64Img);
        jsonOut.setString("screenshotBase64", base64Screenshot);

        // SEND JSON TO SERVER ------------------------------
        try {
			postJSON.postJsonData(jsonOut, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void takeThreadedScreenshot() {
		new Thread(new Runnable() { public void run() {
			screenshot = ScreenUtil.getScreenShotNativeAllMonitors(0, 0);
		}}).start();	
	}
	
	protected void checkQueuedScreenshot() {
		if(screenshot == null) return;
		
		// copy images and get native buffers on UI thread
		// copy in-app image
		BufferedImage img1 = (image != null) ? (BufferedImage)imagePG.getNative() : null;

		// draw scaled screenshot version into a new native image 
		BufferedImage img = null;
		if(screenshot != null) {
			// make small copy
			int scaledScreenshotW = P.round(screenshot.getWidth() * screenshotScale);
			int scaledScreenshotH = P.round(screenshot.getHeight() * screenshotScale);
			img = new BufferedImage(scaledScreenshotW, scaledScreenshotH, BufferedImage.TYPE_INT_ARGB);
			img.getGraphics().drawImage(screenshot.getScaledInstance(scaledScreenshotW, scaledScreenshotH, 0), 0, 0, null);
		}
		BufferedImage img2 = (image != null) ? img : null;
		
		// send it all to the server
		new Thread(new Runnable() { public void run() {
			submitJSON(img1, img2);
		}}).start();
		
		// clear queue
		screenshot = null;
	}

	//////////////////////////////
	// PostJSON callbacks
	//////////////////////////////

	@Override
	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime) {
		if(debug) P.out("postSuccess", responseText, responseCode, requestId, DateUtil.timeFromMilliseconds(responseTime, false));
	}

	@Override
	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage) {
		if(debug) P.out("postFailure", errorMessage, responseText, responseCode, requestId, DateUtil.timeFromMilliseconds(responseTime, false));
	}

}
