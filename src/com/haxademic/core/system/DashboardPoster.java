package com.haxademic.core.system;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.haxademic.core.app.P;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.image.Base64Image;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.ScreenUtil;
import com.haxademic.core.net.IPostJSONCallback;
import com.haxademic.core.net.PostJSON;
import com.haxademic.core.text.StringFormatter;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class DashboardPoster 
implements IPostJSONCallback {

	protected String projectName = "haxademic";
	protected String serverPostPath = "http://localhost/_open-source/haxademic/www/dashboard/";
	protected PostJSON postJSON;
	protected PGraphics imagePG;
	protected PGraphics screenshotPG;
	protected PImage image;
	protected PImage screenshot;
	protected float imageScale = 1;
	protected float screenshotScale = 1;
	protected int postInterval = 60 * 60 * 1000;
	protected int lastPostTime = 0;
	public static boolean firstPost = true; // static in case of multiple instances

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
		
		postJSON = new PostJSON(serverPostPath, this);
	}
	
	// app frame loop
	public void post() {
		if(P.p.millis() > lastPostTime + postInterval) {
			// PostJSON.DEBUG = true;
			lastPostTime = P.p.millis();
			takeThreadedScreenshot();
		}
		checkQueuedScreenshot();
	}
	
	public void setImage(PImage img) {
		image = img;
		if(imagePG == null) imagePG = P.p.createGraphics(P.round(img.width * imageScale), P.round(img.height * imageScale), PRenderers.P2D);
	}
	
	protected void submitJSON(BufferedImage img1, BufferedImage img2) {
		// build JSON object & set basic stats
        JSONObject jsonOut = new JSONObject();
        jsonOut.setString("project", "test");
        jsonOut.setString("frameCount", P.p.frameCount + "");
        jsonOut.setString("uptime", StringFormatter.timeFromSeconds(P.p.millis() / 1000, true) + "");
        jsonOut.setString("frameRate", P.round(P.p.frameRate)+"");
        jsonOut.setString("resolution", P.p.width + "x" + P.p.height);

        // flag for first post after launch
        if(firstPost) jsonOut.setBoolean("relaunch", true);
        firstPost = false;
        
        // add images to json
		String base64Img = "";
		String base64Screenshot = "";
		try {
			// send a scaled-down image from the app
			if(img1 != null) base64Img = Base64Image.encodePImageToBase64(img1, "png");
			base64Screenshot = Base64Image.encodePImageToBase64(img2, "png");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(img1 != null) jsonOut.setString("imageBase64", base64Img);
        jsonOut.setString("screenshotBase64", base64Screenshot);

        // send json to server
        try {
			postJSON.sendData(jsonOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void takeThreadedScreenshot() {
		new Thread(new Runnable() { public void run() {
			screenshot = ScreenUtil.getScreenShotAllMonitors();
			if(screenshotPG == null) screenshotPG = P.p.createGraphics(P.round(screenshot.width * screenshotScale), P.round(screenshot.height * screenshotScale), PRenderers.P2D);
		}}).start();	
	}
	
	protected void checkQueuedScreenshot() {
		if(screenshot == null) return;
		
		// copy images and get native buffers on UI thread
		if(image != null && imagePG != null) ImageUtil.copyImage(image, imagePG);
		ImageUtil.copyImage(screenshot, screenshotPG);
		BufferedImage img1 = (image != null) ? (BufferedImage)imagePG.getNative() : null;
		BufferedImage img2 = (BufferedImage)screenshotPG.getNative();
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
	public void postSuccess(String requestId, int responseTime) {
		if(PostJSON.DEBUG) P.out("postSuccess", requestId, StringFormatter.timeFromMilliseconds(responseTime, false));
	}

	@Override
	public void postFailure(String requestId, int responseTime) {
		if(PostJSON.DEBUG) P.out("postFailure", requestId, StringFormatter.timeFromMilliseconds(responseTime, false));
	}

}
