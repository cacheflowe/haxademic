package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class TextureShaderScrubber
extends BaseTexture {

	protected String _shaderFile;
	public String toString() {
		return this.getClass().getName() + " :: " + _shaderFile;
	}

	protected PGraphics _image;
	protected PShader _patternShader;
	protected PShader _vignette;
	protected PShader _brightness;
	protected PShader _saturation;
	protected int _timingFrame = 0;
	protected EasingFloat _timeSpeed = new EasingFloat(0, 15);
	protected EasingFloat _brightEaser = new EasingFloat(0, 10);
	protected float _time = 0f;
	protected int _mode = 0;

	public TextureShaderScrubber( int width, int height, String textureShader ) {
		super(width, height);
		
		
		loadShaders( textureShader );
	}
	
	protected void loadShaders( String textureShader ) {
		_shaderFile = textureShader;
		_patternShader = _texture.loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/textures/" + textureShader ); 
		_patternShader.set("time", _timeSpeed.value() );
		_patternShader.set("mode", _mode);

		_vignette = _texture.loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

		_brightness = _texture.loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/brightness.glsl" );
		_brightness.set("brightness", _brightEaser.value() );

		_saturation = _texture.loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/saturation.glsl" );
		_saturation.set("saturation", 0.25f );
	}

	public void draw() {
		updateShaders();
		_texture.background(0,255,0);
		_texture.filter( _patternShader );
		_texture.filter( _saturation );
		_texture.filter( _brightness );
		_texture.filter( _vignette );
	}
	
	public BaseTexture setActive( boolean isActive ) {
		boolean wasActive = _active;
		super.setActive(isActive);
		if( _active == true && wasActive == false ) {
			_timeSpeed.setCurrent( 0.0001f );
			_timeSpeed.setTarget( 0.0001f );
		}
		return this;
	}
	
	protected void updateShaders() {
		_timeSpeed.update();
		_time += _timeSpeed.value();

		_brightEaser.update();

		_patternShader.set("time", _timeSpeed.value() );
		_patternShader.set("mode", _mode);

		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

		_brightness.set("brightness", _brightEaser.value() );
	}
	
	public void updateTiming() {
		if( _timingFrame % 4 == 0 ) {
			_brightEaser.setCurrent(1.3f);
		} else {
			_brightEaser.setCurrent(1.0f);
		}
		_brightEaser.setTarget(0.25f);
		_timeSpeed.setTarget( MathUtil.randRangeDecimal(-0.15f, 0.15f) );
	}
	
	public void updateTimingSection() {
		_timingFrame = 0;
		_mode++;
		if(_mode >= 3) _mode = 0;
	}

}
