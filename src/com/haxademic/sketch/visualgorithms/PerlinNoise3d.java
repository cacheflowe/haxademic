package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BadTVLinesFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.filters.shaders.InvertFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;

import controlP5.ControlP5;

public class PerlinNoise3d
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float increment = 0;
	public float detail = 0;
	public float xProgress = 0;
	public float yProgress = 0;
	float noiseScale = 0.003f;
	int octaves = 3;
	float noiseSpeed = 0.02f;
	float falloff = 0.5f;
	int spacing = 40;
	protected ControlP5 _cp5;
	protected float frames = 60 * 12;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(frames) );
	}

	public void setup() {
		super.setup();	

		_cp5 = new ControlP5(this);
		_cp5.addSlider("noiseScale").setPosition(20,20).setWidth(100).setRange(0.0001f, 0.025f).setValue(noiseScale);
		_cp5.addSlider("octaves").setPosition(20,60).setWidth(100).setRange(1, 8).setValue(octaves);
		_cp5.addSlider("noiseSpeed").setPosition(20,40).setWidth(100).setRange(0, 0.04f).setValue(noiseSpeed);
		_cp5.addSlider("falloff").setPosition(20,80).setWidth(100).setRange(0, 1f).setValue(falloff);
		_cp5.addSlider("spacing").setPosition(20,100).setWidth(100).setRange(5, 150f).setValue(spacing);
	}

	public void drawApp() {
		float progress = (p.frameCount % frames) / frames;
		
//		p.blendMode(P.BLEND);
		p.blendMode(PBlendModes.BLEND);
		if(p.frameCount >= 1) p.background(255);
//		DrawUtil.feedback(p.g, p.color(255), 0.6f, 0.1f);
		p.ortho();
		
		p.blendMode(PBlendModes.SUBTRACT);

//		DrawUtil.setDrawFlat2d(p, true);
		p.pushMatrix();
		float halfSize = p.width * 0.2f;

		p.translate(p.width/2, p.height/2 + spacing/4f, -halfSize);
		p.rotateY(progress * P.TWO_PI);
		p.rotateX(progress * P.TWO_PI);
//		p.rotateY(P.PI/4f);
//		p.rotateX(P.PI/4f);
		
		float autoFalloff = (progress < 0.5f) ? P.map(progress, 0, 0.5f, 0, 1) : P.map(progress, 0.5f, 1f, 1, 0);
		p.noiseDetail(octaves, autoFalloff); // falloff

		// For every x,y coordinate in a 2D space, calculate a noise value and produce a brightness value
		p.stroke(255);
		p.strokeWeight(1.85f);
		for (float x = -halfSize; x < halfSize; x += spacing) {
			for (float y = -halfSize; y < halfSize; y += spacing) {
				for (float z = -halfSize; z < halfSize; z += spacing) {
					p.stroke(p.noise(x) * 127f - 0f, p.noise(y) * 127f - 0f, p.noise(z) * 127f - 0f);
					float value = getNoise(x,y,z);
					float valueX = getNoise(x+spacing,y,z);
					float valueY = getNoise(x,y+spacing,z);
					float valueZ = getNoise(x,y,z+spacing);
					if(value >= 0.5f) {
						if(valueX > 0.5f && x + spacing < halfSize) p.line(x, y, z, x+spacing, y, z);
						if(valueY > 0.5f && y + spacing < halfSize) p.line(x, y, z, x, y+spacing, z);
						if(valueZ > 0.5f && z + spacing < halfSize) p.line(x, y, z, x, y, z+spacing);
					}
				}
			}
		}
		p.popMatrix();
		
//		CubicLensDistortionFilter.instance(p).applyTo(p);
//		BadTVLinesFilter.instance(p).applyTo(p.g);
//		VignetteFilter.instance(p).applyTo(p.g);
		
		// hide ControlP5
		// p.translate(-1000, 0);
	}
	
	protected float getNoise(float x, float y, float z ) {
		return p.noise(
				p.frameCount * noiseSpeed + x * noiseScale, 
				p.frameCount * noiseSpeed + y * noiseScale, 
				p.frameCount * noiseSpeed + z * noiseScale
		);
	}
	

}
