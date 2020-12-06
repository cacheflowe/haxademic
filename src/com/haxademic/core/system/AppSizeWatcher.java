package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;

public class AppSizeWatcher {
	
	public interface IAppSizeWatcherDelegate {
		public void appSizeChanged(int appWidth, int appHeight);
	}

	protected IAppSizeWatcherDelegate delegate;
	protected int pollingInterval = 60;
	protected int lastScreenW;
	protected int lastScreenH;
	protected int numScreens = 0;
	
	public AppSizeWatcher(IAppSizeWatcherDelegate delegate, int pollingInterval) {
		this.delegate = delegate;
		this.pollingInterval = pollingInterval;
		lastScreenW = P.p.width;
		lastScreenH = P.p.height;
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	public void pre() {
		checkAppSize();
	}
	
	protected void checkAppSize() {
		// only run once every so many frames
		if(P.p.frameCount % pollingInterval != 1) return;
		
		// check to see if the string representation of the screen boundaries has changed
		if(P.p.width != lastScreenW || P.p.height != lastScreenH) {
			// store screen size
			lastScreenW = P.p.width;
			lastScreenH = P.p.height;
			
			// run callback
			delegate.appSizeChanged(P.p.width, P.p.height);
		}
	}
}