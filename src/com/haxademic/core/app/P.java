package com.haxademic.core.app;

import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.data.store.AppStore;
import com.haxademic.core.data.store.AppStoreDistributed;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.ui.UI;

import processing.core.PApplet;
import processing.core.PImage;

public class P
extends PApplet {
	
	// static app object refs
	
	public static PAppletHax p;
	public static String renderer;
	public static AppStore store;	
	public static AppStoreDistributed storeDistributed;	
	
	public static void init(PAppletHax p) {
		P.p = p;
		P.store = AppStore.instance();
		Config.instance();
		renderer = Config.getString(AppSettings.RENDERER, P.P3D);
		if(P.renderer != PRenderers.PDF) DebugView.instance();
		UI.instance();
		Mouse.instance();
		KeyboardState.instance();
	}
	
	// helper methods
	
	public static boolean isOpenGL() {
		return P.renderer.equals(PRenderers.P2D) || P.renderer.equals(PRenderers.P3D);
	}
	
	public static boolean isHaxApp() {
		return P.p instanceof PAppletHax;
	}
	
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
