package com.haxademic.sketch.render;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;

public class CirclePacking
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	float _frames = 300;
	
	PGraphics _logoOffscreen;
	ArrayList<GrowParticle> _particles;
	float _maxRadius = 0;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "800" );

		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "1" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		PImage img = DemoAssets.squareTexture();
		
		// build off-screen logo image for processing
		_logoOffscreen = p.createGraphics(p.width, p.height);
		_logoOffscreen.beginDraw();
		DrawUtil.setDrawCorner(_logoOffscreen);
		_logoOffscreen.image(img, 0, 0, p.width, p.height);
		_logoOffscreen.endDraw();
		
		_maxRadius = p.width * 0.45f;
		
		// build particles
		_particles = new ArrayList<GrowParticle>();
	}

	public void drawApp() {
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		p.background(255);
		p.strokeWeight(0.75f);
				
		// draw original image
//		DrawUtil.setDrawCorner(p);
//		DrawUtil.setPImageAlpha(p, 0.3f);
//		p.image(_logoOffscreen, 0, 0);
//		DrawUtil.setPImageAlpha(p, 1);
		
		// add particles
		if(_particles.size() < 10000) { //  && p.frameCount <= 250
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
			_particles.add(new GrowParticle());
		}
//		P.println(p.frameCount);
		
		// draw particles
		for (int i = 0; i < _particles.size(); i++) {
			_particles.get(i).update();
		}
		
		// clean up small particles
		for (int i = _particles.size() - 1; i > 0; i--) {
			if(_particles.get(i).active() == false && _particles.get(i).size() <= 0) {
				_particles.remove(i);
			}
		}
	}
	
	public class GrowParticle{
		
		float _x;
		float _y;
		float _size;
		boolean _growing;
		boolean _active;
		int _color;
		int _colorStroke;
		
		public GrowParticle() {
			_size = 0;
			_active = true;
			_growing = true;
			_x = p.random(0,p.width);
			_y = p.random(0,p.height);
			int attempts = 0;
			// find an x/y in the circle, with a non-empty color, and not too close to another circle
			while(ImageUtil.getPixelColor(_logoOffscreen, (int)_x, (int)_y) == ImageUtil.EMPTY_INT || isCloseToAnother() == true || MathUtil.getDistance(_x, _y, p.width/2, p.height/2) > _maxRadius) {
				_x = p.random(0,p.width);
				_y = p.random(0,p.height);
				attempts++;
				if(attempts > 2000) {
					_active = false;
					_growing = false;
					break;
				}
			}
			_color = ImageUtil.getPixelColor(_logoOffscreen, (int)_x, (int)_y);
			_colorStroke = p.lerpColor(_color, p.color(0), 0.7f);
		}
		
		public float x() { return _x; }
		public float y() { return _y; }
		public float size() { return _size; }
		public float radius() { return _size/2f; }
		public boolean active() { return _active; }
		public boolean growing() { return _growing; }
		
		public void update() {
			// check for collisions
			if(_growing == true) {
				for (int i = 0; i < _particles.size(); i++) {
					if(_particles.get(i) != this && _particles.get(i).isTouching(this) == true) {
						_growing = false;
					}
				}
				// check out of bounds
				for(float i=0; i < P.TWO_PI; i += P.TWO_PI/36f) {
					float checkX = _x + P.sin(i) * radius();
					float checkY = _y + P.cos(i) * radius();
					if(MathUtil.getDistance(checkX, checkY, p.width/2, p.height/2) > _maxRadius) {
						_growing = false;
					}
				}
			}

//			if(p.frameCount > 250) {
//				_active = false;
//				_growing = false;
//			}
			if(radius() > 10) _growing = false;
			if(_growing == false && radius() < 2) _active = false;
			if(_growing == true) _size += 1;
			if(_growing == false && _active == false && _size > 0) _size -= 1;
			
			
			DrawUtil.setDrawCenter(p);
			p.pushMatrix();
			p.translate(_x, _y);
			p.fill(_color);
			p.stroke(_colorStroke);
			p.ellipse(0, 0, _size, _size);
			p.popMatrix();
		}
		
		public boolean isTouching(GrowParticle otherParticle) {
			if(MathUtil.getDistance(_x, _y, otherParticle.x(), otherParticle.y()) < (radius() + otherParticle.radius())) 
				return true;
			else
				return false;
		}
		
		public boolean isCloseToAnother() {
			for (int i = 0; i < _particles.size(); i++) {
				if(_particles.get(i) != this && _particles.get(i).isClose(this) == true) {
					return true;
				}
			}
			return false;
		}
		
		public boolean isClose(GrowParticle otherParticle) {
			if(MathUtil.getDistance(_x, _y, otherParticle.x(), otherParticle.y()) - 2 < (radius() + otherParticle.radius())) 
				return true;
			else
				return false;
		}
	}
}



