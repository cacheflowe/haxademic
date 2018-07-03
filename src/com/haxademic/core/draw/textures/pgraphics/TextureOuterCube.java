package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.math.MathUtil;

import processing.core.PShape;
import processing.core.PVector;

public class TextureOuterCube
extends BaseTexture {

	protected PVector _rotSpeed = new PVector( 0, 0, 0 );
	protected PVector _rotSpeedTarget = new PVector( 0, 0, 0 );
	protected PVector rotation = new PVector( 0, 0, 0 );
	protected TextureEQGrid audioTexture;

	public TextureOuterCube( int width, int height ) {
		super();
		buildGraphics( width, height );
		audioTexture = new TextureEQGrid(128, 128);
	}
	
	public void newRotation() {
		float newRotSpeed = P.p.random( 0.001f, 0.01f );
		int wichAxis = (int) P.p.random( 0, 2 );
		_rotSpeedTarget.x = ( wichAxis == 0 ) ? newRotSpeed : 0;
		_rotSpeedTarget.y = ( wichAxis == 1 ) ? newRotSpeed : 0;
		_rotSpeedTarget.z = ( wichAxis == 2 ) ? newRotSpeed : 0;
	}

	public void preDraw() {
		audioTexture.update();
	}
	
	public void updateDraw() {
		// prep context
		_texture.clear();
		DrawUtil.setCenterScreen( _texture );
		_texture.pushMatrix();

		// update rotation
		_rotSpeed.lerp(_rotSpeedTarget, 0.10f);
		rotation.add(_rotSpeed);
		_texture.rotateX( rotation.x );
		_texture.rotateY( rotation.y );
		_texture.rotateZ( rotation.z );
		
		// draw outer sphere
		Shapes.drawTexturedCube(_texture, _texture.width * 2.25f, audioTexture.texture());
		
		// pop context
		_texture.popMatrix();
	}
}
