package com.haxademic.demo.draw.image;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_CirclePacking
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float _frames = 300;
	
	protected PGraphics imageMap;
	protected ArrayList<GrowParticle> particles;
	protected float maxRadius = 0;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}
	
	protected void firstFrame() {
		PImage img = DemoAssets.squareTexture();
		
		// build off-screen logo image for processing
		imageMap = p.createGraphics(p.width, p.height);
		imageMap.beginDraw();
		PG.setDrawCorner(imageMap);
		imageMap.image(img, 0, 0, p.width, p.height);
		imageMap.endDraw();
		
		// if we want to keep our particles within a bounding circle
		maxRadius = p.width * 0.45f;
		
		// build particles
		particles = new ArrayList<GrowParticle>();
	}

	protected void drawApp() {
		p.background(255);
		p.strokeWeight(0.75f);
				
		// draw original image
//		PG.setDrawCorner(p);
//		PG.setPImageAlpha(p, 0.3f);
//		p.image(_logoOffscreen, 0, 0);
//		PG.setPImageAlpha(p, 1);
		
		// add particles
		if(particles.size() < 2000) { //  && p.frameCount <= 250
			for (int i = 0; i < 10; i++) {
				particles.add(new GrowParticle());
			}
		}
//		P.println(p.frameCount);
		
		// draw particles
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).update();
		}
		
		// clean up small particles
		for (int i = particles.size() - 1; i > 0; i--) {
			if(particles.get(i).active() == false && particles.get(i).size() <= 0) {
				particles.remove(i);
			}
		}
	}
	
	public class GrowParticle{
		
		protected float x;
		protected float y;
		protected float _size;
		protected boolean growing;
		protected boolean active;
		protected int color;
		protected int colorStroke;
		
		public GrowParticle() {
			_size = 0;
			active = true;
			growing = true;
			x = p.random(0,p.width);
			y = p.random(0,p.height);
			int attempts = 0;
			// find an x/y in the circle, with a non-empty color, and not too close to another circle
			boolean outsideCircle = MathUtil.getDistance(x, y, p.width/2, p.height/2) > maxRadius;
			boolean pixelIsAvailable = ImageUtil.getPixelColor(imageMap, (int)x, (int)y) != ImageUtil.EMPTY_INT;
			while(!pixelIsAvailable || isCloseToAnother() == true) { // || outsideCircle
				x = p.random(0,p.width);
				y = p.random(0,p.height);
				attempts++;
				if(attempts > 2000) {
					active = false;
					growing = false;
					break;
				}
			}
			color = ImageUtil.getPixelColor(imageMap, (int)x, (int)y);
			colorStroke = p.lerpColor(color, p.color(0), 0.7f);
		}
		
		public float x() { return x; }
		public float y() { return y; }
		public float size() { return _size; }
		public float radius() { return _size/2f; }
		public boolean active() { return active; }
		public boolean growing() { return growing; }
		
		public void update() {
			// check for collisions
			if(growing == true) {
				for (int i = 0; i < particles.size(); i++) {
					if(particles.get(i) != this && particles.get(i).isTouching(this) == true) {
						growing = false;
					}
				}
				// check out of bounds
				for(float i=0; i < P.TWO_PI; i += P.TWO_PI/36f) {
					float checkX = x + P.sin(i) * radius();
					float checkY = y + P.cos(i) * radius();
					if(MathUtil.getDistance(checkX, checkY, p.width/2, p.height/2) > maxRadius) {
						growing = false;
					}
				}
			}

//			if(p.frameCount > 250) {
//				_active = false;
//				_growing = false;
//			}
			if(radius() > 50) growing = false;
			if(growing == false && radius() < 10) active = false;
			if(growing == true) _size += 1;
			if(growing == false && active == false && _size > 0) _size -= 1;
			
			
			PG.setDrawCenter(p);
			p.pushMatrix();
			p.translate(x, y);
			p.fill(color);
			p.stroke(colorStroke);
			p.ellipse(0, 0, _size, _size);
			p.popMatrix();
		}
		
		public boolean isTouching(GrowParticle otherParticle) {
			if(MathUtil.getDistance(x, y, otherParticle.x(), otherParticle.y()) < (radius() + otherParticle.radius())) 
				return true;
			else
				return false;
		}
		
		public boolean isCloseToAnother() {
			for (int i = 0; i < particles.size(); i++) {
				if(particles.get(i) != this && particles.get(i).isClose(this) == true) {
					return true;
				}
			}
			return false;
		}
		
		public boolean isClose(GrowParticle otherParticle) {
			if(MathUtil.getDistance(x, y, otherParticle.x(), otherParticle.y()) - 2 < (radius() + otherParticle.radius())) 
				return true;
			else
				return false;
		}
	}
}
