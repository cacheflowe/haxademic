package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.AppRestart;

public class Demo_AppRestart
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void setup() {
		super.setup();		
	}

	public void drawApp() {
		p.background(0);
		p.text(p.frameCount, 20, 30);
		if(p.frameCount == 90) {
			AppRestart.restart( p );
		}
	}
}
