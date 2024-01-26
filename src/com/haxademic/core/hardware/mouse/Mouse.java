package com.haxademic.core.hardware.mouse;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.math.easing.EasingFloat;
import com.jogamp.newt.opengl.GLWindow;

import processing.core.PApplet;

public class Mouse {
	
	public static int x = 0;
	public static int y = 0;
	public static int xLast = 0;
	public static int yLast = 0;
	public static float xNorm = 0;
	public static float yNorm = 0;
	public static float xSpeed = 0;
	public static float ySpeed = 0;
	public static float xEased = 0;
	public static float yEased = 0;
	public static float xEasedNorm = 0;
	public static float yEasedNorm = 0;
	protected EasingFloat xEase = new EasingFloat(0, 0.25f);
	protected EasingFloat yEase = new EasingFloat(0, 0.25f);
	
	public int lastMouseTime = 0;
	public static boolean mouseShowing = true;

	
	// Singleton instance
	
	public static Mouse instance;
	
	public static Mouse instance() {
		if(instance != null) return instance;
		instance = new Mouse();
		return instance;
	}
	
	// constructor
	
	public Mouse() {
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	public void pre() {
		updateMouseProps();
		autoHideMouse();
	}
	
	// draw() update
	
	protected void updateMouseProps() {
		int mouseX = P.p.mouseX;
		int mouseY = P.p.mouseY;
		int pmouseX = P.p.pmouseX;
		int pmouseY = P.p.pmouseY;
		x = mouseX;
		y = mouseY;
		xNorm = (float) mouseX / (float) P.p.width;
		yNorm = (float) mouseY / (float) P.p.height;
		xLast = pmouseX;
		yLast = pmouseY;
		xSpeed = x - xLast;
		ySpeed = y - yLast;
		xEase.update(true);
		xEase.setTarget(x);
		xEased = xEase.value(); 
		xEasedNorm = xEase.value() / (float) P.p.width;
		yEase.update(true);
		yEase.setTarget(y);
		yEased = yEase.value(); 
		yEasedNorm = yEase.value() / (float) P.p.height;
	}
	
	protected void autoHideMouse() {
		// show mouse if it moved
		if(x != xLast || y != yLast) {
			lastMouseTime = P.p.millis();
			if(mouseShowing == false) {
				mouseShowing = true;
				showMouse();
			}
		}
		// hide mouse
		if(mouseShowing == true) {
			if(P.p.millis() > lastMouseTime + 5000) {
				hideMouse();
				mouseShowing = false;
			}
		}
	}

	public static boolean isShowing() {
		return mouseShowing;
	}
	
	///////////////////////////////////
	// Static Util methods
	///////////////////////////////////
	
	public static void showMouse() {
		P.p.cursor();
	}

	public static void hideMouse() {
		P.p.noCursor();
	}
	
	public static Robot mouseRobot;
	public static Robot mouseRobot() {
		try {
			if(mouseRobot == null) mouseRobot = new Robot();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mouseRobot;
	}
	
	public static void mouseClickAt(int x, int y) {
		mouseRobot().mouseMove(x, y);
		mouseRobot().mousePress(InputEvent.BUTTON1_DOWN_MASK);
		mouseRobot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public static void mouseClick() {
		mouseRobot().mousePress(InputEvent.BUTTON1_DOWN_MASK);
		mouseRobot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public static void movePointerTo(int x, int y) {
		mouseRobot().mouseMove(x, y);
	}
	
	public static Point systemMouseLocation() {
		PointerInfo info = MouseInfo.getPointerInfo();
		return (info != null) ? info.getLocation() : new Point(0, 0);
	}
	
	// alternate method
	public static void setPointerLocation(PApplet p, int x, int y) {
		GLWindow window = (GLWindow) p.getSurface().getNative();
		window.warpPointer(x, y);
	}
	
	// jframe cursor options below

	public static void setCursorWait(Component comp) {
		comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	public static void setInvisibleCursor(Component comp) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Point hotSpot = new Point(0,0);
		BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT); 
		Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");
		comp.setCursor(invisibleCursor);
	}
	
}
