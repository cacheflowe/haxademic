package com.haxademic.core.system;

import processing.core.PApplet;
import processing.core.PImage;

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
			p.getSurface().setTitle(title);
		}
	}

	public static void setAppToDockIcon(PApplet p) {
		if(System.getProperty("os.name").contains("Mac OS") == false) return;
//		Application application = Application.getApplication();
//		application.setDockIconImage((BufferedImage)p.get().getNative());
	}
	
	public static void setPImageToDockIcon(PImage img) {
		if(System.getProperty("os.name").contains("Mac OS") == false) return;
//		Application application = Application.getApplication();
//		application.setDockIconImage((BufferedImage)img.get().getNative());
	}
	
	public static void requestForeground(final PApplet p) {
		if(System.getProperty("os.name").contains("Mac OS") == false) return;
//		Application application = Application.getApplication();
//		application.requestForeground(true);
		
		
		if(p.frame != null) {
			java.awt.EventQueue.invokeLater(new Runnable() {
			    @Override
			    public void run() {
//			    	int sta = p.frame.getExtendedState() & ~JFrame.ICONIFIED & JFrame.NORMAL;
//
//			        p.frame.setExtendedState(sta);
			        p.frame.setAlwaysOnTop(true);
			        p.frame.toFront();
			        p.frame.requestFocus();
//			    	p.frame.repaint();
			    	 
//			    	p.frame.setExtendedState(JFrame.ICONIFIED);
//			    	p.frame.setExtendedState(JFrame.NORMAL);
//			    	p.frame.setState(java.awt.Frame.ICONIFIED);
//			    	p.frame.setState(java.awt.Frame.NORMAL);
			    }
			});
		}

	}
	
}
