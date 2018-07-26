package com.haxademic.app.haxvisual.viz.elements;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.hardware.kinect.IKinectWrapper;

import processing.core.PApplet;
import processing.core.PConstants;
import toxi.color.TColor;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class KinectMeshOld
extends ElementBase 
implements IVizElement {
	
	// kinect setup
	protected IKinectWrapper _kinectInterface;
	protected int _kinectWidth = 640;
	protected int _kinectHeight = 480;
	protected int _drawScale = 10000;
	protected float _thresholdHigh = 1.5f;
	protected int[] _depthArray = null;
	
	protected final int SKIP_MAPPED = 9;
	protected final int SKIP_DRAW = 10;
	protected int _pixelsSkip = 9;

	// used in grid loop for raw depth
	protected Vec3D _curPixelDepth;
	protected Vec3D _rightPixelDepth;
	protected Vec3D _rightDownPixelDepth;
	protected Vec3D _downPixelDepth;
	protected int _offsetC;
	protected int _offsetR;
	protected int _offsetD;
	protected int _offsetRD;

	protected float _uvX = 0; 
	protected float _uvY = 0;
	protected float _uvNextX = 0;
	protected float _uvNextY = 0;
	protected float UVXOffset = -0.04f;
	protected float UVYOffset = 0.06f;
	
	// mesh and draw props
	protected WETriangleMesh _mesh;
	protected boolean _isMirrored = false;
	protected boolean _mapsCamera = false;
	protected boolean _isWireframe = false;
	protected boolean _isPoints = false;
	
	protected TColor _baseColor;
	protected TColor _fillColor;
	protected TColor _strokeColor;

	public KinectMeshOld( PApplet p, ToxiclibsSupport toxi, IKinectWrapper kinectWrapper ) {
		super( p, toxi );
		_kinectInterface = kinectWrapper;
		init();
	}

	public void init() {
		// set some defaults
		_drawScale = 10000;
	}
	
	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_fillColor = _baseColor.copy().lighten( 15 );
		_strokeColor = _baseColor.copy().lighten( 30 );
	}

	public void update() {
		p.pushMatrix();
		
		p.noTint();
		// Run the tracking analysis
		_kinectInterface.update();
		createKinectMesh( false );
		p.translate(0, 0, _drawScale * 0.6f);
		_mesh.scale( _drawScale );
		if( _mapsCamera ) {
			p.noStroke();
			p.fill( 1, 1 );
			toxi.texturedMesh(_mesh, _kinectInterface.getRgbImage(), false);
		} else {
			if( _isPoints == true ) {
//				DrawMesh.drawPointsWithAudio( p, ThreeDeeUtil.GetWETriangleMeshFromTriangleMesh( _mesh ), _audioData, 20, 40, _fillColor, _strokeColor, 0.2f );				
			} else {
				if( _isWireframe == true ) p.strokeWeight( 3 );
//				DrawMesh.drawMeshWithAudio( p, ThreeDeeUtil.GetWETriangleMeshFromTriangleMesh( _mesh ), _audioData, _isWireframe, _fillColor, _strokeColor, 0.2f );				
			}
		}
		
		p.popMatrix();
		_mesh.clear();
	}
	
	protected void createKinectMesh( boolean mapsCamera ) {
		p.rectMode(PConstants.CENTER);

		_mesh = new WETriangleMesh();
		//_depthArray = _kinectInterface.getDepthData();
		
		// loop through point grid and skip over pixels on an interval
		for (int x = 0; x < _kinectWidth; x+=_pixelsSkip) {
			for (int y = 0; y < _kinectHeight; y+=_pixelsSkip) {
				if(y > 120 || y < 360) { // only use the middle portion of the kinect mesh
					int nextX = x + _pixelsSkip;
					int nextY = y + _pixelsSkip;
	
					// stay 1 row/col in from edge, and skip cells for lower resolution
					if (nextX < _kinectWidth && nextY < _kinectHeight && x >= 1 && y >= 1
							&& (x % _pixelsSkip == 0 && y % _pixelsSkip == 0)) {
						
						// get raw depth and plot UV coords
						if( _isMirrored ) {
							_offsetC = _kinectWidth - x - 1 + y * _kinectWidth;
							_offsetR = _kinectWidth - nextX - 1 + y * _kinectWidth;
							_offsetD = _kinectWidth - x - 1 + nextY * _kinectWidth;
							_offsetRD = _kinectWidth - nextX - 1 + nextY * _kinectWidth;
							if( _mapsCamera ) {
								_uvX = ((_kinectWidth - x - 1)/(float)_kinectWidth) + UVXOffset;
								_uvY = (y/(float)_kinectHeight) + UVYOffset;
								_uvNextX = ((_kinectWidth - nextX - 1)/(float)_kinectWidth) + UVXOffset;
								_uvNextY = (nextY/(float)_kinectHeight) + UVYOffset;
							}
						} else {
							_offsetC = x + y * _kinectWidth;
							_offsetR = nextX + y * _kinectWidth;
							_offsetD = x + nextY * _kinectWidth;
							_offsetRD = nextX + nextY * _kinectWidth;
							if( _mapsCamera ) {
								_uvX = (x/(float)_kinectWidth) + UVXOffset;
								_uvY = (y/(float)_kinectHeight) + UVYOffset;
								_uvNextX = (nextX/(float)_kinectWidth) + UVXOffset;
								_uvNextY = (nextY/(float)_kinectHeight) + UVYOffset;
							}
						}
						
						// get depth for neighbor cells
						_rightPixelDepth = new Vec3D( x, y, _kinectInterface.getMillimetersDepthForKinectPixel(nextX, y) );
						_curPixelDepth = new Vec3D( x, y, _kinectInterface.getMillimetersDepthForKinectPixel(x, y) );
						_rightDownPixelDepth = new Vec3D( x, y, _kinectInterface.getMillimetersDepthForKinectPixel(nextX, nextY) );
						_downPixelDepth = new Vec3D( x, y, _kinectInterface.getMillimetersDepthForKinectPixel(x, nextY) );
						
						// draw if within threshold
						if (_curPixelDepth.z > -_thresholdHigh && _rightPixelDepth.z > -_thresholdHigh && _curPixelDepth.z > -_thresholdHigh && _downPixelDepth.z > -_thresholdHigh) {
	
							// if z depth delta is over a threshold, don't draw between near & far objects
							if (Math.abs(_curPixelDepth.z - _rightPixelDepth.z) > 0.2 || Math.abs(_curPixelDepth.z - _rightDownPixelDepth.z) > 0.2 || Math.abs(_curPixelDepth.z - _downPixelDepth.z) > 0.2) {
							} else {
								if( _mapsCamera ) {
									_mesh.addFace( _curPixelDepth, _rightDownPixelDepth, _downPixelDepth, new Vec2D(_uvX,_uvY), new Vec2D(_uvNextX,_uvNextY), new Vec2D(_uvX,_uvNextY) );
									_mesh.addFace( _curPixelDepth, _rightPixelDepth, _rightDownPixelDepth, new Vec2D(_uvX,_uvY), new Vec2D(_uvNextX,_uvY), new Vec2D(_uvNextX,_uvNextY) );
								} else {
									_mesh.addFace( _curPixelDepth, _rightDownPixelDepth, _downPixelDepth );
									_mesh.addFace( _curPixelDepth, _rightPixelDepth, _rightDownPixelDepth );
								}
							}
	
						}
					}
				}
			}
		}
	}
	
	public void reset() {
		_mapsCamera = false;//( p.random(0f,2f) >= 1 ) ? false : true;
		_pixelsSkip = ( _mapsCamera ) ? SKIP_MAPPED : SKIP_DRAW;
		updateCamera();
		updateLineMode();
	}
	
	public void updateLineMode() {
		int linesMode = p.round( p.random( 0, 2 ) );
		if( linesMode == 0 ) {
			_isWireframe = true;
			_isPoints = false;
		} else if( linesMode == 1 ) {
			_isWireframe = false;
			_isPoints = false;
		} else if( linesMode == 2 ) {
			_isWireframe = false;
			_isPoints = true;
		}
	}
	
	public void updateCamera() {
		_isMirrored = ( p.random(0f,2f) >= 1 ) ? false : true;
	}


	public void dispose() {
	}

}
