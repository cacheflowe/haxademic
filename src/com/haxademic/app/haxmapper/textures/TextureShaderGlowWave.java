package com.haxademic.app.haxmapper.textures;

public class TextureShaderGlowWave 
extends BaseShaderTexture {

	public TextureShaderGlowWave( int width, int height ) {
		super( width, height );
		
		super.loadShaders( "glowwave.glsl" );
	}
	
}
