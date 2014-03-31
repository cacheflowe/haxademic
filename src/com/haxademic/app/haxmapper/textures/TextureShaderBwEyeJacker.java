package com.haxademic.app.haxmapper.textures;

import processing.core.PGraphics;
import processing.opengl.PShader;

import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

public class TextureShaderBwEyeJacker 
extends BaseTexture {

	protected PGraphics _image;
	protected PShader _patternShader;
	protected PShader _vignette;
	protected PShader _brightness;
	protected int _timingFrame = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 15);
	protected EasingFloat _brightEaser = new EasingFloat(0, 10);
	protected int _mode = 0;

	public TextureShaderBwEyeJacker( int width, int height ) {
		super();
		
		buildGraphics( width, height );

		loadShaders();
	}
	
	protected void loadShaders() {
		_patternShader = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/textures/bw-eye-jacker-01.glsl" ); 
		_patternShader.set("time", _timeEaser.value() );
		_patternShader.set("mode", _mode);

		_vignette = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

		_brightness = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_brightness.set("brightness", _brightEaser.value() );
	}

	public void update() {
		_texture.beginDraw();		
		
		updateShaders();
		_texture.background(0,255,0);
		_texture.filter( _patternShader );
		_texture.filter( _brightness );
		_texture.filter( _vignette );
		
		_texture.endDraw();
	}
	
	protected void updateShaders() {
		_timeEaser.update();
		_brightEaser.update();

		_patternShader.set("time", _timeEaser.value() );
		_patternShader.set("mode", _mode);

		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

		_brightness.set("brightness", _brightEaser.value() );
	}
	
	public void updateTiming() {
		if( _timingFrame % 4 == 0 ) {
			_brightEaser.setCurrent(1.1f);
			_timeEaser.setTarget( _timeEaser.value() + 4 );
		} else {
			_brightEaser.setCurrent(0.8f);
			_timeEaser.setTarget( _timeEaser.value() + 1 );
		}
		_brightEaser.setTarget(0.25f);
		_timingFrame++;
	}
	
	public void updateTimingSection() {
		_timingFrame = 0;
		_mode++;
		if(_mode >= 3) _mode = 0;
	}

}
