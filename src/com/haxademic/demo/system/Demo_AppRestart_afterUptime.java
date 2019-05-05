package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.Millis;
import com.haxademic.core.system.AppRestart;

public class Demo_AppRestart_afterUptime
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		p.background(0);
		p.text(uptimeHours() + FileUtil.NEWLINE + P.hour() + ":" + P.minute() + ":" + P.second(), 20, 30);
		
		// restart if running for x hours and the local time is between 7-8pm
		if(uptimeHours() > 0.01f && P.hour() >= 19 && P.hour() < 20) {
			AppRestart.restart( p );
		}
	}
	
	protected float uptimeHours() {
		return Millis.msToHours(p.millis());
	}
}
