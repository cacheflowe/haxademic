package com.haxademic.core.hardware.mouse;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import com.jogamp.newt.opengl.GLWindow;

import processing.core.PApplet;

public class MouseUtil {

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
	
	public static void movePointerTo(int x, int y) {
		mouseRobot().mouseMove(x, y);
	}
	
	public static Point systemMouseLocation() {
		return MouseInfo.getPointerInfo().getLocation();
	}
	
	// alternate method
	public static void setPointerLocation(PApplet p, int x, int y) {
		GLWindow window = (GLWindow) p.getSurface().getNative();
		window.warpPointer(x, y);
	}

}
