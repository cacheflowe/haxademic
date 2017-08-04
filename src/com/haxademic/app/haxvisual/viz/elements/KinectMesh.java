package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.filters.PixelFilter;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.IKinectWrapper;
import com.haxademic.core.hardware.kinect.KinectSize;

public class KinectMesh
extends ElementBase 
implements IVizElement {
	
	// kinect setup
	public static final float PIXEL_SIZE = 7;
	public static final int KINECT_TOP = 0;
	public static final int KINECT_BOTTOM = 480;
	public static final int KINECT_CLOSE = 1000;
	public static final int KINECT_FAR = 2000;
	
	protected PixelFilter _pixelFilter;
	
	// mesh and draw props
	protected boolean _isMirrored = false;
	protected boolean _mapsCamera = false;
	protected boolean _isWireframe = false;
	protected boolean _isPoints = false;
	
	protected TColor _baseColor;
	protected TColor _fillColor;
	protected TColor _strokeColor;
	
	protected PAppletHax pHax;
	
	public KinectMesh( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData, IKinectWrapper kinectWrapper ) {
		super( p, toxi, audioData );
		pHax = (PAppletHax) p;
		_pixelFilter = new PixelFilter(KinectSize.WIDTH, KinectSize.WIDTH, (int)PIXEL_SIZE);
		init();
	}

	public void init() {
	}
	
	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_fillColor = _baseColor.copy().lighten( 15 );
		_strokeColor = _baseColor.copy().lighten( 30 );
	}

	public void update() {
		p.pushMatrix();
		
		p.noTint();

		// draw filtered web cam
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		p.image(_pixelFilter.updateWithPImage(pHax.kinectWrapper.getRgbImage()), 0, 0);


		// loop through kinect data within player's control range
		p.stroke(_fillColor.toARGB());
		float pixelDepth;
		for ( int x = 0; x < KinectSize.WIDTH; x += PIXEL_SIZE ) {
			for ( int y = KINECT_TOP; y < KINECT_BOTTOM; y += PIXEL_SIZE ) {
				pixelDepth = pHax.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
					p.pushMatrix();
					p.fill(((pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE)) * 255f);
					p.rect(x, y, PIXEL_SIZE, PIXEL_SIZE);
					p.popMatrix();
				}
			}
		}

		p.popMatrix();
	}
	
	public void reset() {
//		_mapsCamera = false;//( p.random(0f,2f) >= 1 ) ? false : true;
//		_pixelsSkip = ( _mapsCamera ) ? SKIP_MAPPED : SKIP_DRAW;
		updateCamera();
		updateLineMode();
	}
	
	public void updateLineMode() {
		int linesMode = P.round( p.random( 0, 2 ) );
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

	}


	public void dispose() {
		_audioData = null;
	}

}
