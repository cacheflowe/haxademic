package com.haxademic.app.haxmapper.textures;

import processing.core.PConstants;
import processing.core.PGraphics;

import com.haxademic.core.app.P;

public class BaseTexture {
	
	protected PGraphics _texture;
	protected boolean _active;
	protected int _color;
	
	public BaseTexture() {
		_active = false;
		_color = P.p.color(255);
	}
	
	public PGraphics texture() {
		return _texture;
	}
	
	public void setActive( boolean isActive ) {
		
	}
	
	public void setColor( int color ) {
		_color = color;
	}
	
	public void update() {
		// override with subclass
	}
	
	public void updateTiming() {
		// override with subclass
	}
	
	public void updateTimingSection() {
		// override with subclass
	}
	
	public void newMode() {
		// override with subclass
	}
	
	public void newLineMode() {
		// override with subclass
	}
	
	public void newRotation() {
		// override with subclass
	}
	
	protected void buildGraphics( int width, int height ) {
		_texture = P.p.createGraphics( width, height, PConstants.OPENGL );
	}
}
