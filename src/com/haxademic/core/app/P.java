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
import com.haxademic.core.system.Console;
import com.haxademic.core.system.ScreenSaverBlocker;
import com.haxademic.core.ui.UI;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL4;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PSurface;
import processing.opengl.PGL;
import processing.opengl.PJOGL;

public class P
extends PApplet {
	
	// static app object refs
	
	public static PAppletHax p;
	public static GLWindow window;
	public static GL4 gl4;
	public static String renderer;
	public static AppStore store;	
	public static AppStoreDistributed storeDistributed;	
	
	public static void init(PAppletHax p) {
		P.p = p;
		P.store = AppStore.instance();
		Config.instance();
		Config.printArgs();
	}
	
	public static void appInitialized() {
		// now that the app/window is initialized, we can act on P.p properties 
		// ... and anything that relied on PAppletHax.config() overrides per-app
		if(P.isOpenGL()) window = (GLWindow) P.p.getSurface().getNative();
		if(P.isOpenGL()) gl4 = getGL4();
		if(Config.getInt(AppSettings.LOOP_FRAMES, 0) != 0) FrameLoop.instance(Config.getInt(AppSettings.LOOP_FRAMES, 360), Config.getInt(AppSettings.LOOP_TICKS, 4));
		if(P.renderer != PRenderers.PDF) DebugView.instance();
		ScreenSaverBlocker.instance();
		UI.instance();
		Mouse.instance();
		KeyboardState.instance();
		Renderer.instance();
	}
	
	// helper methods
	
	public static String appClassName() {
		return P.p.getClass().getCanonicalName();
	}
	
	public static boolean isOpenGL() {
		return P.renderer.equals(PRenderers.P2D) || P.renderer.equals(PRenderers.P3D);
	}
	
	public static GL4 getGL4() {
		PGL pgl = P.p.beginPGL();
		GL4 gl = ((PJOGL)pgl).gl.getGL4();
		P.p.endPGL();
		return gl;
	}
	
	public static boolean isHaxApp() {
		return P.p instanceof PAppletHax;
	}
	
	public static PSurface surface() {
		return P.p.getSurface();
	}
	
	// quick relative paths to project/data
	
	public static String path(String path) {
		return FileUtil.getPath(path);
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
	public static void warn(Object ...args) { P.outColor(Console.YELLOW, args); }
	public static void success(Object ...args) { P.outColor(Console.GREEN, args); }
	public static void fail(Object ...args) { P.outColor(Console.RED, args); }
	public static void outColor(String color, Object ...args) {
		if(!logging) return;
		P.print(color);
		for (int i = 0; i < args.length; i++) {
			if(i > 0) P.print(" ");
			P.print(args[i]);
		}
		P.println(Console.RESET);
	}
	public static void outInit(Object ...args) {
		Object[] tempArr = new Object[args.length + 1];
    System.arraycopy(args, 0, tempArr, 1, args.length);
    tempArr[0] = "##HAX##| ";
		P.success(tempArr);
	}
	public static void outInitLineBreak() {
		outInit("=================================");
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
