package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;

public class AppSizeWatcher {
	
	public interface IAppSizeWatcherDelegate {
		public void appSizeChanged(int appWidth, int appHeight);
		public void appSizeIncorrect(int appWidth, int appHeight);
	}

	protected IAppSizeWatcherDelegate delegate;
	protected int pollingInterval = 60;
	protected int lastScreenW;
	protected int lastScreenH;
	protected int numScreens = 0;
	protected int expectedW = 0;
	protected int expectedH = 0;
	
	public AppSizeWatcher(IAppSizeWatcherDelegate delegate, int pollingInterval) {
		this(delegate, 0, 0, pollingInterval);
	}
	
	public AppSizeWatcher(IAppSizeWatcherDelegate delegate, int expectedW, int expectedH, int pollingInterval) {
		this.delegate = delegate;
		this.pollingInterval = pollingInterval;
		lastScreenW = P.p.width;
		lastScreenH = P.p.height;
		setExpectedAppSize(expectedW, expectedH);
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	public void pre() {
		checkAppSize();
	}
	
	public void setExpectedAppSize(int expectedW, int expectedH) {
		this.expectedW = expectedW;
		this.expectedH = expectedH;
	}
	
	protected void checkAppSize() {
		// only run once every so many frames
		if(P.p.frameCount % pollingInterval != 1) return;
		
		// check to see if the string representation of the screen boundaries has changed
		if(P.p.width != lastScreenW || P.p.height != lastScreenH) {
			lastScreenW = P.p.width;
			lastScreenH = P.p.height;
			delegate.appSizeChanged(P.p.width, P.p.height);
		} else if(expectedW > 0) {
			// or check to see if the expected size is incorrect
			if(P.p.width != expectedW || P.p.height != expectedH) {
				delegate.appSizeIncorrect(P.p.width, P.p.height);
			}
		}
	}
}