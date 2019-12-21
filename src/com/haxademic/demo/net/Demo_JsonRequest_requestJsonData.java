package com.haxademic.demo.net;

import java.io.IOException;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.net.IJsonRequestCallback;
import com.haxademic.core.net.JsonHttpRequest;
import com.haxademic.core.net.JsonRequest;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.system.DateUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class Demo_JsonRequest_requestJsonData
extends PAppletHax
implements IJsonRequestCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected JsonRequest postJSON;
	protected PGraphics scaledPG;
	protected PGraphics screenshotPG;
	protected PImage screenshot;
	protected boolean firstPost = true;
	protected String jsonURL = "http://localhost/haxademic/www/json-response/";
	
	public void firstFrame() {
		postJSON = new JsonRequest(jsonURL);
	}
	
	public void drawApp() {
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
	
	protected void requestJson() {
        // send json to server
        try {
			postJSON.requestJsonData(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			requestJson();
		}
	}
	
	//////////////////////////////
	// PostJSON callbacks
	//////////////////////////////

	@Override
	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime) {
		P.out("postSuccess", responseText, responseCode, requestId, DateUtil.timeFromMilliseconds(responseTime, false));
		if(JsonUtil.isValid(responseText)) {
			JSONObject jsonData = JSONObject.parse(responseText);
			P.out(jsonData.toString());
			if(jsonData.hasKey("success") || jsonData.hasKey("error")) P.out("JSON parse success!");
		} else {
			P.out("JSON.parse() failed"); 
		}
	}

	@Override
	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage) {
		P.out("postFailure", errorMessage, responseText, responseCode, requestId, DateUtil.timeFromMilliseconds(responseTime, false));
	}

	@Override
	public void aboutToRequest(JsonHttpRequest request) {
		
	}
}
