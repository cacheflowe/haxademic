package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;

import processing.core.PGraphics;
import processing.data.JSONObject;

public class Demo_SocketClient_motionTotal
extends Demo_SocketClient {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected boolean batReady = false;
	protected float motionTotal = 0;
	protected float lastPlotY = 0;
	protected float motionMax = 12000;
	protected float motionThresh = motionMax / 2;
	
	protected PGraphics buffer;
	
	protected void firstFrame() {
	    super.firstFrame();
	    
	    buffer = PG.newPG(p.width, p.height);
	    buffer.beginDraw();
	    buffer.background(0);
	    buffer.endDraw();
	}
	
	protected void drawApp() {
		background(0);
		
		// draw history
		buffer.beginDraw();
		buffer.stroke(255, 0, 0);
		if(batReady) buffer.stroke(0, 255, 0);
		buffer.copy(0, 0, buffer.width, buffer.height, -1, 0, buffer.width, buffer.height); // scroll
		float plotX = p.width * 0.8f;
		float plotY = P.map(motionTotal, 0, motionMax, buffer.height, 0);
		buffer.line(plotX, plotY, plotX - 1, lastPlotY);
		lastPlotY = plotY;
		buffer.endDraw();
				
		// draw debug
		pg.beginDraw();
		pg.background(0);
		pg.image(buffer, 0, 0);
		pg.fill(0, 0, 255); pg.rect(0, pg.height / 2, pg.width, 2);
		drawServerLocation();
		pg.endDraw();
		p.image(pg, 0, 0);
	}
	
	////////////////////////////////////////////
	// ISocketClientDelegate methods
	////////////////////////////////////////////

	public void messageReceived(String message) {
		socketLog.update("IN:     " + message);
		if(message.indexOf("motionTotal") != -1) {
			JSONObject eventData = JSONObject.parse(message);
		    motionTotal = eventData.getFloat("motionTotal");	
		}
		if(message.indexOf("position") != -1) {
		    JSONObject eventData = JSONObject.parse(message);
		    batReady = eventData.getString("position").equals("up");	
		}
		if(message.indexOf("swing") != -1) {
		    batReady = false;	
		}
	}
	
}
