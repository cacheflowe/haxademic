package com.haxademic.app.haxmapper.textures;

import processing.core.PConstants;
import processing.core.PGraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorHaxEasing;

public class BaseTexture {
	
	protected PGraphics _texture;
	protected boolean _active;
	protected int _useCount = 0;
	protected int _color;
	protected ColorHaxEasing _colorEase;

	public BaseTexture() {
		_active = false;
		_color = P.p.color(255);
		_colorEase = new ColorHaxEasing( "#ffffff", 5 );
	}
	
	public PGraphics texture() {
		return _texture;
	}
	
	public void setActive( boolean isActive ) {
		_active = isActive;
		if( _active == true ) addUseCount();
	}
	
	public void addUseCount() {
		_useCount += 1;
	}
	
	public void resetUseCount() {
		_useCount = 0;
	}
	
	public int useCount() {
		return _useCount;
	}
	
	public void setColor( int color ) {
		_color = color;
		_colorEase.setTargetColorInt( color );
	}
	
	public void update() {
		_colorEase.update();
		resetUseCount(); // this should be the last thing that happens in a frame, to help with texture pool optimization
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
		if( _texture != null ) _texture.dispose();
		_texture = P.p.createGraphics( width, height, PConstants.OPENGL );
		_texture.noSmooth();
	}
}
