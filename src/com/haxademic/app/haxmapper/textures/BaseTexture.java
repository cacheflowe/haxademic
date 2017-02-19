package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.filters.shaders.InvertFilter;
import com.haxademic.core.draw.filters.shaders.ThresholdFilter;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class BaseTexture {
	
	protected PGraphics _texture = null;
	protected boolean _active;
	protected int _useCount = 0;
	protected int _color;
	protected ColorHaxEasing _colorEase;
	protected int _timingFrame = 0;
	
	public static PShader _chroma;
	protected int _brightMode = -1;
	protected EasingFloat _brightEaser = new EasingFloat(1, 10);
	protected boolean _makeOverlay;
	protected boolean _knockoutBlack;

	public BaseTexture() {
		_active = false;
		_color = P.p.color(255);
		_colorEase = new ColorHaxEasing( "#ffffff", 5 );
	}
	
	protected void buildGraphics( int width, int height ) {
		if( _texture != null ) _texture.dispose();
		_texture = P.p.createGraphics( width, height, PConstants.P3D );
//		_texture.smooth(OpenGLUtil.SMOOTH_MEDIUM);
//		_texture.smooth(OpenGLUtil.SMOOTH_HIGH);
//		_texture.smooth(OpenGLUtil.SMOOTH_LOW);
//		_texture.noSmooth();
		OpenGLUtil.setTextureRepeat(_texture);

		// postprocessing - only create 1 shader for all instances
		if(_chroma == null) _chroma = P.p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/chroma-color.glsl" );
		_chroma.set("thresholdSensitivity", 0.0f);
		_chroma.set("smoothing", 0.5f);
		_chroma.set("colorToReplace", 0.0f,0.0f,0.0f);
	}
	
	public String toString() {
		return this.getClass().getName();
	}
	
	public PGraphics texture() {
		return _texture;
	}
	
	public boolean isActive() {
		return _active;
	}
	
	public BaseTexture setActive( boolean isActive ) {
		_active = isActive;
		if( _active == true ) addUseCount();
		return this;
	}
	
	public void setAsOverlay( boolean isOverlay ) {
		_makeOverlay = isOverlay;
	}
	
	public void setKnockoutBlack( boolean knockoutBlack ) {
		_knockoutBlack = knockoutBlack;
	}
	
	public void postProcess() {
		if( _makeOverlay == true ) {
			ThresholdFilter.instance(P.p).applyTo(_texture);
			InvertFilter.instance(P.p).applyTo(_texture);
			_texture.filter( _chroma );
		}
		if( _knockoutBlack == true ) {
			_texture.filter( _chroma );
		}
		
		if( _brightMode > -1 ) {
			_brightEaser.update();
			if(P.p.frameCount == 10) {
				P.println(_brightEaser.value());
			}
			BrightnessFilter.instance(P.p).setBrightness(_brightEaser.value());
			BrightnessFilter.instance(P.p).applyTo(_texture);
		}
	}
	
	public void updateBrightnessTiming() {
		// update brightness filter - fade up or down
		if( _timingFrame % 4 == 0 ) {
			if(_brightMode == 0) _brightEaser.setCurrent(1.3f);
			else 				 _brightEaser.setCurrent(0f);
		} else {
			if(_brightMode == 0) _brightEaser.setCurrent(1.0f);
			else 				 _brightEaser.setCurrent(0.5f);
		}
		if(_brightMode == 0) _brightEaser.setTarget(0.25f);
		else 				 _brightEaser.setTarget(1.2f);
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
		_texture.endDraw();
		postProcess();
	}
	
	public void updateDraw() {
		// override with subclass
	}
	
	public void updateTiming() {
		if( _brightMode > -1 ) updateBrightnessTiming();
		
		// keep incrementing
		_timingFrame++;
	}
	
	public void updateTimingSection() {
		_timingFrame = 0;
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
	
	public void feedback(float amp, float darkness) {
		DrawUtil.setDrawCorner(_texture);
		_texture.copy(
			_texture, 
			0, 
			0, 
			_texture.width, 
			_texture.height, 
			P.round(-amp/2f), 
			P.round(-amp/2f), 
			P.round(_texture.width + amp), 
			P.round(_texture.height + amp)
		);
		_texture.fill(0, darkness * 255f);
		_texture.noStroke();
		_texture.rect(0, 0, _texture.width, _texture.height);
	}

}
