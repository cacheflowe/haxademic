package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;

public class Demo_SquiggleLine 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected Squiggle _squiggle;

	protected void config() {
//		Config.setProperty(AppSettings.RENDERER, PRenderers.JAVA2D);
	}


	protected void firstFrame() {
		_squiggle = new Squiggle(5, 30f, 20, 100, 0.01f, p.color(0,255,0) );
	}

	protected void drawApp() {
		background(0);
		translate(0, 100);
		_squiggle.update();
	}

	public class Squiggle {
		
		public float _periodY = 170;
		public float _weight = 10;
		public float _amplitude = 30;
		public float _speed = 0.01f;
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
			
			_easingInc = new EasingFloat( 0, 6f );
			_mode = MODE_AUTO;
		}
		
		public void update() {
		    _periodY = 30;
	//		p.fill(255);
	//		p.noStroke();
	//		p.rect(0, 0, p.width/2f, p.height);
			
			if( _mode == MODE_AUTO ) {
				_autoInc = p.frameCount / _speed;
			}
	
			
	//		startY -= periodY * p.abs(P.sin(p.frameCount/speed) / P.PI);
			
	//		if( P.sin(p.frameCount/speed) < 0 ) {
	//			startY += periodY * p.abs(P.sin(p.frameCount/speed) / P.PI);
	//		} else {
	//			startY -= periodY * p.abs(P.sin(p.frameCount/speed) / P.PI);
	//		}
			
			_speed = 0.06f;
			
			p.strokeCap(P.ROUND);
			p.strokeWeight(_weight);
			p.stroke(_color);
			p.noFill();
			
			p.beginShape();
			for(int i=0; i < 15; i++) {

			    float osc = P.sin( p.frameCount * _speed + i/8f ) * _amplitude;
			    float oscOff = -P.sin( p.frameCount * _speed + i/8f ) * _amplitude;
			    float periodYHalf = _periodY / 2f;
			    float periodYQuarter = _periodY / 4f;
			    
			    float startX = p.width / 2f;
			    float startY = -_periodY; 
				
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
