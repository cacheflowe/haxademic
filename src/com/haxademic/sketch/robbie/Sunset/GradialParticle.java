package com.haxademic.sketch.robbie.Sunset;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.MouseEvent;

public class GradialParticle {
	
	protected PAppletHax p;
	protected PGraphics pg;
	
	protected int index;
	
	protected PVector posInit;
	protected PVector pos;
	protected int randRadius;
	protected PVector randRadiusMinMax;
	protected float randRadian;

	protected PVector speedMinMax = new PVector(50, 100);
	
	protected int brightness;
	protected int brightnessUpdate;
	protected PVector brightnessMinMax = new PVector(10, 255);
	
	protected boolean available = true;
	
	protected float size = 50f;
	protected float sizeMin = 50f;
	protected float sizeMax = 180f;

	protected float lifespan = 30;
//	protected float lifespanUpdate = lifespan;
	
	protected float lifespanMin = 30;
	protected float lifespanMax = 100;
	protected LinearFloat sizeProgress = new LinearFloat(0, 1f/lifespan);
	
	protected int mouseX = 0;
	protected int mouseY = 0;
	protected boolean mouseOver = false;
	
	protected float easing = 0.025f;
	protected LinearFloat mouseProgress = new LinearFloat(0, 1f/lifespan);
	
	public GradialParticle(PGraphics _pg, int _index) {
		p = P.p;
		pg = _pg;
		index = _index;
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
		respawn();
	}
	
	public void respawn() {
		available = false;

		brightness  = (int)p.random(brightnessMinMax.x, brightnessMinMax.y);
		brightnessUpdate = brightness;

		sizeProgress.setInc(1f/MathUtil.randRange(lifespanMin, lifespanMax));
		sizeProgress.setCurrent(0);
		sizeProgress.setTarget(1);
		size = MathUtil.randRangeDecimal(sizeMin, sizeMax);
		
		randRadiusMinMax  = new PVector(0, pg.width/2 - size/2);
		randRadius = (int)p.random(randRadiusMinMax.x, randRadiusMinMax.y);
		randRadian = p.random(0, P.TWO_PI);
		posInit = new PVector(P.cos(randRadian) * randRadius, P.sin(randRadian) * randRadius);
		pos = posInit;
//		pos = new PVector(p.random(0, p.width), p.random(0, p.height));
		mouseProgress.setInc(1f/lifespanMax);
		mouseProgress.setCurrent(0);
		mouseProgress.setTarget(1);
		
		if (mouseOver) {
			sizeProgress.setInc(1f/MathUtil.randRange(lifespanMin/1.5f, lifespanMax/1.5f));
		} else {
			sizeProgress.setInc(1f/MathUtil.randRange(lifespanMin, lifespanMax));
		}
	}
	
	public void display() {
		if (index == 0) {
//			pg.fill(255, 0, 0);
//			pg.circle(mouseX, mouseY, 100);
//			P.out(mouseX, mouseY);
		}
		if(available()) respawn();		
		
		// update pos
		mouseProgress.update();
		if (mouseOver) {
			float curMouse = Penner.easeOutQuad(mouseProgress.value(), 0, 1, 1);
//			if(mouseProgress.value() == 1) mouseProgress.setTarget(0);
			
			float targetX = mouseX - pg.width/2;
			float dx = targetX - pos.x;
			pos.x += dx * easing * curMouse;
			
			float targetY = mouseY - pg.height/2;
			float dy = targetY - pos.y;
			pos.y += dy * easing * curMouse;
			

//			sizeProgress.setInc(1f/lifespanMin/2);
			brightnessUpdate = (int)P.map((int)P.constrain(brightness + 255 * curMouse, 0, 255), 0, 255, 0, 150 + (105*curMouse));

		} else {
			mouseProgress.setTarget(0);
			float curMouse = Penner.easeOutQuad(mouseProgress.value(), 0, 1, 1);
			
			float targetX = posInit.x - pg.width/2;
			float dx = targetX - pos.x;
			pos.x += dx * (easing * 0.2) * curMouse;
			
			float targetY = posInit.y - pg.height/2;
			float dy = targetY - pos.y;
			pos.y += dy * (easing * 0.2) * curMouse;
			
//			sizeProgress.setInc(1f/MathUtil.randRange(lifespanMin, lifespanMax));
			brightnessUpdate = (int)P.map((int)P.constrain(brightness + 255 * curMouse, 0, 255), 0, 255, 0, 150 + (105*curMouse));
			
//			float targetX = posInit.x - pg.width/2;
//			float dx = targetX - pos.x;
//			pos.x += dx * easing;
//			
//			float targetY = posInit.y - pg.height/2;
//			float dy = targetY - pos.y;
//			pos.y += dy * easing;
		}
		
//		if (index == 0) P.out(brightnessUpdate);
		
		// update size
		sizeProgress.update();
		float curSize = size * Penner.easeOutQuad(sizeProgress.value(), 0, 1, 1);
		if(sizeProgress.value() == 1) sizeProgress.setTarget(0);
				
		pg.beginDraw();
		pg.pushStyle();
		pg.pushMatrix();
		
		pg.blendMode(PBlendModes.SCREEN);
		pg.translate(pg.width/2 + pos.x, pg.height/2 + pos.y);
		Gradients.radial(pg, curSize, curSize, pg.color(brightnessUpdate), pg.color(0), 100);
		pg.popMatrix();
		pg.popStyle();
		pg.endDraw();
	}
	
	public boolean available() {
		boolean finished = (sizeProgress.value() == 0 && sizeProgress.target() == 0);
		return finished;
	}
	
	/////////////////////////////////////////
	// Mouse listener
	/////////////////////////////////////////
	
	public void mouseEvent(MouseEvent event) {
			mouseX = event.getX();
			mouseY = event.getY();
			
			switch (event.getAction()) {
				case MouseEvent.ENTER:
					mouseOver = true;
					break;
				case MouseEvent.MOVE:
					break;
				case MouseEvent.EXIT:
					mouseOver = false;
					break;
				case MouseEvent.PRESS:
					break;
				case MouseEvent.RELEASE:
					break;
			}
	}
}
