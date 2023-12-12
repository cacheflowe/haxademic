package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;

import processing.core.PShape;
import processing.core.PVector;

public class TextureOuterSphere
extends BaseTexture {

	protected float _targetRadius = 1000;
	protected boolean _isWireframe = true;
	protected boolean _isSphere = true;
	protected PVector _rotSpeed = new PVector( 0, 0, 0 );
	protected PVector _rotSpeedTarget = new PVector( 0, 0, 0 );
	protected PVector rotation = new PVector( 0, 0, 0 );
	protected PShape icosa;
	protected boolean _makeNewMesh;

	public TextureOuterSphere( int width, int height ) {
		super(width, height);
		
		
		int detail = 3;
		icosa = Icosahedron.createIcosahedron(P.p.g, detail, pg);
		PShapeUtil.scaleShapeToHeight(icosa, width * 2f);

		reset();
	}
	
	public void reset() {
		newLineMode();
	}
	
	public void newLineMode() {
		_isWireframe = ( MathUtil.randBoolean() == true ) ? false : true;
		_isSphere = ( MathUtil.randBoolean() == true ) ? false : true;
		
		// new sphere mesh flag - don't do it here since it's asynchronous apparently
		_makeNewMesh = true;
	}
	
	public void newRotation() {
		float newRotSpeed = P.p.random( 0.001f, 0.01f );
		int wichAxis = (int) P.p.random( 0, 2 );
		_rotSpeedTarget.x = ( wichAxis == 0 ) ? newRotSpeed : 0;
		_rotSpeedTarget.y = ( wichAxis == 1 ) ? newRotSpeed : 0;
		_rotSpeedTarget.z = ( wichAxis == 2 ) ? newRotSpeed : 0;
	}

	public void draw() {
		// prep context
//		_texture.clear();
		pg.background(0);
		
		PG.setCenterScreen( pg );
		pg.pushMatrix();

		// update rotation
		_rotSpeed.lerp(_rotSpeedTarget, 0.10f);
		rotation.add(_rotSpeed);
		pg.rotateX( rotation.x );
		pg.rotateY( rotation.y );
		pg.rotateZ( rotation.z );
		
		// prep sphere drawing
		if( _isWireframe ) {
			pg.noFill(); 
			pg.strokeWeight = 2;
			pg.stroke(255);
		} else {
			pg.noStroke(); 
		}
		
		// draw outer sphere
		PShapeUtil.drawTrianglesAudio(pg, icosa, 1f, _colorEase.colorInt());
		
		// pop context
		pg.popMatrix();
	}
}
