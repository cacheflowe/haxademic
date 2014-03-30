package com.haxademic.app.haxmapper.textures;

import processing.core.PConstants;
import processing.core.PGraphics;

import com.haxademic.core.app.P;

public class BaseTexture {
	
	protected PGraphics _texture;
	
	public BaseTexture() {
		
	}
	
	public PGraphics texture() {
		return _texture;
	}
	
	public void update() {
		// override with subclass
	}
	
	protected void buildGraphics( int width, int height ) {
		_texture = P.p.createGraphics( width, height, PConstants.OPENGL );
	}
}
