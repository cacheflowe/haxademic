package com.haxademic.core.hardware.mouse;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

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
