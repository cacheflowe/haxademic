package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class TextureShaderTimeStepper
extends BaseTexture {
	
	protected String _shaderFile;
	public String toString() {
		return this.getClass().getSimpleName() + " :: " + _shaderFile;
	}

	public enum ShaderTimeMode {
		BeatEaseOut,
		BeatSpeedUp,
		DirectionSpeedShift,
		ForwardOsc
	}
	
	protected PGraphics _image;
	protected PShader _patternShader;
	protected int _mode = 0;

	protected EasingFloat _timeEaser = new EasingFloat(0, 15);
	protected float _smallTimeStep = 0.5f;
	protected float _largeTimeStep = 2f;
	protected float _nonBeatSpeed = 0.1f;
	protected float _beatSpeedUp = 0.1f;
	protected float _beatSpeedUpMax = 0.055f;
	protected ShaderTimeMode _nonBeatTimeMode = ShaderTimeMode.BeatEaseOut;
	protected float _reverseTimeThreshold = 100f;
	
	// special crap for shader day drawing learnings ---------
	protected float[] locations;
	protected float[] colors;
	// -------------------------------------------------------

	public TextureShaderTimeStepper( int width, int height, String textureShader ) {
		super(width, height);
		_shaderFile = textureShader;
		loadShaders( textureShader );
	}
	
	protected void loadShaders( String textureShader ) {
		_patternShader = TextureShader.loadShader(textureShader); 
		_patternShader.set("time", _timeEaser.value() );
		_patternShader.set("mode", _mode);
	}

		
	public void draw() {
		pg.background(0);
		updateTime();
		updateDrawWithTime(_timeEaser.value());
	}
	
	public void updateDrawWithTime(float time) {
		// updateShaders();
		// _texture.background(0,255,0);
		
		_patternShader.set("time", time );
		_patternShader.set("mode", _mode);
		pg.filter( _patternShader );
		
		postProcess();
	}
	
	public void postProcess() {
//		SaturationFilter.instance().setSaturation(0.3f);
//		SaturationFilter.instance().applyTo(_texture);
//		FXAAFilter.instance().applyTo(_texture);
//		super.postProcess();
	}
	
	public BaseTexture setActive( boolean isActive ) {
		boolean wasActive = _active;
		super.setActive(isActive);
		_brightMode = MathUtil.randRange(0, 1);
		if( _active == true && wasActive == false ) {
			if(_timeEaser != null) {
				_timeEaser.setCurrent( 0.0001f );
				_timeEaser.setTarget( 0.0001f );
			}
			pickNewTimeMode();
		}
		return this;
	}
	
	protected void pickNewTimeMode() {
		float newTimeMode = MathUtil.randRange(0, 3);
		if(newTimeMode == 0) _nonBeatTimeMode = ShaderTimeMode.BeatEaseOut;
		else if(newTimeMode == 1) _nonBeatTimeMode = ShaderTimeMode.BeatSpeedUp;
		else if(newTimeMode == 2) _nonBeatTimeMode = ShaderTimeMode.DirectionSpeedShift;
		else if(newTimeMode == 3) _nonBeatTimeMode = ShaderTimeMode.ForwardOsc;
	}
	
	protected void updateTime() {
		if(_nonBeatTimeMode == ShaderTimeMode.DirectionSpeedShift) {
			_timeEaser.setCurrent( _timeEaser.value() + _nonBeatSpeed );
			_timeEaser.setTarget( _timeEaser.value() + _nonBeatSpeed );			
		} else if(_nonBeatTimeMode == ShaderTimeMode.BeatEaseOut) {
			_timeEaser.update();
		} else if(_nonBeatTimeMode == ShaderTimeMode.BeatSpeedUp) {
			_timeEaser.setCurrent( _timeEaser.value() + _beatSpeedUp );
			_timeEaser.setTarget( _timeEaser.value() + _beatSpeedUp );
			_beatSpeedUp *= 1.2f;
			if(Math.abs(_beatSpeedUp) > _beatSpeedUpMax) _beatSpeedUp = P.constrain(_beatSpeedUp, -_beatSpeedUpMax, _beatSpeedUpMax);
		} else if(_nonBeatTimeMode == ShaderTimeMode.ForwardOsc) {
			_timeEaser.setCurrent( _timeEaser.value() + _nonBeatSpeed * 0.75f + _nonBeatSpeed * 0.25f * P.sin(P.p.frameCount/40f) );
			_timeEaser.setTarget( _timeEaser.value() + _nonBeatSpeed );
		}
		
		// switch time directions
		if( Math.abs(_timeEaser.value()) > _reverseTimeThreshold ) {
			_timeEaser.setCurrent( ( _timeEaser.value() > _reverseTimeThreshold ) ? _reverseTimeThreshold : -_reverseTimeThreshold );
			_largeTimeStep = _largeTimeStep * -1f;
			_smallTimeStep = _smallTimeStep * -1f;
			_nonBeatSpeed = _nonBeatSpeed * -1f;
			_beatSpeedUp = _beatSpeedUp * -1f;
			_timeEaser.setTarget( _timeEaser.value() + _largeTimeStep );
			_timeEaser.update();
			_timeEaser.update();
		}
	}
	
	// special crap for shader day drawing learnings ---------
	protected void updateShaders() {
		_patternShader.set("mouse", (float)P.p.mouseX, P.p.height - (float)P.p.mouseY);
		for(int i=0; i < locations.length; i++) {
			locations[i] += 10f * (-0.5f + P.p.noise(i*10f+P.p.frameCount));
		}
		_patternShader.set("locations", locations);
		for(int i=0; i < colors.length; i++) {
			// colors[i] = P.p.noise(i*10f+P.p.frameCount);
		}
		_patternShader.set("colors", colors);
	}
	// -------------------------------------------------------
	
	public void updateTiming() {
		super.updateTiming();
		
		// handle 3 modes
		if(_nonBeatTimeMode == ShaderTimeMode.BeatEaseOut) {
			if(_timingFrame % 4 == 0) {
				_timeEaser.setTarget( _timeEaser.value() + _largeTimeStep );
			} else {
				_timeEaser.setTarget( _timeEaser.value() + _smallTimeStep );
			}
		} else if(_nonBeatTimeMode == ShaderTimeMode.DirectionSpeedShift) {
			_nonBeatSpeed = MathUtil.randRangeDecimal(_smallTimeStep/120f, _smallTimeStep/80f);
			if(MathUtil.randBoolean() == true) _nonBeatSpeed *= -1;
		} else if(_nonBeatTimeMode == ShaderTimeMode.BeatSpeedUp) {
			if(_nonBeatTimeMode == ShaderTimeMode.BeatSpeedUp) {
				if(_beatSpeedUp > 0) _beatSpeedUp = 0.001f;
				else _beatSpeedUp = -0.001f;
			}
		} else if(_nonBeatTimeMode == ShaderTimeMode.ForwardOsc) {
			if( _timingFrame % 4 == 0 ) {
				_nonBeatSpeed = MathUtil.randRangeDecimal(-_smallTimeStep/80f, _smallTimeStep/80f);
			}
		}
	}
	
	public void updateTimingSection() {
		super.updateTimingSection();
		
		_mode++;
		if(_mode >= 3) _mode = 0;
	}

}
