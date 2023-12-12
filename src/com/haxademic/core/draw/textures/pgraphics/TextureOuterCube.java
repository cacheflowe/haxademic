package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.camera.CameraUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;

import processing.core.PVector;

public class TextureOuterCube
extends BaseTexture {

	protected PVector _rotSpeed = new PVector( 0, 0, 0 );
	protected PVector _rotSpeedTarget = new PVector( 0, 0, 0 );
	protected PVector rotation = new PVector( 0, 0, 0 );
	protected TextureEQGrid audioTexture;

	public TextureOuterCube( int width, int height ) {
		super(width, height);
		
		audioTexture = new TextureEQGrid(128, 128);
	}
	
	public void newRotation() {
		float newRotSpeed = P.p.random( 0.001f, 0.01f );
		int wichAxis = (int) P.p.random( 0, 2 );
		_rotSpeedTarget.x = ( wichAxis == 0 ) ? newRotSpeed : 0;
		_rotSpeedTarget.y = ( wichAxis == 1 ) ? newRotSpeed : 0;
		_rotSpeedTarget.z = ( wichAxis == 2 ) ? newRotSpeed : 0;
	}

	public void drawPre() {
		audioTexture.update();
	}
	
	public void draw() {
		// prep context
//		_texture.clear();
		pg.background(0);
		
		PG.setCenterScreen( pg );
		CameraUtil.setCameraDistance(pg, 200, 20000);
		pg.pushMatrix();

		// update rotation
		_rotSpeed.lerp(_rotSpeedTarget, 0.10f);
		rotation.add(_rotSpeed);
		pg.rotateX( rotation.x );
		pg.rotateY( rotation.y );
		pg.rotateZ( rotation.z );
		
		// draw outer sphere
		Shapes.drawTexturedCube(pg, width * 2.25f, audioTexture.texture());
		
		// pop context
		pg.popMatrix();
	}
}
