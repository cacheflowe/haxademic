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
import com.haxademic.core.net.IJsonRequestCallback;
import com.haxademic.core.net.JsonRequest;
import com.haxademic.core.text.StringFormatter;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class Demo_JsonRequest_postJsonData
extends PAppletHax
implements IJsonRequestCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected JsonRequest postJSON;
	protected PGraphics scaledPG;
	protected String serverPostPath = "http://localhost/haxademic/www/post-json/";
	
	public void setupFirstFrame() {
		postJSON = new JsonRequest(serverPostPath);
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
 	}
	
	protected void submitJSON(BufferedImage img1) {
		// build JSON object & set a string
        JSONObject jsonOut = new JSONObject();
        jsonOut.setString("project", "test");
        jsonOut.setString("frameCount", p.frameCount + "");
        jsonOut.setString("uptime", StringFormatter.timeFromSeconds(P.p.millis() / 1000, true) + "");
        jsonOut.setString("frameRate", P.round(p.frameRate)+"");
        jsonOut.setString("resolution", P.p.width + "x" + P.p.height);
        
        // add image to json
		String base64Img = "";
		try {
			base64Img = Base64Image.encodePImageToBase64(img1, "png");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        jsonOut.setString("imageBase64", base64Img);

        // send json to server
        try {
			postJSON.postJsonData(jsonOut, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	protected void checkQueuedScreenshot() {
		// copy images and get native buffers on UI thread
		ImageUtil.copyImage(pg, scaledPG);
		BufferedImage img1 = (BufferedImage)scaledPG.getNative();
		
		new Thread(new Runnable() { public void run() {
			submitJSON(img1);
		}}).start();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			checkQueuedScreenshot();
		}
	}
	
	//////////////////////////////
	// PostJSON callbacks
	//////////////////////////////

	@Override
	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime) {
		P.out("postSuccess", responseText, responseCode, requestId, StringFormatter.timeFromMilliseconds(responseTime, false));
	}

	@Override
	public void postFailure(String responseText, int responseCode, String requestId, int responseTime) {
		P.out("postFailure", responseText, responseCode, requestId, StringFormatter.timeFromMilliseconds(responseTime, false));
	}
}
