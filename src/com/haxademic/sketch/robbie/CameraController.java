package com.haxademic.sketch.robbie;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.shared.InputTrigger;

import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.MouseEvent;

public class CameraController {
	
	protected PAppletHax p;
	protected PGraphics pg;

	protected InputTrigger key0 = (new InputTrigger()).addKeyCodes(new char[]{'0'});
	protected InputTrigger keyW = (new InputTrigger()).addKeyCodes(new char[]{'W'});
	protected InputTrigger keyA = (new InputTrigger()).addKeyCodes(new char[]{'A'});
	protected InputTrigger keyS = (new InputTrigger()).addKeyCodes(new char[]{'S'});
	protected InputTrigger keyD = (new InputTrigger()).addKeyCodes(new char[]{'D'});
	protected int cameraTranslateSpeed = 50;
	protected int cameraZoomSpeed = 100;
	protected int xOff = 0;
	protected int yOff = 0;
	protected int zOff = 0;
	
	protected boolean mouseDown = false;
	protected int mouseX = 0;
	protected int mouseY = 0;
	protected PVector mouseStart = new PVector(0, 0);
	protected PVector mouseUpdate = new PVector(0, 0);
	protected PVector mouseCurrent = new PVector(0, 0);
	protected PVector cameraRotation = new PVector(0, 0);
	
	public CameraController(PGraphics _pg) {
//		p = P.p;
		pg = _pg;
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
	}
	
	public void draw() {
		// Reset camera
		if(key0.triggered()) {
			xOff = 0;
			yOff = 0;
			zOff = 0;
			mouseCurrent = new PVector(0, 0);
			cameraRotation = new PVector(0, 0);
		}
		// Move camera
		if(keyW.on()) {
			yOff -= cameraTranslateSpeed;
		}
		if(keyA.on()) {
			xOff -= cameraTranslateSpeed;
		}
		if(keyS.on()) {
			yOff += cameraTranslateSpeed;
		}
		if(keyD.on()) {
			xOff += cameraTranslateSpeed;
		}
		
		// Camera rotation
		pg.translate(pg.width/2, pg.height/2);
		if (mouseDown) {
			mouseCurrent = new PVector(mouseX - mouseStart.x + cameraRotation.x, mouseStart.y - mouseY + cameraRotation.y);
			pg.rotateX(P.map(mouseCurrent.y, 0, pg.width, 0, P.TWO_PI));
			pg.rotateY(P.map(mouseCurrent.x, 0, pg.height, 0, P.TWO_PI));
	    } else if (!mouseDown){
	    	pg.rotateX(P.map(cameraRotation.y, 0, pg.width, 0, P.TWO_PI));
	    	pg.rotateY(P.map(cameraRotation.x, 0, pg.height, 0, P.TWO_PI));
	    }
		
		// Final Camera translation
		pg.translate(-pg.width/2 + xOff, -pg.height/2 + yOff, zOff);
	}
	
	/////////////////////////////////////////
	// Mouse listener
	/////////////////////////////////////////
	
	public void mouseEvent(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
		
		switch (event.getAction()) {
			case MouseEvent.PRESS:
				mouseDown = true;
				mouseStart = new PVector(mouseX, mouseY);
				break;
			case MouseEvent.RELEASE:
				mouseDown = false;
				cameraRotation = new PVector(mouseCurrent.x, mouseCurrent.y);
				break;
			case MouseEvent.WHEEL:
				if (event.getCount() == 1) zOff -= cameraZoomSpeed;
				else zOff += cameraZoomSpeed;
				break;
		}
	}
	
}
