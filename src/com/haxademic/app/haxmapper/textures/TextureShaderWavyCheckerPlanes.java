package com.haxademic.app.haxmapper.textures;

public class TextureShaderWavyCheckerPlanes
extends BaseShaderTexture {

	public TextureShaderWavyCheckerPlanes( int width, int height ) {
		super( width, height );
		
		super.loadShaders( "wavy-checker-planes.glsl" );
	}
	
}
