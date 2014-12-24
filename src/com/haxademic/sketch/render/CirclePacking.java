package com.haxademic.sketch.render;

import java.util.ArrayList;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.AnimatedGifEncoder;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class CirclePacking
extends PAppletHax{
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	PImage _bread;
	float _frames = 300;
	
	PGraphics _logoOffscreen;
	ArrayList<GrowEllo> _particles;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "800" );
		_appConfig.setProperty( "height", "800" );

		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "1" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-inverse.svg");
		_bread = p.loadImage(FileUtil.getHaxademicDataPath()+"images/bread.png");
		
		PImage img = p.loadImage(FileUtil.getHaxademicDataPath() + "images/snowblinded-mtn-2.jpg");
		
		// build off-screen logo image for processing
		_logoOffscreen = p.createGraphics(p.width, p.height);
		_logoOffscreen.beginDraw();
		DrawUtil.setDrawCorner(_logoOffscreen);
		_logoOffscreen.image(img, 0, 0, p.width, p.height);
		_logoOffscreen.endDraw();
		
		// build particles
		_particles = new ArrayList<GrowEllo>();
	}

	public void drawApp() {
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		p.background(255);
		p.noStroke();
				
		// Ello logo
//		DrawUtil.setDrawCorner(p);
//		DrawUtil.setPImageAlpha(p, 0.3f);
//		p.image(_logoOffscreen, 0, 0);
//		DrawUtil.setPImageAlpha(p, 1);
		
		// add particles
		if(_particles.size() < 10000) { //  && p.frameCount <= 250
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
			_particles.add(new GrowEllo());
		}
//		P.println(p.frameCount);
		
		// draw particles
		p.strokeWeight(0.75f);
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
	
	public class GrowEllo {
		
		float _x;
		float _y;
		float _size;
		boolean _growing;
		boolean _active;
		int _color;
		int _colorStroke;
		
		public GrowEllo() {
			_size = 0;
			_active = true;
			_growing = true;
			_x = p.random(0,p.width);
			_y = p.random(0,p.height);
			int attempts = 0;
			while(ImageUtil.getPixelColor(_logoOffscreen, (int)_x, (int)_y) == ImageUtil.EMPTY_INT || isCloseToAnother() == true || MathUtil.getDistance(_x, _y, p.width/2, p.height/2) > p.width * 0.45f) {
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
				// check for collision with whitespace
				for(float i=0; i < P.TWO_PI; i += P.TWO_PI/36f) {
					float checkX = _x + P.sin(i) * radius();
					float checkY = _y + P.cos(i) * radius();
					if(ImageUtil.getPixelColor(_logoOffscreen, (int)checkX, (int)checkY) == ImageUtil.EMPTY_INT) {
						_growing = false;
					}
				}
			}

			if(_growing == false && radius() < 2) _active = false;
//			if(p.frameCount > 250) {
//				_active = false;
//				_growing = false;
//			}
			if(radius() > 10) _growing = false;
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
		
		public boolean isTouching(GrowEllo otherParticle) {
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
		
		public boolean isClose(GrowEllo otherParticle) {
			if(MathUtil.getDistance(_x, _y, otherParticle.x(), otherParticle.y()) - 2 < (radius() + otherParticle.radius())) 
				return true;
			else
				return false;
		}
	}
}



