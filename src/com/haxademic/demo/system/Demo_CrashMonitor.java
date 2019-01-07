package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.system.CrashMonitor;

public class Demo_CrashMonitor 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected CrashMonitor appMonitor;

	protected void overridePropsFile() {
//		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.APP_NAME, "CrashMonitor Test" );
	}

	public void setupFirstFrame() {
		appMonitor = new CrashMonitor(true, 5000);
	}

	public void drawApp() {
		DrawUtil.setDrawCenter(p);
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
	
	public void mouseClicked() {
		super.mouseClicked();
		// crash!
		Object nulll = null;
		nulll.toString();
	}

}
