package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;

public class SquiggleLine 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected Squiggle _squiggle;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "30" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}


	public void setup() {
		super.setup();	
		_squiggle = new Squiggle(5, 50f, 20, 400, 5, p.color(0,255,0) );
	}

	public void drawApp() {
		background(0);
		_squiggle.update();
	}

	public class Squiggle {
		
		protected PGraphics pg;
		public float _periodY = 170;
		public float _weight = 10;
		public float _amplitude = 30;
		public float _speed = 8;
		public int _color;
		
		protected float _autoInc;
		protected EasingFloat _easingInc;
		
		protected int _mode;
		public static final int MODE_AUTO = 0;
		public static final int MODE_EASE = 1;

		public Squiggle( int weight, float periodY, int amplitude, int height, float speed, int color ) {
			_periodY = periodY;
			_weight = weight;
			_amplitude = amplitude;
			_speed = speed;
			_color = color;
			pg = p.createGraphics( amplitude * 2 + weight, height, P.P3D );
			pg.smooth(OpenGLUtil.SMOOTH_HIGH);
			
			_easingInc = new EasingFloat( 0, 6f );
			_mode = MODE_AUTO;
		}
		
		public void update() {
		
	//		p.fill(255);
	//		p.noStroke();
	//		p.rect(0, 0, p.width/2f, p.height);
			
			if( _mode == MODE_AUTO ) {
				_autoInc = p.frameCount / _speed;
			}
	
			float osc = P.sin( _autoInc ) * _amplitude;
			float oscOff = P.sin(p.frameCount/_speed + P.PI) * _amplitude;
			float periodYHalf = _periodY / 2f;
			float periodYQuarter = _periodY / 4f;
			
			float startX = p.width / 2f;
			float startY = -_periodY; 
			
	//		startY -= periodY * p.abs(P.sin(p.frameCount/speed) / P.PI);
			
	//		if( P.sin(p.frameCount/speed) < 0 ) {
	//			startY += periodY * p.abs(P.sin(p.frameCount/speed) / P.PI);
	//		} else {
	//			startY -= periodY * p.abs(P.sin(p.frameCount/speed) / P.PI);
	//		}
			
			
			p.strokeCap(P.ROUND);
			p.strokeWeight(_weight);
			p.stroke(255);
			p.noFill();
			
			p.beginShape();
			for(int i=0; i < 15; i++) {
				if( i == 0 ) {
					p.vertex(startX + osc, startY + periodYHalf);
				} else {
					p.quadraticVertex(
							startX + osc, 
							startY + (_periodY * i) + periodYQuarter, 
							startX, 
							startY + (_periodY * i) + periodYHalf
					);
				}
				p.quadraticVertex(
						startX + oscOff, 
						startY + (_periodY * i) + periodYQuarter * 3f, 
						startX, 
						startY + (_periodY * i) + _periodY
				);
			}
			p.endShape();
		}
	}
}
