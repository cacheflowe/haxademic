package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.image.filters.shaders.BrightnessFilter;
import com.haxademic.core.image.filters.shaders.SaturationFilter;
import com.haxademic.core.image.filters.shaders.VignetteFilter;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class TextureShaderTimeStepper
extends BaseTexture {

	protected PGraphics _image;
	protected PShader _patternShader;
	protected int _timingFrame = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 15);
	protected EasingFloat _brightEaser = new EasingFloat(0, 10);
	protected int _mode = 0;
	protected float _smallTimeStep = 1f;
	protected float _largeTimeStep = 3f;
	protected float _nonBeatSpeed = 0.1f;
	protected boolean _nonBeatTimeMode = false;
	protected float _reverseThreshold = 100f;
	
	protected float[] locations;
	protected float[] colors;

	public TextureShaderTimeStepper( int width, int height, String textureShader ) {
		super();
		
		buildGraphics( width, height );
		loadShaders( textureShader );
	}
	
	protected void loadShaders( String textureShader ) {
		_patternShader = _texture.loadShader( FileUtil.getFile("shaders/textures/" + textureShader)); 
		_patternShader.set("time", _timeEaser.value() );
		_patternShader.set("mode", _mode);
		_patternShader.set("mouse", (float)P.p.mouseX, (float)P.p.mouseY);
		locations = new float[50];
		for(int i=0; i < locations.length; i++) {
			locations[i] = MathUtil.randRangeDecimal(0, 640);
		}
		_patternShader.set("locations", locations);
		colors = new float[75];
		for(int i=0; i < colors.length; i++) {
			colors[i] = MathUtil.randRangeDecimal(0, 1f);
		}
		_patternShader.set("colors", colors);

		SaturationFilter.instance(P.p).setSaturation(0.25f);
		VignetteFilter.instance(P.p).setDarkness(0.7f);
		VignetteFilter.instance(P.p).setSpread(0.15f);
	}

	public void updateDraw() {
		updateShaders();
		// _texture.background(0,255,0);
		_texture.filter( _patternShader );
		SaturationFilter.instance(P.p).applyTo(_texture);
		BrightnessFilter.instance(P.p).applyTo(_texture);
	}
	
	public void setActive( boolean isActive ) {
		boolean wasActive = _active;
		super.setActive(isActive);
		if( _active == true && wasActive == false ) {
			_timeEaser.setCurrent( 0.0001f );
			_timeEaser.setTarget( 0.0001f );
			_nonBeatTimeMode = MathUtil.randRangeDecimal(0, 1f) > 0.8f;
		}
	}
	
	protected void updateShaders() {
		if(_nonBeatTimeMode == true) {
			_timeEaser.setCurrent( _timeEaser.value() + _nonBeatSpeed );
			_timeEaser.setTarget( _timeEaser.value() + _nonBeatSpeed );			
		} else {
			_timeEaser.update();
		}
		
		// switch time directions
		if( Math.abs(_timeEaser.value()) > _reverseThreshold ) {
			_timeEaser.setCurrent( ( _timeEaser.value() > _reverseThreshold ) ? _reverseThreshold : -_reverseThreshold );
			_largeTimeStep = _largeTimeStep * -1f;
			_smallTimeStep = _smallTimeStep * -1f;
			_nonBeatSpeed = _nonBeatSpeed * -1f;
			_timeEaser.setTarget( _timeEaser.value() + _largeTimeStep );
			_timeEaser.update();
			_timeEaser.update();
		}


		_patternShader.set("time", _timeEaser.value() );
		_patternShader.set("mode", _mode);
		_patternShader.set("mouse", (float)P.p.mouseX, P.p.height - (float)P.p.mouseY);
		for(int i=0; i < locations.length; i++) {
			locations[i] += 10f * (-0.5f + P.p.noise(i*10f+P.p.frameCount));
		}
		_patternShader.set("locations", locations);
		for(int i=0; i < colors.length; i++) {
			//colors[i] = P.p.noise(i*10f+P.p.frameCount);
		}
		_patternShader.set("colors", colors);


		_brightEaser.update();
		BrightnessFilter.instance(P.p).setBrightness(_brightEaser.value());
	}
	
	public void updateTiming() {
		if( _timingFrame % 4 == 0 ) {
			_brightEaser.setCurrent(1.3f);
			_timeEaser.setTarget( _timeEaser.value() + _largeTimeStep );
		} else {
			_brightEaser.setCurrent(1.0f);
			_timeEaser.setTarget( _timeEaser.value() + _smallTimeStep );
		}
		_nonBeatSpeed = MathUtil.randRangeDecimal(-_smallTimeStep/10f, _smallTimeStep/10f);
		_brightEaser.setTarget(0.25f);
		_timingFrame++;
	}
	
	public void updateTimingSection() {
		_timingFrame = 0;
		_mode++;
		if(_mode >= 3) _mode = 0;
	}

}
