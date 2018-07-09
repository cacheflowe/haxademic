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
	
	public static PImage getImage(String file) {
		return P.p.loadImage(FileUtil.getFile(file));
	}
}
