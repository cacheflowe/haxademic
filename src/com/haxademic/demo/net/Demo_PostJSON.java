package com.haxademic.demo.net;

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
import com.haxademic.core.net.IPostJSONCallback;
import com.haxademic.core.net.PostJSON;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.text.StringFormatter;

import processing.core.PGraphics;
import processing.data.JSONObject;

public class Demo_PostJSON
extends PAppletHax
implements IPostJSONCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected WebServer server;
	protected PostJSON postJSON;
	protected PGraphics scaledPG;
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
 	}
	
	protected void submitJSON() {
		// build JSON object & set a string
        JSONObject jsonOut = new JSONObject();
        jsonOut.setString("project", "haxademic");
        jsonOut.setString("frameCount", p.frameCount + "");
        jsonOut.setString("frameRate", P.round(p.frameRate)+"");
        
        // add image to json
		String base64Img = "";
		try {
			ImageUtil.copyImage(pg, scaledPG);
			base64Img = Base64Image.encodePImageToBase64(scaledPG, "png");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        jsonOut.setString("imageBase64", base64Img);

        // send json to server
        try {
			postJSON.sendData(jsonOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') submitJSON();
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
