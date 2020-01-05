package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.net.HttpRequest;
import com.haxademic.core.net.HttpRequest.IHttpRequestCallback;

public class Demo_HttpRequest
extends PAppletHax
implements IHttpRequestCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected HttpRequest request;
	
	protected void drawApp() {
		// background
		pg.beginDraw();
		PG.setDrawCenter(pg);
		pg.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		pg.fill(255);
		pg.endDraw();
		p.image(pg, 0, 0);
	}
	
	protected void makeRequest() {
		HttpRequest.DEBUG = true;
		if(request == null) request = new HttpRequest(this);
		request.send("http://cacheflowe.com");
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') makeRequest();
	}

	// IHttpRequestCallback methods
	
	public void postSuccess(String responseText, int responseCode) {
		P.out("HttpRequest success:", responseText);
	}

	public void postFailure(String errorMessage) {
		P.out("HttpRequest error:", errorMessage);
	}
}
