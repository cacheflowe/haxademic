package com.haxademic.sketch.test;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.image.filters.shaders.BadTVLinesFilter;
import com.haxademic.core.image.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.image.filters.shaders.InvertFilter;
import com.haxademic.core.image.filters.shaders.VignetteFilter;

import controlP5.ControlP5;

public class PerlinNoise3d
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float increment = 0;
	public float detail = 0;
	public float xProgress = 0;
	public float yProgress = 0;
	float noiseScale = 0.005f;
	int octaves = 2;
	float noiseSpeed = 0.02f;
	float falloff = 0.5f;
	int spacing = 50;
	protected ControlP5 _cp5;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
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
		p.blendMode(P.BLEND);
		if(p.frameCount == 1) p.background(0);
		DrawUtil.feedback(p.g, 0, 0.7f, 0.35f);
//		p.ortho();
		
		p.blendMode(P.SCREEN);

		DrawUtil.setDrawFlat2d(p, true);
		p.pushMatrix();
		int halfSize = p.width;

		p.translate(p.width/2, p.height/2, -halfSize);
		p.rotateY(p.frameCount*0.01f);
		p.rotateX(P.sin(p.frameCount*0.01f));
//		p.rotateY(P.PI/4f);
//		p.rotateX(P.PI/4f);
		
		p.noiseDetail(octaves, falloff);

		// For every x,y coordinate in a 2D space, calculate a noise value and produce a brightness value
		p.stroke(255);
		p.strokeWeight(0.5f);
		for (int x = -halfSize; x < halfSize; x += spacing) {
			for (int y = -halfSize; y < halfSize; y += spacing) {
				for (int z = -halfSize; z < halfSize; z += spacing) {
					p.stroke(p.noise(x) * 255, p.noise(y) * 255, p.noise(z) * 255);
					float value = getNoise(x,y,z);
					float valueNext = getNoise(x,y,z+spacing);
					float valueLeft = getNoise(x-spacing,y,z);
					float valueUp = getNoise(x,y+spacing,z);
					if(value > 0.5f) {
						if(valueNext > 0.5f) p.line(x, y, z, x, y, z+spacing);
						if(valueLeft > 0.5f) p.line(x, y, z, x-spacing, y, z);
						if(valueUp > 0.5f) p.line(x, y, z, x, y+spacing, z);
					}
				}
			}
		}
		p.popMatrix();
		
//		CubicLensDistortionFilter.instance(p).applyTo(p);
		BadTVLinesFilter.instance(p).applyTo(p.g);
		VignetteFilter.instance(p).applyTo(p.g);
	}
	
	protected float getNoise(int x, int y, int z ) {
		return p.noise(
				p.frameCount * noiseSpeed + x * noiseScale, 
				p.frameCount * noiseSpeed + y * noiseScale, 
				p.frameCount * noiseSpeed + z * noiseScale
		);
	}
	

}
