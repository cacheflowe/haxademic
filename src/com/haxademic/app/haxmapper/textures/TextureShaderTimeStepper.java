package com.haxademic.app.haxmapper.textures;

import processing.core.PGraphics;
import processing.opengl.PShader;

import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

public class TextureShaderTimeStepper
extends BaseTexture {

	protected PGraphics _image;
	protected PShader _patternShader;
	protected PShader _vignette;
	protected PShader _brightness;
	protected PShader _saturation;
	protected int _timingFrame = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 15);
	protected EasingFloat _brightEaser = new EasingFloat(0, 10);
	protected int _mode = 0;
	protected float _smallTimeStep = 1f;
	protected float _largeTimeStep = 3f;
	protected float _reverseThreshold = 100f;

	public TextureShaderTimeStepper( int width, int height, String textureShader ) {
		super();
		
		buildGraphics( width, height );
		loadShaders( textureShader );
	}
	
	protected void loadShaders( String textureShader ) {
		_patternShader = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/textures/" + textureShader ); 
		_patternShader.set("time", _timeEaser.value() );
		_patternShader.set("mode", _mode);

		_vignette = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

		_brightness = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_brightness.set("brightness", _brightEaser.value() );

		_saturation = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/saturation.glsl" );
		_saturation.set("saturation", 0.25f );
	}

	public void update() {
		super.update();

		_texture.beginDraw();		
		
		updateShaders();
		_texture.background(0,255,0);
		_texture.filter( _patternShader );
		_texture.filter( _saturation );
		_texture.filter( _brightness );
		_texture.filter( _vignette );
		
		_texture.endDraw();
	}
	
	public void setActive( boolean isActive ) {
		boolean wasActive = _active;
		super.setActive(isActive);
		if( _active == true && wasActive == false ) {
			_timeEaser.setCurrent( 0.0001f );
			_timeEaser.setTarget( 0.0001f );
		}
	}
	
	protected void updateShaders() {
		_timeEaser.update();
		if( _timeEaser.value() > _reverseThreshold || _timeEaser.value() < -_reverseThreshold ) {
			_timeEaser.setCurrent( ( _timeEaser.value() > _reverseThreshold ) ? _reverseThreshold : -_reverseThreshold );
			_largeTimeStep = _largeTimeStep * -1f;
			_smallTimeStep = _smallTimeStep * -1f;
			_timeEaser.setTarget( _timeEaser.value() + _largeTimeStep );
			_timeEaser.update();
			_timeEaser.update();
		}

		_brightEaser.update();

		_patternShader.set("time", _timeEaser.value() );
		_patternShader.set("mode", _mode);

		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

		_brightness.set("brightness", _brightEaser.value() );
	}
	
	public void updateTiming() {
		if( _timingFrame % 4 == 0 ) {
			_brightEaser.setCurrent(1.3f);
			_timeEaser.setTarget( _timeEaser.value() + _largeTimeStep );
		} else {
			_brightEaser.setCurrent(1.0f);
			_timeEaser.setTarget( _timeEaser.value() + _smallTimeStep );
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
