package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.SystemUtil;

public class Demo_KillAllJava
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void firstFrame() {
		
	}

	protected void drawApp() {
		p.background(0);
		p.text(p.frameCount, 20, 30);
		if(p.frameCount == 90) {
		    SystemUtil.killAllJavaWindows();
		}
	}
}
