package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.data.Point3D;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;

public class SphereClouds 
extends ElementBase 
implements IVizElement {
	
	SphereDraw _sphere;
	TColor _baseColor;

	protected Point3D _rotSpeed = new Point3D( 0, 0, 0 );
	protected Point3D _rotation = new Point3D( 0, 0, 0 );
	protected Point3D _rotationTarget = new Point3D( 0, 0, 0 );

	public SphereClouds( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}
	
	public void init() {
		_sphere = new SphereDraw( p, toxi, _audioData );
		reset();
	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		
		// draw center sphere
		p.pushMatrix();
		updateRotation();
		_sphere.setDrawProps(1000, 20, 32, _baseColor.toARGB() );
		_sphere.update();
		p.popMatrix();
		
		
//		_sphere.setDrawProps(p.width/5, 20, 128, _baseColor.toARGB() );
//		p.pushMatrix();
//		p.translate( 0, 0, -200 );
//		
//		p.pushMatrix();
//		p.translate( -400, 0, 0 );
//		p.rotateY(p.frameCount * 0.001f);
//		_sphere.update();
//		p.popMatrix();
//		
//		p.pushMatrix();
//		p.translate( 400, 0, 0 );
//		p.rotateY(-p.frameCount * 0.001f);
//		_sphere.update();
//		p.popMatrix();
//		
//		p.popMatrix();
	}
	
	protected void updateRotation() {
		_rotation.easeToPoint( _rotationTarget, 6 );
		p.rotateX( _rotation.x );
		p.rotateY( _rotation.y );
		p.rotateZ( _rotation.z );
		
		_rotationTarget.x += _rotSpeed.x;
		_rotationTarget.y += _rotSpeed.y;
		_rotationTarget.z += _rotSpeed.z;
	}
	
	public void reset() {
		updateCamera();
	}
	
	public void updateLineMode() {
	}
	
	public void updateCamera() {
		// rotate
		float circleSegment = (float) ( Math.PI * 2f );
		_rotationTarget.x = p.random( -circleSegment, circleSegment );
		_rotationTarget.y = p.random( -circleSegment, circleSegment );
		_rotationTarget.z = p.random( -circleSegment, circleSegment );

		_rotSpeed.x = p.random( 0.001f, 0.001f );
		_rotSpeed.y = p.random( 0.001f, 0.001f );
		_rotSpeed.z = p.random( 0.001f, 0.001f );
	}

	
	public void dispose() {
		super.dispose();
	}
	
}