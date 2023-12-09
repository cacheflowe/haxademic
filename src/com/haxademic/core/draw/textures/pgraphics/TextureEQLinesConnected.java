package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.RepeatFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PVector;

public class TextureEQLinesConnected 
extends BaseTexture {

	protected int points = 20;
	protected EasingFloat[] amps = new EasingFloat[points];
	protected PVector[] positions = new PVector[points];
	protected PVector[] noiseLoc = new PVector[points];
	protected PVector utilVec = new PVector();
	protected EasingFloat audioAmp = new EasingFloat(0, 0.2f);
	protected boolean useCurves = true;

	public TextureEQLinesConnected( int width, int height ) {
		super(width, height);
		
		_texture.beginDraw();
		_texture.background(0);
		_texture.endDraw();

		// build objects
		for (int i = 0; i < points; i++ ) {
			amps[i] = new EasingFloat(0, 0.8f);
			positions[i] = new PVector();
			noiseLoc[i] = new PVector();
		}
	}
	
	public void newLineMode() {
		useCurves = MathUtil.randBoolean();
	}
	
	public void updateDraw() {
		// update audio amp
		audioAmp.setTarget(AudioIn.amplitude()).update();

		// context
		PG.setCenterScreen(_texture);
		PG.setDrawCenter(_texture);
		
		// feedback w/shaders
		float zoom = 1f - 0.2f * audioAmp.value(); // zoom more when moving
		RepeatFilter.instance().setOffset(0, 0);
		RepeatFilter.instance().setZoom(zoom);
		RepeatFilter.instance().applyTo(_texture);
		float fade = P.map(audioAmp.value(), 0, 1, 1, 0.15f); // fade more when audio is quiet
		fade = P.constrain(fade, 0, 1);
		BrightnessStepFilter.instance().setBrightnessStep(-fade);
		BrightnessStepFilter.instance().applyTo(_texture);
		
		// draw points
		_texture.noFill();
		_texture.stroke(_color);
		float strokeWeight = width * 0.007f;
		_texture.strokeWeight(strokeWeight);
		_texture.beginShape();
		for (int i = 0; i < points; i++ ) {
			// lerp the EQ amp
			int ampInterval = i * 5; // skip frequencies
			amps[i].setTarget(AudioIn.audioFreq(1 + ampInterval)).update();
			
			// move point with noise
			PVector pos = positions[i];
			PVector noise = noiseLoc[i];
			noise.x += widthNorm(amps[i].value() * 10f);
			noise.y -= heightNorm(amps[i].value() * 10f);
			float noiseZoom = 0.09f; // how wiggly are the points? higher is more wiggly as destination pos jumps around more
			utilVec.set(
				-width * 0.5f + P.p.noise(noise.x * noiseZoom) * width * 1f,
				-height * 0.5f + P.p.noise(100 + noise.y * noiseZoom) * height * 1f
			);
			pos.lerp(utilVec, 0.15f);

			// draw shape
			if(useCurves) {
				_texture.curveVertex(positions[i].x, positions[i].y);
			} else {
				_texture.vertex(positions[i].x, positions[i].y);
			}
		}
		_texture.endShape();
	}
	
}
