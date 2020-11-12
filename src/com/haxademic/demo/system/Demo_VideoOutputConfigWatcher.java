package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.system.VideoOutputConfigWatcher;
import com.haxademic.core.system.VideoOutputConfigWatcher.IVideoOutputConfigWatcher;

public class Demo_VideoOutputConfigWatcher
extends PAppletHax
implements IVideoOutputConfigWatcher {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected VideoOutputConfigWatcher videoConfigWatcher;
	
	protected void config() {
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		videoConfigWatcher = new VideoOutputConfigWatcher(this, 120);	// check every 2 seconds. should be much less often, IRL, probably
		P.out("Initial screens size:", videoConfigWatcher.screensWidth(), videoConfigWatcher.screensHeight());
	}

	protected void drawApp() {
		background(0);
	}

	//////////////////////////
	// IVideoOutputConfigWatcher delegate methods
	//////////////////////////

	public void videoOutputConfigChanged(int screensWidth, int screensHeight) {
		P.out("New screen config!", screensWidth, screensHeight);
	}
	
}
