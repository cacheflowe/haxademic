package com.haxademic.core.app;

import com.haxademic.core.data.store.AppStore;
import com.haxademic.core.data.store.AppStoreDistributed;
import com.haxademic.core.file.FileUtil;
import com.jogamp.opengl.GL;

import processing.core.PApplet;
import processing.core.PImage;

public class P
extends PApplet {
	
	// static app object refs
	
	public static PAppletHax p;
	public static AppStore store;	
	public static AppStoreDistributed storeDistributed;	
	
	// helper methods
	
	// image loading 
	
	public static PImage getImage(String file) {
		return P.p.loadImage(FileUtil.getFile(file));
	}
	
	// logging
	
	public static boolean logging = true;
	public static void out(Object ...args) {
		if(logging) P.println(args);
	}
	public static void error(Object ...args) {
		if(logging) {
			for (int i = 0; i < args.length; i++) {
				System.err.print(args[i] + " ");
			}
			System.err.print("\n");
		}
	}
}
