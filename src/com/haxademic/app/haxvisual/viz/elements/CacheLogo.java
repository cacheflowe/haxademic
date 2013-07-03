package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.ColorHax;
import com.haxademic.core.draw.shapes.CacheFloweLogo;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

public class CacheLogo
extends ElementBase 
implements IVizElement {
	
	protected boolean _isWireframe = false;
	protected float _fillAlpha = 1;
	protected float _scale = 1.5f;
	protected ColorHax _yellow = new ColorHax( 255/255f, 249/255f, 0, 1 );
	protected ColorHax _blue = new ColorHax( 0, 249/255f, 255/255f, 1 );
	protected ColorHax _red = new ColorHax( 249/255f, 150/255f, 150/255f, 1 );
	protected ColorHax _white = new ColorHax( 240/255f, 240/255f, 240/255f, 1 );
	protected ColorHax[] _ringColors;
	protected ColorHax _curColor;
	protected TColor _baseColor;
	protected TColor _fillColor;
	protected TColor _blackColor;
	protected float wallOffset = -30/255f;
	
	protected float curScale;
	protected float curRotX;
	protected float curRotY;
	protected float curRotZ;
	
	protected float targetScale;
	protected float targetRotX;
	protected float targetRotY;
	protected float targetRotZ;
	
	protected float _thickness = 0;
	protected float _targetThickness = 0;



	public CacheLogo( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		// set some defaults
		_curColor = _yellow;
		_blackColor = new TColor( TColor.BLACK );
		_blackColor.alpha = 0.2f;
		reset();
	}
	
	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_fillColor = _baseColor.copy();
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();

		p.translate( 0, 0, -300 );
		easeParameters();
		
		_targetThickness = ( _audioData.getFFT().spectrum[100] ) * 400;
		_thickness = MathUtil.easeTo( _thickness, _targetThickness, 4 );
		float logoAlpha = ( P.constrain( _audioData.getFFT().averages[2] * 1.7f, 0f, 1f ) );	//  * 255 + 127
		
		TColor fillColor = null;
		
		if( _isWireframe ) {
			p.stroke( _baseColor.toARGB() );
			fillColor = _blackColor;
		} else {
			p.noStroke();
			_fillColor.alpha = logoAlpha;
			fillColor = _fillColor;
		} 
		CacheFloweLogo.drawCacheFloweLogo( p, curScale, _thickness, fillColor.toARGB(), fillColor.toARGB() );	// _curColor.colorIntWithAlpha(logoAlpha, wallOffset)	//_curColor.colorIntWithAlpha(logoAlpha, 0)	
		
		p.popMatrix();
	}
	
	public void easeParameters() {
		curRotX = MathUtil.easeTo(curRotX, targetRotX, 5);
		curRotY = MathUtil.easeTo(curRotY, targetRotY, 5);
		curRotZ = MathUtil.easeTo(curRotZ, targetRotZ, 5);
		curScale = MathUtil.easeTo(curScale, targetScale, 5);
		
		p.rotateX( curRotX );
		p.rotateY( curRotY );
		p.rotateZ( curRotZ );
	}

	public void reset() {
		updateLineMode();
		updateCamera();
	}
	
	public void updateLineMode() {
		_isWireframe = ( p.random(0f,2f) >= 1 ) ? false : true;
	}
	
	public void updateCamera() {
		float quarterPi = (float) Math.PI / 4f;
		targetRotX = p.random(-quarterPi,quarterPi);
		targetRotY = p.random(-quarterPi,quarterPi);
		targetRotZ = p.random(-quarterPi,quarterPi);
		targetScale = _scale + p.random(0,1);
	}

	public void dispose() {
		_audioData = null;
	}


}
