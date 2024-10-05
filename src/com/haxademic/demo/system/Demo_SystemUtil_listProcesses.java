package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.system.SystemUtil;

public class Demo_SystemUtil_listProcesses
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "520" );
		Config.setProperty( AppSettings.HEIGHT, "120" );
	}
	
	protected void firstFrame() {
		// print all
		// SystemUtil.printRunningProcesses();
		SystemUtil.printRunningProcessesSorted();
		// count specific
		String processName = "Sports.exe";
		int vsCodeCount = SystemUtil.countProcessesByString(processName);
		P.out("=====================================");
		P.out("=====================================");
		P.out("== " + vsCodeCount + " processes found for: " + processName);
		P.out("=====================================");
		P.out("=====================================");
	}
	
	protected void drawApp() {
		p.background(0);
	}
}
