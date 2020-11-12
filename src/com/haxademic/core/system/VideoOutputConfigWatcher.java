package com.haxademic.core.system;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;

public class VideoOutputConfigWatcher {
	
	public interface IVideoOutputConfigWatcher {
		public void videoOutputConfigChanged(int screensWidth, int screensHeight);
	}

	protected IVideoOutputConfigWatcher delegate;
	protected int pollingInterval = 60;
	protected Rectangle screenBounds = new Rectangle();
	protected String screenBoundsStr = null;
	protected int numScreens = 0;
	
	public VideoOutputConfigWatcher(IVideoOutputConfigWatcher delegate, int pollingInterval) {
		this.delegate = delegate;
		this.pollingInterval = pollingInterval;
		checkScreensConfig();
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	public int screensWidth() {
		return screenBounds.width;
	}

	public int screensHeight() {
		return screenBounds.height;
	}
	
	public String screenBoundsStr() {
		return screenBoundsStr;
	}
	
	public int numScreens() {
		return numScreens;
	}
	
	public void pre() {
		checkScreensConfig();
	}
	
	protected void checkScreensConfig() {
		// only run once every so many frames
		if(P.p.frameCount % pollingInterval != 1) return;
		
		// reset rectangle
		screenBounds.setBounds(0, 0, 0, 0);

		// get screen size
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (int j = 0; j < gs.length; j++) { 
			GraphicsDevice gd = gs[j];
			GraphicsConfiguration[] gc = gd.getConfigurations();
			for (int i=0; i < gc.length; i++) {
				screenBounds = screenBounds.union(gc[i].getBounds());
			}
		}
		
		// get number of video outputs
		int newNumScreens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
		
		// check to see if the string representation of the screen boundaries has changed
		if(screenBounds.toString().equals(screenBoundsStr) == false || newNumScreens != numScreens) {
			// run callback, but don't call it the first time
			if(screenBoundsStr != null) {
				delegate.videoOutputConfigChanged(screensWidth(), screensHeight());
			}
			
			// store current config
			screenBoundsStr = screenBounds.toString();
			numScreens = newNumScreens;
		}
	}
}