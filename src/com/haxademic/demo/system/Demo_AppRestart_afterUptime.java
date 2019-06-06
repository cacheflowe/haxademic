package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.AppRestart;
import com.haxademic.core.system.DateUtil;

public class Demo_AppRestart_afterUptime
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		p.background(0);
		p.text(DateUtil.uptimeHours() + FileUtil.NEWLINE + P.hour() + ":" + P.minute() + ":" + P.second(), 20, 30);
		
		// restart if running for x hours and the local time is between 7-8pm
		if(DateUtil.uptimeHours() > 0.01f && DateUtil.timeIsBetweenHours(0, 1)) {
			AppRestart.restart( p );
		}
	}
	
}
