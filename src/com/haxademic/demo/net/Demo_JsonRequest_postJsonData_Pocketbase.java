package com.haxademic.demo.net;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.Base64Image;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.net.IJsonRequestDelegate;
import com.haxademic.core.net.JsonHttpRequest;
import com.haxademic.core.net.JsonRequest;
import com.haxademic.core.system.DateUtil;
import com.haxademic.core.text.RandomStringUtil;

import processing.core.PGraphics;
import processing.data.JSONObject;

public class Demo_JsonRequest_postJsonData_Pocketbase
extends PAppletHax
implements IJsonRequestDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected JsonRequest postJSON;
	protected PGraphics scaledPG;
	protected String serverPostPath = "http://127.0.0.1:8090/api/collections/inventory/records";
	
	protected void firstFrame() {
		postJSON = new JsonRequest(serverPostPath);
		scaledPG = p.createGraphics(p.width / 2, p.height / 2, PRenderers.P2D);
	}
	
	protected void drawApp() {
		// background
		pg.beginDraw();
		PG.setDrawCenter(pg);
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
        jsonOut.setString("name", RandomStringUtil.randomString());
        jsonOut.setInt("age", p.frameCount);
        
        // add image to json
		String base64Img = "";
		try {
			base64Img = Base64Image.encodeNativeImageToBase64(img1, "png");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//        jsonOut.setString("imageBase64", base64Img);

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
		P.out("postSuccess", responseText, responseCode, requestId, DateUtil.timeFromMilliseconds(responseTime, false));
	}

	@Override
	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage) {
		P.out("postFailure", errorMessage, responseText, responseCode, requestId, DateUtil.timeFromMilliseconds(responseTime, false));
	}

	@Override
	public void aboutToRequest(JsonHttpRequest request) {
		
	}
}
