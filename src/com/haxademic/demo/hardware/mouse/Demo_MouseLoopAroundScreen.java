package com.haxademic.demo.hardware.mouse;

import java.awt.MouseInfo;
import java.awt.Point;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.mouse.MouseUtil;

public class Demo_MouseLoopAroundScreen
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int lastLoopFrame = 0;
	protected Point mousePoint;
	protected Point lastMousePoint = new Point();

	public void setupFirstFrame() {
		P.p.registerMethod("post", this);
	}

	public void drawApp() {
		p.background(0);
		
		//get native window object
		p.debugView.setValue("window width", window.getBounds().getWidth());
		p.debugView.setValue("window height", window.getBounds().getHeight());
		p.debugView.setValue("window hasFocus", window.hasFocus());
		p.debugView.setValue("p.mouseX", p.mouseX);
		p.debugView.setValue("p.mouseY", p.mouseY);
	}
	
	public void post() {
		// update mouse w/system location
		mousePoint = MouseInfo.getPointerInfo().getLocation();
		p.debugView.setValue("mousePoint.x", mousePoint.x);
		p.debugView.setValue("mousePoint.y", mousePoint.y);
		
		// wrap mouse 
		int padding = 1;
		if(p.frameCount > lastLoopFrame + 1) {
			if(mousePoint.x == p.displayWidth - 1 && lastMousePoint.x < mousePoint.x) { MouseUtil.movePointerTo(padding, mousePoint.y); lastLoopFrame = p.frameCount; }
			else if(mousePoint.x == 0 && lastMousePoint.x > mousePoint.x) { MouseUtil.movePointerTo(p.displayWidth - padding, mousePoint.y); lastLoopFrame = p.frameCount; }
			else if(mousePoint.y == p.displayHeight - 1 && lastMousePoint.y < mousePoint.y) { MouseUtil.movePointerTo(mousePoint.x, padding); lastLoopFrame = p.frameCount; }
			else if(mousePoint.y == 0 && lastMousePoint.y > mousePoint.y) { MouseUtil.movePointerTo(mousePoint.x, p.displayHeight - padding); lastLoopFrame = p.frameCount; }
		}
		
		// store last mouse position
		lastMousePoint.setLocation(mousePoint);
	}

}