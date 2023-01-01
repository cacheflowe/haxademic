package com.haxademic.demo.debug;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.math.MathUtil;

public class Demo_StringBufferLog 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected StringBufferLog logOut = new StringBufferLog(10);

	protected void drawApp() {
		background(0);
		if(MathUtil.randBooleanWeighted(0.05f)) logOut.update("Frame " + p.frameCount);
		logOut.printToScreen(p.g, 20, 20);
//		logOut.printToScreen(p.g, 20, 20, true, 40);
		
		// show oldest item
		p.text(logOut.itemAt(0), 120, 20);
	}

	public void keyPressed() {
		super.keyPressed();
		logOut.update("Key pressed!");
	}

}