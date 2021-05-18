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
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.Renderer;
import com.haxademic.core.system.ScreenSaverBlocker;
import com.haxademic.core.ui.UI;
import com.jogamp.newt.opengl.GLWindow;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PSurface;

public class P
extends PApplet {
	
	// static app object refs
	
	public static PAppletHax p;
	public static GLWindow window;
	public static String renderer;
	public static AppStore store;	
	public static AppStoreDistributed storeDistributed;	
	
	public static void init(PAppletHax p) {
		P.p = p;
		P.store = AppStore.instance();
		Config.instance();
	}
	
	public static void appInitialized() {
		// now that the app/window is initialized, we can act on P.p properties 
		// ... and anything that relied on PAppletHax.config() overrides per-app
		if(P.isOpenGL()) window = (GLWindow) P.p.getSurface().getNative();
		if(Config.getInt(AppSettings.LOOP_FRAMES, 0) != 0) FrameLoop.instance(Config.getInt(AppSettings.LOOP_FRAMES, 360), Config.getInt(AppSettings.LOOP_TICKS, 4));
		if(P.renderer != PRenderers.PDF) DebugView.instance();
		ScreenSaverBlocker.instance();
		UI.instance();
		Mouse.instance();
		KeyboardState.instance();
		Renderer.instance();
	}
	
	// helper methods
	
	public static boolean isOpenGL() {
		return P.renderer.equals(PRenderers.P2D) || P.renderer.equals(PRenderers.P3D);
	}
	
	public static boolean isHaxApp() {
		return P.p instanceof PAppletHax;
	}
	
	public static PSurface surface() {
		return P.p.getSurface();
	}
	
	// image loading 
	
	public static PImage getImage(String file) {
		return P.p.loadImage(FileUtil.getPath(file));
	}
	
	// logging
	
	public static boolean logging = true;
	public static void out(Object ...args) {
		if(logging) P.println(args);
	}
	public static void outInit(Object ...args) {
		if(logging) {
			P.print("##Hax## ");
			P.println(args);
		}
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
