package com.haxademic.render.ello;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PShape;

public class GifRenderEllo021ElloBurst
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PShape _logo;
	float _frames = 165;
	protected PGraphics _pg;
	protected MotionBlurPGraphics _pgMotionBlur;
	protected ArrayList<ElloBurst> _bursts;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "640" );
		Config.setProperty( AppSettings.HEIGHT, "640" );


		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}
	
	public void firstFrame() {

		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_bursts = new ArrayList<ElloBurst>();
		buildCanvas();
	}

	protected void buildCanvas() {
		_pg = p.createGraphics( p.width, p.height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pgMotionBlur = new MotionBlurPGraphics(3);
	}
	
	protected void drawGraphics( PGraphics pg ) {
		pg.beginDraw();
		pg.clear();
		PG.setDrawCorner(_pg);
		int numBursts = _bursts.size();
		for (int i = 0; i < numBursts; i++) {
			_bursts.get(i).update();
		}
		pg.endDraw();
	}

	public void drawApp() {
		if(p.frameCount == 2) _bursts.add(new ElloBurst(0, p.width/3f, p.width/2, p.height/2, p.width/2, p.height/2));
		p.background(255);
		drawGraphics(_pg);
		_pgMotionBlur.updateToCanvas(_pg, p.g, 1f);
	}

	
	public class ElloBurst {
		public EasingFloat _size;
		public EasingFloat _x;
		public EasingFloat _y;
		public float _radians;
		public float _children = 6;
		public float _childrenScale = 0.5f;
		public float _easeFactor = 4f;
		public boolean _hasStopped = false;
		public boolean _hasChildren = false;
		
		public ElloBurst(float size, float sizeDest, float x, float y, float xDest, float yDest) {
			_x = new EasingFloat(x, _easeFactor);
			_x.setTarget(xDest);
			
			_y = new EasingFloat(y, _easeFactor);
			_y.setTarget(yDest);
			
			_size = new EasingFloat(size, _easeFactor);
			_size.setTarget(sizeDest);
		}
		
		public void createChildren() {
			float childRads = P.TWO_PI / _children;
			for (float i = 0; i < _children; i++) {
				_bursts.add(
					new ElloBurst(
						_size.value(), 
						_size.value() * _childrenScale, 
						_x.value(), 
						_y.value(), 
						_x.value() + P.sin(childRads * i) * _size.value() * 0.75f, 
						_y.value() + P.cos(childRads * i) * _size.value() * 0.75f
					)
				);

			}
		}
		
		public void update() {
			_size.update();
			_x.update();
			_y.update();
			
			if(_hasStopped == false || (_hasStopped == true && _hasChildren == false)) {
				_pg.pushMatrix();
				_pg.translate(_x.value(), _y.value());
				_pg.rotate(_radians); 
				_pg.shape(_logo, 0, 0, _size.value(), _size.value());
				_pg.popMatrix();
			}
			
			if(P.abs(_size.value() - _size.target()) < 0.01f) {
				if(_hasStopped == false) {
					if(_size.value() > 10f) {
						createChildren();
						_hasChildren = true;
					} else {
						_size.setTarget(0);
					}
					_hasStopped = true;
				}
			}
		}
	}
}



