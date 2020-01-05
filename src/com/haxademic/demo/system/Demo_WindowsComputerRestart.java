package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.SystemUtil;

public class Demo_WindowsComputerRestart
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void drawApp() {
		p.background(0);
		p.text(p.frameCount, 20, 30);
		if(p.frameCount == 90) {
			try { 
				// /K : runs command specified by following string 
				// -r = restart
				// -t 10 = wait 10 seconds
				SystemUtil.runShellCommand(SystemUtil.WINDOWS_RESTART_COMMAND);
			} catch (Exception e) { 
				System.out.println("Didn't work!"); 
				e.printStackTrace(); 
			} 		
		}
	}
}
