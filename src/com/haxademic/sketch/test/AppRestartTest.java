package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.AppRestart;

public class AppRestartTest
extends PAppletHax  
{

	public static void main(String args[]) {
		PAppletHax.main(P.concat(args, new String[] { "--hide-stop", "--bgcolor=000000", Thread.currentThread().getStackTrace()[1].getClassName() }));
	}

	public void setup() {
		super.setup();		
	}

	public void drawApp() {
		p.background(0);
		p.text(p.frameCount, 20, 30);
		if(p.frameCount == 90) AppRestart.restart( p );
	}
}
