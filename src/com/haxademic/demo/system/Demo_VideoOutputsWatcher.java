package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.system.VideoOutputsWatcher;
import com.haxademic.core.system.VideoOutputsWatcher.IVideoOutputsWatcherDelegate;

public class Demo_VideoOutputsWatcher
extends PAppletHax
implements IVideoOutputsWatcherDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected VideoOutputsWatcher videoOutputsWatcher;
	
	protected void config() {
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		videoOutputsWatcher = new VideoOutputsWatcher(this, 120);	// check every 2 seconds. should be much less often, IRL, probably
		P.out("Initial screens config:", "[" + videoOutputsWatcher.numScreens() + "]", videoOutputsWatcher.screensWidth() + ", " + videoOutputsWatcher.screensHeight());
	}

	protected void drawApp() {
		background(0);
	}

	//////////////////////////
	// IVideoOutputConfigWatcher delegate methods
	//////////////////////////

	public void videoOutputsChanged(int screensWidth, int screensHeight) {
		P.out("New screens config!", "[" + videoOutputsWatcher.numScreens() + "]", screensWidth + ", " + screensHeight);
	}
	
}
