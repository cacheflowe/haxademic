package com.haxademic.core.draw.textures.pgraphics.shared;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.camera.CameraUtil;
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
	protected boolean _active;
	protected boolean _newlyActive;
	protected int _useCount = 0;
	protected int _color;
	protected EasingColor _colorEase;
	protected int _timingFrame = 0;
	protected int renderTime = 0;
	
//	public static PShader _chroma;
	protected int _brightMode = -1;
	protected EasingFloat _brightEaser = new EasingFloat(1, 10);
	protected boolean _makeOverlay;
	protected boolean _knockoutBlack;
	
	protected ArrayList<BaseTexture> _curTexturePool;

	public BaseTexture(int width, int height) {
		this.width = width;
		this.height = height;
		_active = false;
		_color = P.p.color(255);
		_colorEase = new EasingColor( "#ffffff", 5 );
	}
	
	public String toString() {
		// return this.getClass().getName();
		return this.getClass().getSimpleName() + " (" + renderTime + "ms)";
	}
	
	public PGraphics texture() {
		return _texture;
	}
	
	public boolean isActive() {
		return _active;
	}
	
	public BaseTexture setActive( boolean isActive ) {
		_active = isActive;
		_newlyActive = true;
		if( _active == true ) addUseCount();
		return this;
	}
	
	public void setAsOverlay( boolean isOverlay ) {
		_makeOverlay = isOverlay;
	}
	
	public void setKnockoutBlack( boolean knockoutBlack ) {
		_knockoutBlack = knockoutBlack;
	}
	
	protected void applyChromaBlackKnockout(PGraphics pg) {
		// set black knockout chroma shader 
		ChromaColorFilter.instance(P.p).setColorToReplace(0f, 0f, 0f);
		ChromaColorFilter.instance(P.p).setThresholdSensitivity(0.2f);
		ChromaColorFilter.instance(P.p).setSmoothing(0.25f);
		ChromaColorFilter.instance(P.p).applyTo(pg);
	}
	
	public void setCurTexturePool(ArrayList<BaseTexture> curTexturePool) {
		_curTexturePool = curTexturePool;
	}
	
	public void postProcess() {

		if( _makeOverlay == true ) {
			ThresholdFilter.instance(P.p).setCutoff(0.5f);
			ThresholdFilter.instance(P.p).applyTo(_texture);
			InvertFilter.instance(P.p).applyTo(_texture);
			applyChromaBlackKnockout(_texture);
		} else if( _knockoutBlack == true ) {
			applyChromaBlackKnockout(_texture);
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
		_colorEase.setTargetInt( color );
	}
	
	public void update() {
		// TODO: reset to null after another BaseTexture uses it
		if(_texture == null) _texture = PGPool.getPG(width, height);
		PGPool.updatePG(_texture);

		int startRender = P.p.millis();
		_colorEase.update();
		resetUseCount(); // this should be the last thing that happens in a frame, to help with texture pool optimization
		
		preDraw();
		
		_texture.beginDraw();
		_texture.perspective();
		_texture.noLights();
//		_texture.strokeJoin(P.PROJECT);
//		_texture.strokeCap(P.ROUND);
		_texture.blendMode(PBlendModes.BLEND);
//		CameraUtil.setCameraDistance(_texture, 200, 20000);
		PG.setDrawCorner(_texture);
		PG.push(_texture);
		updateDraw();
		PG.pop(_texture);
		_texture.endDraw();
		
//		postProcess();
		_newlyActive = false;
		renderTime = P.p.millis() - startRender;
	}
	
	public void preDraw() {
		// override with subclass
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
		PG.setDrawCorner(_texture);
		_texture.copy(
			_texture, 
			0, 
			0, 
			width, 
			height, 
			P.round(-amp/2f), 
			P.round(-amp/2f), 
			P.round(width + amp), 
			P.round(height + amp)
		);
		_texture.fill(0, darkness * 255f);
		_texture.noStroke();
		_texture.rect(0, 0, width, height);
	}

}
