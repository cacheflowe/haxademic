package com.haxademic.app.haxvisual.viz.elements;


import processing.core.PApplet;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.data.Point3D;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.util.DrawUtil;

public class RotatingRings
extends ElementBase 
implements IVizElement {
	
	protected float _width;
	protected float _height;

	protected int NUM_RINGS = 10;
	
	protected Point3D _rotation = new Point3D( 0, 0, 0 );
	protected Point3D _rotationTarget = new Point3D( 0, 0, 0 );

	protected boolean _isWireframe = false;
	protected TColor _baseColor;
	protected TColor _strokeColor;
	protected ColorGroup _curColors;

	public RotatingRings( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		// set some defaults
	}
	
	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_strokeColor = _baseColor.copy();
		_strokeColor.lighten( 10 );
		_curColors = colors;
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();
		
		// rotation for entire scene
		updateRotation();
		
		// disc properties	
		int discPrecision = 40;
		int discRadius = 14000;
		float circleSegment = (float) P.PI / (float) NUM_RINGS;
		
		// wireframe modes
		if( _isWireframe == true ) 
			p.noFill();
		else
			p.noStroke();
		p.strokeWeight( 5 );
		
		// draw rings
		for( int i = 0; i < NUM_RINGS; i++ ) {
			// get eq val for alpha
			float ringEQVal = _audioData.getFFT().spectrum[i + 5];
			float alphaMultiplier = 1.3f;

			// set colors
			_curColors.getColorFromIndex(i % 4).alpha = ringEQVal * alphaMultiplier;
			if( _isWireframe == false ) {
				_baseColor.alpha = ringEQVal * alphaMultiplier;
				p.fill( _curColors.getColorFromIndex(i % 4).toARGB() );
			}
			if( _isWireframe == true ) {
				_strokeColor.alpha = ringEQVal * alphaMultiplier;
				p.stroke( _curColors.getColorFromIndex(i % 4).toARGB() );
			}
			
			// draw disc, with thickness based on eq 
			float eqThickness = 100 + ( ringEQVal * 5000 );
			p.pushMatrix();			
			p.rotateY( i * circleSegment );
			Shapes.drawDisc3D( p, discRadius, discRadius + 100, eqThickness, discPrecision, -1, -1 );
			p.popMatrix();
			
			// draw orbiting star per ring
//			p.pushMatrix();
//			p.fill( _baseColor.toARGB() );//_ringColors[i].colorIntWithAlpha(ringAlpha, 0) );
//			float starX = innerRadius * scale * p.sin( p.frameCount * ringSpacingIndex * 0.01f );
//			float starY = innerRadius * scale * p.cos( p.frameCount * ringSpacingIndex * 0.01f );
//			p.translate( starX, starY, 0 );
//			p.rotateZ( i * (2*p.PI)/NUM_RINGS );
//			p.rotateY( i * (2*p.PI)/NUM_RINGS );
//			Shapes.drawStar( p, 5f, 50f * ringEQVal, 10f, 50 + 50 * ringEQVal, 0f);
//			p.popMatrix();
		}
		
		p.popMatrix();
	}

	protected void updateRotation() {
		_rotation.easeToPoint( _rotationTarget, 6 );
		p.rotateX( _rotation.x );
		p.rotateY( _rotation.y );
		p.rotateZ( _rotation.z );
	}
	
	public void reset() {
		updateLineMode();
		updateCamera();
	}
	
	public void updateLineMode() {
		_isWireframe = ( p.random(0f,2f) >= 1 ) ? false : true;
	}
	public void updateCamera() {
		// rotate
		float circleSegments = 4f;
		float circleSegment = (float) ( Math.PI * 2f ) / circleSegments;
		
		_rotationTarget.x = circleSegment * P.round( p.random( 0, circleSegments ) );
		_rotationTarget.y = circleSegment * P.round( p.random( 0, circleSegments ) );
		_rotationTarget.z = circleSegment * P.round( p.random( 0, circleSegments ) );
	}

	public void dispose() {
		_audioData = null;
	}

}

