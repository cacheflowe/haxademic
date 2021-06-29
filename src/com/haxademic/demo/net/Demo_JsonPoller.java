package com.haxademic.demo.net;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.net.IJsonRequestDelegate;
import com.haxademic.core.net.JsonHttpRequest;
import com.haxademic.core.net.JsonPoller;

import processing.data.JSONObject;

public class Demo_JsonPoller
extends PAppletHax
implements IJsonRequestDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected JsonPoller poller;
	protected static HashMap<String, String> headers;
	static {
		headers = new HashMap<String, String>();
		headers.put("x-api-key", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		headers.put("Content-Type", "application/json");
	};

	
	protected void firstFrame() {
		poller = new JsonPoller("http://localhost/haxademic/www/post-json/", 5000, this);
		poller.setPostData(new JSONObject());
		poller.setHeaders(headers);
	}
	
	protected void drawApp() {
		// background
		pg.beginDraw();
		PG.setDrawCenter(pg);
		pg.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		pg.fill(255);
		pg.endDraw();
		p.image(pg, 0, 0);
	}
		
	////////////////////////////////////////
	// ICachedJsonPollerDelegate methods
	////////////////////////////////////////
	
	public void aboutToRequest(JsonHttpRequest request) {
		P.out("aboutToRequest");
	}

	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime) {
		P.out("postSuccess", responseText);
		
	}

	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage) {
		P.out("postFailure", responseText);
	}
	
}
