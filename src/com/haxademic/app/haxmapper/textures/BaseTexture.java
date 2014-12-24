package com.haxademic.app.haxmapper.textures;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.system.FileUtil;

public class BaseTexture {
	
	protected PGraphics _texture;
	protected boolean _active;
	protected int _useCount = 0;
	protected int _color;
	protected ColorHaxEasing _colorEase;
	public static PShader _threshold;
	public static PShader _invert;
	public static PShader _chroma;
	
	protected boolean _makeOverlay;
	protected boolean _knockoutBlack;

	public BaseTexture() {
		_active = false;
		_color = P.p.color(255);
		_colorEase = new ColorHaxEasing( "#ffffff", 5 );
	}
	
	protected void buildGraphics( int width, int height ) {
		if( _texture != null ) _texture.dispose();
		_texture = P.p.createGraphics( width, height, PConstants.OPENGL );
		_texture.smooth(OpenGLUtil.SMOOTH_MEDIUM);

		// postprocessing - only create 1 shader for all instances
		if(_threshold == null) _threshold = P.p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blackandwhite.glsl" );
		if(_invert == null) _invert = P.p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/invert.glsl" );
		if(_chroma == null) _chroma = P.p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/chroma-gpu.glsl" );
		_chroma.set("thresholdSensitivity", 0.0f);
		_chroma.set("smoothing", 0.5f);
		_chroma.set("colorToReplace", 0.0f,0.0f,0.0f);

	}
	
	public PGraphics texture() {
		return _texture;
	}
	
	public void setActive( boolean isActive ) {
		_active = isActive;
		if( _active == true ) addUseCount();
	}
	
	public void setAsOverlay( boolean isOverlay ) {
		_makeOverlay = isOverlay;
	}
	
	public void setKnockoutBlack( boolean knockoutBlack ) {
		_knockoutBlack = knockoutBlack;
	}
	
	public void postProcess() {
		if( _makeOverlay == true ) {
			_texture.filter( _threshold );
			_texture.filter( _invert );
			_texture.filter( _chroma );
			
//			_chroma.set("thresholdSensitivity", P.p.midi.midiCCPercent(0, 22));
//			_chroma.set("smoothing", P.p.midi.midiCCPercent(0, 23));
//			_chroma.set("colorToReplace", P.p.midi.midiCCPercent(0, 24), P.p.midi.midiCCPercent(0, 25), P.p.midi.midiCCPercent(0, 26));
		}
		if( _knockoutBlack == true ) {
			_texture.filter( _chroma );
		}
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
		_texture.beginDraw();		
		updateDraw();
		postProcess();
		_texture.endDraw();
	}
	
	public void updateDraw() {
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
}
