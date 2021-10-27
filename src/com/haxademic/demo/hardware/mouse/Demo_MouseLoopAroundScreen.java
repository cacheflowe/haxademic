package com.haxademic.demo.hardware.mouse;

import java.awt.Point;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.mouse.Mouse;

public class Demo_MouseLoopAroundScreen
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int lastLoopFrame = 0;
	protected Point mousePoint;
	protected Point lastMousePoint = new Point();

	protected void firstFrame() {
		P.p.registerMethod("post", this);
	}

	protected void drawApp() {
		p.background(0);
		
		//get native window object
		DebugView.setValue("window width", P.window.getBounds().getWidth());
		DebugView.setValue("window height", P.window.getBounds().getHeight());
		DebugView.setValue("window hasFocus", P.window.hasFocus());
		DebugView.setValue("p.mouseX", p.mouseX);
		DebugView.setValue("p.mouseY", p.mouseY);
	}
	
	public void post() {
		// update mouse w/system location
		mousePoint = Mouse.systemMouseLocation();
		DebugView.setValue("mousePoint.x", mousePoint.x);
		DebugView.setValue("mousePoint.y", mousePoint.y);
		
		// wrap mouse 
		int padding = 1;
		if(p.frameCount > lastLoopFrame + 1) {
			if(mousePoint.x == p.displayWidth - 1 && lastMousePoint.x < mousePoint.x) { Mouse.movePointerTo(padding, mousePoint.y); lastLoopFrame = p.frameCount; }
			else if(mousePoint.x == 0 && lastMousePoint.x > mousePoint.x) { Mouse.movePointerTo(p.displayWidth - padding, mousePoint.y); lastLoopFrame = p.frameCount; }
			else if(mousePoint.y == p.displayHeight - 1 && lastMousePoint.y < mousePoint.y) { Mouse.movePointerTo(mousePoint.x, padding); lastLoopFrame = p.frameCount; }
			else if(mousePoint.y == 0 && lastMousePoint.y > mousePoint.y) { Mouse.movePointerTo(mousePoint.x, p.displayHeight - padding); lastLoopFrame = p.frameCount; }
		}
		
		// store last mouse position
		lastMousePoint.setLocation(mousePoint);
	}

}