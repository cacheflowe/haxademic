package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.system.AppSizeWatcher;
import com.haxademic.core.system.AppSizeWatcher.IAppSizeWatcherDelegate;
import com.haxademic.core.system.AppUtil;

public class Demo_AppSizeWatcher
extends PAppletHax
implements IAppSizeWatcherDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected AppSizeWatcher appSizeWatcher;
	protected LinearFloat changedFlash = new LinearFloat(0, 0.04f);
	
	protected void config() {
		Config.setAppSize(800, 600);
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.RESIZABLE, true );
	}

	protected void firstFrame() {
		appSizeWatcher = new AppSizeWatcher(this, 120);	// check every 2 seconds. should be much less often, IRL, probably
		P.out("Initial app size:", P.p.width + ", " + P.p.height);
	}

	protected void drawApp() {
		changedFlash.update();
		background(changedFlash.value() * 255);
	}

	//////////////////////////
	// IAppSizeWatcherDelegate methods
	//////////////////////////

	@Override
	public void appSizeChanged(int appWidth, int appHeight) {
		P.out("New app size!", "[appSizeWatcher]", appWidth + ", " + appHeight);
		if(p.width != 800 || p.height != 600) {
			changedFlash.setTarget(0).setCurrent(1);
			AppUtil.setSize(p, 800, 600);
			AppUtil.setLocation(p, 0, 0);
		}
	}
	
}
