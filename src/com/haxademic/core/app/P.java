package com.haxademic.core.app;

import com.haxademic.core.data.store.AppStore;
import com.haxademic.core.file.FileUtil;
import com.jogamp.opengl.GL;

import processing.core.PApplet;
import processing.core.PImage;

public class P
extends PApplet {
	
	// static app object refs
	
	public static PAppletHax p;
	public static GL gl;	
	public static AppStore store;	
	
	// helper methods
	
	// image loading 
	
	public static PImage getImage(String file) {
		return P.p.loadImage(FileUtil.getFile(file));
	}
	
	// logging
	
	public static boolean logging = true;
	public static void out(Object... objs) {
		if(logging) P.println(objs);
	}
}
