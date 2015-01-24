package com.haxademic.sketch.render.ello;

import java.util.ArrayList;

import processing.core.PGraphics;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.MotionBlurPGraphics;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class GifRenderEllo021ElloBurst
extends PAppletHax{
	
	PShape _logo;
	float _frames = 165;
	protected PGraphics _pg;
	protected MotionBlurPGraphics _pgMotionBlur;
	protected ArrayList<ElloBurst> _bursts;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "640" );


		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_bursts = new ArrayList<ElloBurst>();
		buildCanvas();
	}

	protected void buildCanvas() {
		_pg = p.createGraphics( p.width, p.height, P.OPENGL );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pgMotionBlur = new MotionBlurPGraphics(3);
	}
	
	protected void drawGraphics( PGraphics pg ) {
		pg.beginDraw();
		pg.clear();
		DrawUtil.setDrawCorner(_pg);
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



