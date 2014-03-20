package com.haxademic.sketch.hardware.kinect_openni;

import java.util.ArrayList;

import processing.core.PGraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectWrapper;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;


@SuppressWarnings("serial")
public class KinectHumanJoysticks 
extends PAppletHax {
	
	protected KinectRegionGrid _kinectGrid;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "kinect_active", "true" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
	}
	
	public void setup() {
		super.setup();
		
		_kinectGrid = new KinectRegionGrid(2, 2, 500, 1000, 40, 0, 480);
	}

	public void drawApp() {		
		// reset drawing 
		DrawUtil.resetGlobalProps( p );
		p.shininess(1000f); 
		p.lights();
		p.background(0);
		
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		// p.image( p.kinectWrapper.getRgbImage(), 0, 0);
		
		_kinectGrid.updateDebug();
		
		// P.println( _kinectGrid.getRegion(0).controlX() + " , " + _kinectGrid.getRegion(0).controlZ() );
	}
	
	public void keyPressed() {
		super.keyPressed();
		if (p.key == ' ') {
			_kinectGrid.toggleDebugOverhead();
		}
	}
	
	public class KinectRegionGrid {
		
		protected int _kinectClose = 0;
		protected int _kinectFar = 0;
		protected int _kinectDepth = 0;
		
		public ArrayList<KinectRegion> kinectRegions;
		
		// debug drawing helpers
		protected EasingFloat _sceneRot;
		protected EasingFloat _sceneTranslateZ;
		protected EasingFloat _sceneTranslateZ2;
		protected boolean _overheadView = false;


		public KinectRegionGrid(int cols, int rows, int kinectClose, int kinectFar, int padding, int kinectTop, int kinectBottom) {
			_kinectClose = kinectClose;
			_kinectFar = kinectFar;
			_kinectDepth = _kinectFar - _kinectClose;
			
			// set up rectangles for position detection
			kinectRegions = new ArrayList<KinectRegion>();
			int colW = (KinectWrapper.KWIDTH - padding*(cols-1)) / cols;
			int kinectDepth = kinectFar - kinectClose;
			int rowH = (kinectDepth - padding*(rows-1)) / rows;

			for ( int x = 0; x < cols; x++ ) {
				for ( int y = 0; y < rows; y++ ) {
					KinectRegion region = new KinectRegion( 
							colW * x + padding * x, 
							colW * x + padding * x + colW, 
							kinectClose + y * rowH + padding * y,
							kinectClose + y * rowH + padding * y + rowH,
							kinectTop,
							kinectBottom,
							20,
							p.color( p.random(130,255), p.random(130,255), p.random(130,255) )
					);
					kinectRegions.add( region );
				}
			}
		}
		
		public KinectRegion getRegion( int index ) {
			return kinectRegions.get(index);
		}
		
		public void update() {
			updateRegions(false);
		}
		
		public void updateRegions( boolean isDebugging ) {
			for( int i=0; i < kinectRegions.size(); i++ ) {
				kinectRegions.get(i).detect(isDebugging);
			}
		}
		
		public void updateDebug() {
			// lazy-init debugging camera easing
			if( _sceneRot == null ) {
				_sceneRot = new EasingFloat(0, 6f);
				_sceneTranslateZ = new EasingFloat(0, 6f);
				_sceneTranslateZ2 = new EasingFloat(0, 6f);
				
				
			}
			
			// move scene towards front of kinect range
			p.pushMatrix();
			p.translate(0,0,_kinectClose);
			
			// rotate scene for debugging
			_sceneTranslateZ.update();
			_sceneRot.update();
			_sceneTranslateZ2.update();
			
			p.translate(0,0,_sceneTranslateZ.value());
			p.rotateX(_sceneRot.value());
			p.translate(0,0,_sceneTranslateZ2.value());
			
			// loop through kinect data within rectangles ----------
			updateRegions(true);
			
			// draw regions' rectangles ----------------------------
			p.pushMatrix();
			
			p.rotateX(-P.PI/2f);
			p.translate(0,0,460);
					
			for( int i=0; i < kinectRegions.size(); i++ ) {
				kinectRegions.get(i).drawRect();
			}
			p.popMatrix();
			p.popMatrix();
		}
		
		public void toggleDebugOverhead() {
			_overheadView = !_overheadView;
			
			if(_overheadView == true) {
				_sceneTranslateZ.setTarget(_kinectDepth * -2f);
				_sceneRot.setTarget(-P.PI/2f);
				_sceneTranslateZ2.setTarget(_kinectClose + _kinectDepth);
			} else {
				_sceneTranslateZ.setTarget(0);
				_sceneRot.setTarget(0);
				_sceneTranslateZ2.setTarget(0);
			}

		}
	}
	
	public class KinectRegion {
		
		protected int _left = 0;
		protected int _right = 0;
		protected int _near = 0;
		protected int _far = 0;
		protected int _top = 0;
		protected int _bottom = 0;
		protected int _resolution = 10;
		protected int _blockColor = -1;
		
		protected int _pixelCount = 0;
		protected float _controlX = 0.5f;
		protected float _controlZ = 0.5f;
		
		public KinectRegion( int left, int right, int near, int far, int top, int bottom, int resolution, int blockColor ) {
			_left = left;
			_right = right;
			_near = near;
			_far = far;
			_top = top;
			_bottom = bottom;
			_resolution = resolution;
			_blockColor = blockColor;
		}
		
		public int pixelCount() {
			return _pixelCount;
		}
		
		public float controlX() {
			return _controlX;
		}

		public float controlZ() {
			return _controlZ;
		}

		public void drawRect() {
			if( _blockColor == -1 ) return;
			p.stroke( _blockColor );
			p.fill( _blockColor, P.min(_pixelCount * 5, 255) );
			p.rect(_left, _near, _right - _left, _far - _near);
		}
		
		public void detect( boolean isDebugging ) {
			// find kinect readings in the region
			_pixelCount = 0;
			float controlXTotal = 0;
			float controlZTotal = 0;
			float pixelDepth = 0;
			for ( int x = _left; x < _right; x += _resolution ) {
				for ( int y = _top; y < _bottom; y += _resolution ) {
					pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
					if( pixelDepth != 0 && pixelDepth > _near && pixelDepth < _far ) {
						if( isDebugging == true ) {
							p.noStroke();
							p.fill( _blockColor, 200 );
							p.pushMatrix();
							p.translate(x, y, -pixelDepth);
							p.box(_resolution, _resolution, _resolution);
							p.popMatrix();
						}
						// add up for calculations
						_pixelCount++;
						controlXTotal += x;
						controlZTotal += pixelDepth;
					}
				}
			}
			
			// if we have enough blocks in a region, update the player's joystick position
			 if( _pixelCount > 20 ) {
				// compute averages
				if( controlXTotal > 0 && controlZTotal > 0 ) {
					float avgX = controlXTotal / _pixelCount;
					_controlX = MathUtil.getPercentWithinRange(_left, _right, avgX);
					float avgZ = controlZTotal / _pixelCount;
					_controlZ = MathUtil.getPercentWithinRange(_near, _far, avgZ);
	
					// show debug
					if( isDebugging == true ) {
						p.fill( 255 );
						p.pushMatrix();
						p.translate(avgX, 220, -avgZ);
						p.box(40, 480, 40);
						p.popMatrix();
					}
				}
			 }
		}
	}
		
}

