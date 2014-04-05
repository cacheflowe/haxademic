package com.haxademic.app.haxmapper.textures;

public class TextureShaderBwEyeJacker 
extends BaseShaderTexture {

	public TextureShaderBwEyeJacker( int width, int height ) {
		super( width, height );
		
		super.loadShaders( "bw-eye-jacker-01.glsl" );
	}
	
}
