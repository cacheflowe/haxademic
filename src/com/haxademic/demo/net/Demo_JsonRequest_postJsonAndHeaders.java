package com.haxademic.demo.net;

import java.io.IOException;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.net.IJsonRequestDelegate;
import com.haxademic.core.net.JsonHttpRequest;
import com.haxademic.core.net.JsonRequest;
import com.haxademic.core.system.DateUtil;

import processing.core.PGraphics;
import processing.data.JSONObject;

public class Demo_JsonRequest_postJsonAndHeaders
extends PAppletHax
implements IJsonRequestDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected JsonRequest postJSON;
	protected PGraphics scaledPG;
	protected String serverPostPath = "http://localhost/haxademic/www/post-json/";
	protected static HashMap<String, String> headers;
	static {
		headers = new HashMap<String, String>();
		headers.put("x-api-key", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		headers.put("Content-Type", "application/json");
	};

	protected void firstFrame() {
		postJSON = new JsonRequest(serverPostPath);
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
	
	protected void submitJSON() {
        // send json to server
        try {
			postJSON.postJsonDataWithHeaders(new JSONObject(), headers, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			submitJSON();
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
