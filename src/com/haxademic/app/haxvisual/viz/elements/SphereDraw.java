package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.shapes.Shapes;

public class SphereDraw
extends ElementBase 
implements IVizElement {
	
	protected float _circleInc;
	protected float _baseRadius;
	protected float _amp;
	protected int _numCubes;
	protected int _baseColor;
	protected float startPhi = 0;
//	protected PImage _image;

	public SphereDraw( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		// set some defaults
		_baseRadius = 100;
		_amp = 20;
		_numCubes = 32;
		
//		_image = p.loadImage("images/circle-gradient-100.png");
//		p.imageMode( PConstants.CENTER );
	}
	
	public void setDrawProps(float baseRadius, float amp, int numCubes, int fillColor) {
		_baseRadius = baseRadius;
		_amp = amp;
		_numCubes = numCubes;
		_baseColor = fillColor;
	}
	
	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy().toARGB();
	}

	public void update() {
		// sphere coordinate vars
		int u = 1;
		float theta = 0;
		float phi = 0;
		float thetaIncrement = 1f;
		float phiIncrement = startPhi;
		float pointX = 0;
		float pointY = 0;
		float pointZ = 0;

		float twoPi = (float)Math.PI * 2;
		
		for( int i = 0; i < _numCubes; i++ ) {
			// get position of cube
			phi = p.acos( -1f + ( 2f * i - 1f ) / _numCubes );
     		theta = p.sqrt( _numCubes * p.PI ) * phi;
     		pointX = _baseRadius * p.cos(theta)* p.sin(phi);
     		pointY = _baseRadius * p.sin(theta)* p.sin(phi);
     		pointZ = _baseRadius * p.cos(phi);

     		// get size and alpha and draw cube
     		float fillAlpha = 0f + _audioData.getFFT().spectrum[i + 20];
     		float size = 1 + 40 * _audioData.getFFT().spectrum[i + 20];
     		p.noStroke();
     		
//     		p.pushMatrix();
     		//     		p.noFill();
//     		p.translate(pointX, pointY, pointZ);
//     		p.tint(0.5f, fillAlpha/2);
//			p.image( _image, 0, 0, 30, 30 );
//     		p.popMatrix();
     		
     		p.fill( _baseColor, fillAlpha * 255 );//, fillAlpha * 127 );	// , fillAlpha
//			toxi.box( new AABB(new Vec3D(pointX, pointY, pointZ),new Vec3D(size,size,size)) );
			
     		
     		p.pushMatrix();
     		p.translate(pointX, pointY, pointZ);
     		p.rotateX( twoPi / 10 * i );
     		p.rotateY( twoPi / 10 * i );
     		p.rotateZ( twoPi / 10 * i );
			Shapes.drawStar( p, 7, size, size * 0.15f, size, 0);
     		p.popMatrix();
		}
	}

	public void reset() {

	}

	public void dispose() {
		super.dispose();
	}

}
