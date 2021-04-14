package com.haxademic.demo.hardware.mouse;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.mouse.MouseShutdown;
import com.haxademic.core.render.FrameLoop;

public class Demo_MouseShutdown
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected MouseShutdown mouseShutdown;
	
	protected void firstFrame() {
		mouseShutdown = new MouseShutdown(10, 3000);
	}

	protected void drawApp() {
		p.background(FrameLoop.osc(0.01f, 0, 255));
	}

}