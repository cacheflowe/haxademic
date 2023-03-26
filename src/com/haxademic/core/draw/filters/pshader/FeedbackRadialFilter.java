package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class FeedbackRadialFilter
extends BaseFragmentShader {

	public static FeedbackRadialFilter instance;
	
	public FeedbackRadialFilter() {
		super("haxademic/shaders/filters/feedback-radial.glsl");
		setAmp(1f);
		setSampleMult(1f);
		setWaveAmp(1f);
		setWaveFreq(1f);
		setAlphaMult(1f);
	}
	
	public static FeedbackRadialFilter instance() {
		if(instance != null) return instance;
		instance = new FeedbackRadialFilter();
		return instance;
	}
	
	public void setAmp(float amp) {
		shader.set("amp", amp);
	}
	
	public void setMultX(float multX) {
		shader.set("multX", multX);
	}
	
	public void setMultY(float multY) {
		shader.set("multY", multY);
	}
	
	public void setSampleMult(float samplemult) {
		shader.set("samplemult", samplemult);
	}

	public void setWaveAmp(float waveAmp) {
		shader.set("waveAmp", waveAmp);
	}

	public void setWaveFreq(float waveFreq) {
		shader.set("waveFreq", waveFreq);
	}
	
	public void setWaveStart(float waveStart) {
		shader.set("waveStart", waveStart);
	}
	
	public void setAlphaMult(float alphaMult) {
		shader.set("alphaMult", alphaMult);
	}
	
}
