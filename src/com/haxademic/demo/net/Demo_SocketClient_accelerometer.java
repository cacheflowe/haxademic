package com.haxademic.demo.net;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.FloatBuffer;

import processing.data.JSONObject;

public class Demo_SocketClient_accelerometer
extends Demo_SocketClient {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected EasingFloat rotX = new EasingFloat(0, 0.1f);
	protected FloatBuffer rotXBuff = new FloatBuffer(10);
	protected EasingFloat rotZ = new EasingFloat(0, 0.1f);
	protected FloatBuffer rotZBuff = new FloatBuffer(10);
	
	protected void drawApp() {
		background(0);
				
		// draw debug
		pg.beginDraw();
		pg.background(0);
		drawServerLocation();
		drawBox();
		pg.endDraw();
		p.image(pg, 0, 0);
	}
	
	protected void drawBox() {
		rotX.setTarget(rotXBuff.average());
		rotZ.setTarget(rotZBuff.average());
		rotX.update(true);
		rotZ.update(true);
		
		pg.lights();
		pg.pushMatrix();
		PG.setCenterScreen(pg);
		pg.fill(255, 255, 0);
		pg.stroke(0);
		pg.rotateX(rotX.value());
		pg.rotateZ(rotZ.value());
		pg.box(200);
		pg.popMatrix();
	}
	
	////////////////////////////////////////////
	// ISocketClientDelegate methods
	////////////////////////////////////////////

	public void messageReceived(String message) {
		socketLog.update("IN:     " + message);
		if(message.indexOf("pitch") != -1) {
			JSONObject eventData = JSONObject.parse(message);
		    float azimuth = eventData.getFloat("azimuth");	
		    float pitch = eventData.getFloat("pitch");	
//		    float roll = eventData.getFloat("roll");	
		    
		    // rotZ.setTarget(azimuth * -0.2f);
		    rotZBuff.update(azimuth * -0.2f);
		    // rotX.setTarget(pitch * -0.2f);
		    rotXBuff.update(pitch * -0.2f);
		}
	}
	
}
