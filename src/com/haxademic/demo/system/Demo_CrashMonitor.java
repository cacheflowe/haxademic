package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.system.CrashMonitor;

public class Demo_CrashMonitor 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected CrashMonitor appMonitor;

	protected void config() {
		Config.setProperty( AppSettings.APP_NAME, "CrashMonitor Test" );
	}

	protected void firstFrame() {
		boolean RESTARTS = true;	// if false, we're expecting CrashMonitor to quit the app (rather than restart), 
									// and the run.cmd script will restart it after a delay. 
									// If true, we shouldn't have restart recovery built into the run.cmd script.
		appMonitor = new CrashMonitor(true, 5000, RESTARTS);
//		WindowsSystemUtil.killOtherJavaApps();
	}

	protected void drawApp() {
		PG.setDrawCenter(p);
		p.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		p.fill(255);
		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		p.rotate(p.frameCount * 0.01f);
		p.rect(0, 0, 100, 100);
		p.popMatrix();
		
		p.fill(255);
		p.text("CLICK TO CRASH", 20, 30);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'c') mouseClicked();
		if(p.key == 'x') p.exit();
	}

	
	@SuppressWarnings("null")
	public void mouseClicked() {
		super.mouseClicked();
		// crash!
		Object nulll = null;
		nulll.toString();
	}

}
