package com.haxademic.core.net;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.image.Base64Image;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.ScreenshotBuffer;

import processing.core.PImage;
import processing.data.JSONObject;

public class DashboardCheckinPoller
implements IJsonRequestDelegate {

	// events
	public static String IMAGE_READY_SCREENSHOT = "IMAGE_READY_SCREENSHOT";
	public static String IMAGE_READY_SCREENSHOT_64 = "IMAGE_READY_SCREENSHOT_64";
	public static String IMAGE_READY_CUSTOM = "IMAGE_READY_CUSTOM";
	public static String IMAGE_READY_CUSTOM_64 = "IMAGE_READY_CUSTOM_64";
	
	// basic props 
	public static float JPG_QUALITY = 0.5f;
	public static boolean DEBUG = false;
	protected String appId;
	protected String appTitle;
	protected String dashboardURL;
	protected JsonPoller jsonPoller;
	
	protected LinkedHashMap<String, String> appCustomInfo = new LinkedHashMap<String, String>();
	protected LinkedHashMap<String, Number> appCustomInfoNumeric = new LinkedHashMap<String, Number>();
	
	// images
	
	protected ScreenshotBuffer screenshotBuffer;
	protected Timer screenshotTimer;
	protected boolean screenshotNeedsUpdate = false;
	protected String screenshotBase64 = null;
	
	protected PImage customImg;
	protected PImage customImgToCopy;
	protected BufferedImage customImgNative;
	protected Timer customImgTimer;
	protected String customImgBase64 = null;
	protected boolean customImgNeedsUpdate = false;
	
	public DashboardCheckinPoller(String appId, String appTitle, String checkinURL, int checkinIntervalSeconds, int screenshotIntervalSeconds, float screenshotScale) {
		this.appId = appId;
		this.appTitle = appTitle;

		// init polling to dashboard URL
		// & set initial post data on poller
		if(checkinURL != null) {
			jsonPoller = new JsonPoller(checkinURL, checkinIntervalSeconds * 1000, this);
			jsonPoller.setPostData(getJsonObj());
		}
		
		// add screenshot grabber
		screenshotBuffer = new ScreenshotBuffer();
		screenshotBuffer.addScaledImage(screenshotScale);
		DebugView.setTexture("screenshot", screenshotBuffer.scaledImg());
		
		// then start the timer on repeat
		// put screenshots on a timer to update the buffer once between every post interval
		screenshotIntervalSeconds *= 1000;
		screenshotTimer = new Timer();
		screenshotTimer.schedule(new TimerTask() { public void run() {
			updateScreenshot();
		}}, screenshotIntervalSeconds / 2, screenshotIntervalSeconds);	 // delay, [repeat]

		// take first screenshot one minute after start
		(new Timer()).schedule(new TimerTask() { public void run() {
			updateScreenshot();
		}}, 60 * 1000); 
		
		// subscribe to post for base64 encoding of custom image
		P.p.registerMethod(PRegisterableMethods.post, this);
	}
	
	// set basic json data
	
	protected JSONObject getJsonObj() {
		JSONObject jsonObj = new JSONObject();
		// add standard runtime properties
		jsonObj.setString("appId", appId);								// project
		jsonObj.setString("appTitle", appTitle);
		jsonObj.setString("ipAddress", IPAddress.getLocalAddress().replace("http://", ""));
		jsonObj.setString("resolution", P.p.width + "x" + P.p.height);
		jsonObj.setInt("frameRate", P.round(P.p.frameRate));
		jsonObj.setInt("frameCount", P.p.frameCount);
		jsonObj.setInt("uptime", P.round(P.p.millis() / 1000));
		
		// add images if needed/available
		if(screenshotBase64 != null) {
			jsonObj.setString("imageScreenshot", screenshotBase64);		// imageBase64
			screenshotBase64 = null;
		}
		if(customImgBase64 != null) {
			jsonObj.setString("imageExtra", customImgBase64);			// screenshotBase64
			customImgBase64 = null;
		}
		
		// add custom props
		for (Map.Entry<String, String> item : appCustomInfo.entrySet()) {
		    String key = item.getKey();
		    String value = item.getValue();
		    if(key != null && value != null) {
		    	jsonObj.setString(key, value);
		    }
		}
		for (Map.Entry<String, Number> item : appCustomInfoNumeric.entrySet()) {
			String key = item.getKey();
			float value = item.getValue().floatValue();
			if(key != null) {
				jsonObj.setFloat(key, value);
			}
		}
		
		// clear custom values after adding them to post object
		appCustomInfo.clear();
		appCustomInfoNumeric.clear();
		
		return jsonObj;
	}
	
	// add image data
	
	protected void updateScreenshot() {
		screenshotNeedsUpdate = true;
	}
	
	public PImage customImage() {
		return customImg;
	}
	
	public String customImageBase64() {
		return customImgBase64;
	}
	
	public PImage screenshotImage() {
		return screenshotBuffer.image();
	}
	
	public String screenshotImageBase64() {
		return screenshotBase64;
	}
	
	public void setCustomImage(PImage img, int customImgIntervalSeconds) {
		// store image and create jpeg-encoding-safe copy for posting
		customImg = ImageUtil.newPImageForBase64Jpeg(img.width, img.height);
		customImgToCopy = img;

		// upload first image one minute after setting
		(new Timer()).schedule(new TimerTask() { public void run() {
			customImgNeedsUpdate = true;
		}}, 60 * 1000); 

		// start timer to base64 custom image on interval
		customImgIntervalSeconds *= 1000;
		if(customImgTimer != null) customImgTimer.cancel();
		customImgTimer = new Timer();
		customImgTimer.schedule(new TimerTask() { public void run() {
			customImgNeedsUpdate = true;
		}}, customImgIntervalSeconds / 2, customImgIntervalSeconds);	 // delay, [repeat]
	}
	
	public void post() {
		// base64 encode on ui thread
		if(screenshotNeedsUpdate) {
			screenshotBuffer.updateScreenshot();
			screenshotBase64 = screenshotBuffer.base64Scaled(JPG_QUALITY);
			screenshotNeedsUpdate = false;
			P.store.setString(IMAGE_READY_SCREENSHOT_64, screenshotBase64);
			P.store.setImage(IMAGE_READY_SCREENSHOT, screenshotBuffer.image());
		}
		if(customImgNeedsUpdate) {
			// copy image into special jpeg-encoding-safe format
			ImageUtil.copyImage(customImgToCopy, customImg);

//			customImgBase64 = Base64Image.encodePImageToBase64(customImg, "jpg");
			customImgBase64 = Base64Image.encodeImageToBase64Jpeg(customImg, JPG_QUALITY);
			customImgNeedsUpdate = false;
			P.store.setString(IMAGE_READY_CUSTOM_64, customImgBase64);
			P.store.setImage(IMAGE_READY_CUSTOM, customImg);

		}
	}
	
	// add custom data
	
	public void setCustomValue(String key, String val) {
		appCustomInfo.put(key, val);
	}
	
	public void setCustomValue(String key, Number val) {
		appCustomInfoNumeric.put(key, val);
	}
	

	
	////////////////////////////////////////
	// IJsonRequestCallback callbacks 
	////////////////////////////////////////
	
	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime) {
		if(DEBUG == true) {
			P.out("DashboardCheckinPoller: Checkin success!");
			P.out(responseText);
		}
	}

	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage) {
		if(DEBUG == true) {
			P.out("DashboardCheckinPoller: API Unreachable");
			P.out("-- " + errorMessage);
		}
	}	
	
	public void aboutToRequest(JsonHttpRequest request) {
		// update post data on poller
		if(jsonPoller == null) return;
		JSONObject postData = (jsonPoller != null) ? getJsonObj() : new JSONObject();	// send an empty object if we're not fully initialized
		jsonPoller.setPostData(postData);
	}

}
