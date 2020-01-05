package com.haxademic.demo.hardware.mouse;

import java.awt.Point;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;

public class Demo_MouseLock
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int lastLoopFrame = 0;
	protected Point mousePoint;
	protected Point lastMousePoint = new Point();

	protected void firstFrame() {
		// keep mouse locked in window
		P.window.confinePointer(true);
		P.window.setPointerVisible(true);
	}

	protected void drawApp() {
		p.background(0);

		// lock mouse in center, and check offset from last frame
		DebugView.setValue("mouseMoveX", p.mouseX - lastMousePoint.x);
		DebugView.setValue("mouseMoveY", p.mouseY - lastMousePoint.y);
		P.window.warpPointer(width/2, height/2);
		lastMousePoint.setLocation(width/2, height/2);
	}

}