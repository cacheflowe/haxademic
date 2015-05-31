package com.haxademic.core.app;

import java.awt.image.BufferedImage;

import processing.core.PApplet;
import processing.core.PImage;

import com.apple.eawt.Application;

public class AppUtil {
	
	public static void setFrameBackground(PApplet p, float r, float g, float b) {
		if(p.frame != null) {
			p.frame.setBackground(new java.awt.Color(r,g,b));
		}
	}
	
	public static void removeChrome(PApplet p) {
		if(p.frame != null) {
			p.frame.removeNotify();
			p.frame.setUndecorated(true);
			p.frame.addNotify();
		}
	}
	
	public static void setTitle(PApplet p, String title) {
		if(p.frame != null) {
			p.frame.setTitle(title);
		}
	}

	public static void setAppToDockIcon(PApplet p) {
		Application application = Application.getApplication();
		application.setDockIconImage((BufferedImage)p.get().getNative());
	}
	
	public static void setPImageToDockIcon(PImage img) {
		Application application = Application.getApplication();
		application.setDockIconImage((BufferedImage)img.get().getNative());
	}
	
	public static void requestForeground() {
		Application application = Application.getApplication();
		application.requestForeground(true);
	}
	
}
