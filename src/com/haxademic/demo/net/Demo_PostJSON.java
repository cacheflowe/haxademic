package com.haxademic.demo.net;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.Base64Image;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.ScreenUtil;
import com.haxademic.core.net.IPostJSONCallback;
import com.haxademic.core.net.PostJSON;
import com.haxademic.core.text.StringFormatter;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class Demo_PostJSON
extends PAppletHax
implements IPostJSONCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PostJSON postJSON;
	protected PGraphics scaledPG;
	protected PGraphics screenshotPG;
	protected PImage screenshot;
	protected boolean firstPost = true;
//	protected String serverPostPath = "http://localhost/_open-source/haxademic/www/post-json/";
	protected String serverPostPath = "http://localhost/_open-source/haxademic/www/dashboard/";
	
	public void setupFirstFrame() {
		PostJSON.DEBUG = true;
		postJSON = new PostJSON(serverPostPath, this);
		scaledPG = p.createGraphics(p.width / 2, p.height / 2, PRenderers.P2D);
		P.out(Arrays.toString(ImageIO.getWriterFormatNames()));
	}
	
	public void drawApp() {
		// background
		pg.beginDraw();
		DrawUtil.setDrawCenter(pg);
		pg.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		pg.fill(255);
		
		// square
		pg.pushMatrix();
		pg.translate(p.width/2, p.height/2);
		pg.rotate(p.frameCount * 0.01f);
		pg.rect(0, 0, 100, 100);
		pg.popMatrix();
		
		// draw to screen
		pg.endDraw();
		p.image(pg, 0, 0);
		
		// if screenshot is queued, send off to dashboard
		checkQueuedScreenshot();
 	}
	
	protected void submitJSON(BufferedImage img1, BufferedImage img2) {
		// build JSON object & set a string
		// jsonOut.setString("date", P.year() + "-" + P.month() + "-" + P.day());
		// jsonOut.setString("time", P.hour() + ":" + P.minute() + ":" + P.second());
        JSONObject jsonOut = new JSONObject();
        jsonOut.setString("project", "haxademic");
        jsonOut.setString("frameCount", p.frameCount + "");
        jsonOut.setString("uptime", StringFormatter.timeFromSeconds(P.p.millis() / 1000, true) + "");
        jsonOut.setString("frameRate", P.round(p.frameRate)+"");
        jsonOut.setString("resolution", P.p.width + "x" + P.p.height);
        
        if(firstPost) jsonOut.setBoolean("relaunch", true);
        firstPost = false;
        
        // add image to json
		String base64Img = "";
		String base64Screenshot = "";
		try {
			// send a scaled-down image from the app
			base64Img = Base64Image.encodePImageToBase64(img1, "png");
			base64Screenshot = Base64Image.encodePImageToBase64(img2, "png");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        jsonOut.setString("imageBase64", base64Img);
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
			if(screenshotPG == null) screenshotPG = p.createGraphics(screenshot.width / 2, screenshot.height / 2, PRenderers.P2D);
		}}).start();	
	}
	
	protected void checkQueuedScreenshot() {
		if(screenshot == null) return;
		
		// copy images and get native buffers on UI thread
		ImageUtil.copyImage(pg, scaledPG);
		ImageUtil.copyImage(screenshot, screenshotPG);
		BufferedImage img1 = (BufferedImage)scaledPG.getNative();
		BufferedImage img2 = (BufferedImage)screenshotPG.getNative();
		new Thread(new Runnable() { public void run() {
			submitJSON(img1, img2);
		}}).start();
		
		// clear queue
		screenshot = null;
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			takeThreadedScreenshot();
		}
	}
	
	//////////////////////////////
	// PostJSON callbacks
	//////////////////////////////

	@Override
	public void postSuccess(String requestId, int responseTime) {
		P.out("postSuccess", requestId, StringFormatter.timeFromMilliseconds(responseTime, false));
	}

	@Override
	public void postFailure(String requestId, int responseTime) {
		P.out("postFailure", requestId, StringFormatter.timeFromMilliseconds(responseTime, false));
	}
}
