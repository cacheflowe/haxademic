package com.haxademic.core.draw.textures.pgraphics.shared;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ChromaColorFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;

public class BaseTexture {
	
	protected PGraphics _texture;
	protected int width;
	protected int height;
	protected boolean smoothPG;
	protected boolean _active;
	protected boolean _newlyActive;
	protected int _useCount = 0;
	protected int _color;
	protected EasingColor _colorEase;
	protected int _timingFrame = 0;
	protected int renderTime = 0;
	
	protected int _brightMode = -1;
	protected EasingFloat _brightEaser = new EasingFloat(1, 10);
	
	protected boolean _makeOverlay;
	protected boolean _knockoutBlack;
	
	protected ArrayList<BaseTexture> _curTexturePool;

	public BaseTexture(int width, int height) {
		this(width, height, true);
	}
	
	public BaseTexture(int width, int height, boolean smoothPG) {
		this.width = width;
		this.height = height;
		this.smoothPG = smoothPG;
//		_texture = PG.newPG(width, height, smoothPG, true);
		setActive(true);
		_color = P.p.color(255);
		_colorEase = new EasingColor( "#ffffff", 5 );
	}
	
	public String toString() {
		// return this.getClass().getName();
		return this.getClass().getSimpleName() + " (" + renderTime + "ms)";
	}
	
	public PGraphics texture() {
		if(_texture == null) _texture = PGPool.getPG(width, height);
		return _texture;
	}
	
	public boolean isActive() {
		return _active;
	}
	
	public BaseTexture setActive( boolean isActive ) {
		_active = isActive;
		_newlyActive = true;
		if( _active == true ) {
			if(_texture == null) {
				_texture = PGPool.getPG(width, height);
//				_texture = PG.newPG(width, height, smoothPG, true);
			}
			addUseCount();
		}
		return this;
	}

	public float widthNorm(float val) {
		return val * width / 1000;
	}
	
public float heightNorm(float val) {
		return val * height / 1000;
	}

	public void setAsOverlay( boolean isOverlay ) {
		_makeOverlay = isOverlay;
	}
	
	public void setKnockoutBlack( boolean knockoutBlack ) {
		_knockoutBlack = knockoutBlack;
	}
	
	public void setCurTexturePool(ArrayList<BaseTexture> curTexturePool) {
		_curTexturePool = curTexturePool;
	}
	
	public void postProcess() {

		if( _makeOverlay == true ) {
			ThresholdFilter.instance().setCutoff(0.5f);
			ThresholdFilter.instance().applyTo(_texture);
			InvertFilter.instance().applyTo(_texture);
			ChromaColorFilter.instance().presetBlackKnockout().applyTo(_texture);
		} else if( _knockoutBlack == true ) {
			ChromaColorFilter.instance().presetBlackKnockout().applyTo(_texture);
		}
		
		if( _brightMode > -1 ) {
			_brightEaser.update();
			if(P.p.frameCount == 10) {
				P.println(_brightEaser.value());
			}
			BrightnessFilter.instance().setBrightness(_brightEaser.value());
			BrightnessFilter.instance().applyTo(_texture);
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
		_colorEase.setTargetInt( color );
	}
	
	public void update() {
		if(_active == false) {
			_texture = null;	// do this on the main draw thread so _texture doesn't disappear in a race condition
			return; 
		}

		PGPool.updatePG(_texture);

		int startRender = P.p.millis();
		_colorEase.update();
		resetUseCount(); // this should be the last thing that happens in a frame, to help with texture pool optimization
		
		drawPre();
		
		_texture.beginDraw();
		_texture.push();
		_texture.perspective();
		_texture.noLights();
		_texture.blendMode(PBlendModes.BLEND);
//		CameraUtil.setCameraDistance(_texture, 200, 20000);
		PG.setDrawCorner(_texture);
		_texture.push();
		draw();
		_texture.pop();
		_texture.pop();
		_texture.endDraw();
		
//		postProcess();
		_newlyActive = false;
		renderTime = P.p.millis() - startRender;
	}
	
	public void drawPre() {
		// override with subclass
	}
	
	public void draw() {
		// override with subclass
	}
	
	public void randomizeAll() {
		updateTiming();
		updateTimingSection();
		newMode();
		newLineMode();
		newRotation();
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
	
}
