package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.PBlendModes;
import com.haxademic.core.draw.filters.shaders.BlurProcessingFilter;
import com.haxademic.core.draw.filters.shaders.DilateFilter;
import com.haxademic.core.draw.filters.shaders.EdgesFilter;
import com.haxademic.core.draw.filters.shaders.InvertFilter;
import com.haxademic.core.draw.filters.shaders.SaturationFilter;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class SSAOTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float increment = 0;
	public float detail = 0;
	public float xProgress = 0;
	public float yProgress = 0;
	float noiseScale = 0.003f;
	int octaves = 3;
	float noiseSpeed = 0.002f;
	float falloff = 0.5f;
	int spacing = 20;
	protected float frames = 60 * 16;
	protected float progress = 0;
	
	protected PGraphics canvas;
	protected PGraphics ssaoBuffer;
	
	
	protected PShader ssao;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1200 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(frames) );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, true );
		p.appConfig.setProperty( AppSettings.RETINA, false );
	}

	public void setup() {
		super.setup();	
		
		canvas = p.createGraphics(p.width/2, p.height/2, P.P3D);
		ssaoBuffer = p.createGraphics(p.width/2, p.height/2, P.P2D);
		ssao = p.loadShader(FileUtil.getFile("shaders/filters/ssao-test.glsl"));
	}
	
	protected void drawShapes() {
		canvas.beginDraw();
		
		// rendering progress
		progress = (p.frameCount % frames) / frames;
		float progressRads = progress * P.TWO_PI;
		if(p.appConfig.getBoolean(AppSettings.RENDERING_MOVIE, false) == true) {
			if(p.frameCount > 2 + p.appConfig.getInt(AppSettings.RENDERING_MOVIE_STOP_FRAME, 0)) p.exit();
		}
		
		// set background color
		canvas.background(0);
		
		// calculate sphere size
		float halfSize = canvas.width * 0.4f;
		
		// move to center
		canvas.translate(canvas.width/2, canvas.height/2 + spacing/4f, -halfSize);
		canvas.rotateY(progressRads);
		canvas.rotateX(progressRads);
		
		// lighting
		canvas.lights();
		
		// draw some shapes
		canvas.noStroke();
		for (int i = 0; i < 40; i++) {
			canvas.fill(127f + 127 * P.sin(i + progressRads), 127f + 127 * P.cos(i + progressRads * 2f), 127f + 127 * P.sin(i + progressRads));
			canvas.pushMatrix();
			canvas.translate(halfSize * P.sin(i + progressRads), halfSize * P.cos(i + progressRads), 0);
			canvas.rotateX(progressRads + i);
			canvas.rotateY(progressRads + i);
			if(i % 2 == 0) {
				canvas.box(canvas.height * 0.2f);
			} else {
				canvas.sphere(canvas.height * 0.1f);
			}
			canvas.popMatrix();
		}
		
		canvas.endDraw();
	}
	
	protected void createSSAOBuffer() {
		ssaoBuffer.beginDraw();
		ssaoBuffer.blendMode(PBlendModes.BLEND);
		ssaoBuffer.copy(canvas, 0, 0, canvas.width, canvas.height, 0, 0, ssaoBuffer.width, ssaoBuffer.height);
		
//		BrightnessFilter.instance(p).setBrightness(10.5f);
//		BrightnessFilter.instance(p).applyTo(ssaoBuffer);
//		ContrastFilter.instance(p).setContrast(0.5f);
//		ContrastFilter.instance(p).applyTo(ssaoBuffer);
		
		EdgesFilter.instance(p).applyTo(ssaoBuffer);
		SaturationFilter.instance(p).setSaturation(0);
		SaturationFilter.instance(p).applyTo(ssaoBuffer);
		InvertFilter.instance(p).applyTo(ssaoBuffer);
		
//		ssao.set("time", p.millis());
//		p.filter(ssao);
		BlurProcessingFilter.instance(p).setBlurSize(5);
		BlurProcessingFilter.instance(p).applyTo(ssaoBuffer);
//		ContrastFilter.instance(p).setContrast(0.5f);
//		ContrastFilter.instance(p).applyTo(ssaoBuffer);

//		BlurBasicFilter.instance(p).applyTo(ssaoBuffer);
//		BlurBasicFilter.instance(p).applyTo(ssaoBuffer);
//		BlurBasicFilter.instance(p).applyTo(ssaoBuffer);
//		BlurBasicFilter.instance(p).applyTo(ssaoBuffer);
		DilateFilter.instance(p).applyTo(ssaoBuffer);

		ssaoBuffer.blendMode(PBlendModes.MULTIPLY);
		ssaoBuffer.image(ssaoBuffer, 0, 0);
		ssaoBuffer.image(ssaoBuffer, 0, 0);
		ssaoBuffer.endDraw();

	}

	public void drawApp() {
		drawShapes();
		createSSAOBuffer();
		
		p.blendMode(PBlendModes.BLEND);
		p.image(canvas, 0, 0);
		p.image(ssaoBuffer, 0, p.height/2);
		
		p.image(canvas, p.width/2, 0);
		p.blendMode(PBlendModes.MULTIPLY);
		p.image(canvas, p.width/2, 0);
		p.image(ssaoBuffer, p.width/2, 0);
		// post process
//		EdgesFilter.instance(p).applyTo(p);
//		SaturationFilter.instance(p).setSaturation(0);
//		SaturationFilter.instance(p).applyTo(p);
//		InvertFilter.instance(p).applyTo(p);
//		
////		BrightnessFilter.instance(p).setBrightness(10.5f);
////		BrightnessFilter.instance(p).applyTo(p);
////		ssao.set("time", p.millis());
////		p.filter(ssao);
//		BlurBasicFilter.instance(p).applyTo(p);
//		BlurBasicFilter.instance(p).applyTo(p);
//		BlurBasicFilter.instance(p).applyTo(p);
//		BlurBasicFilter.instance(p).applyTo(p);
//		BlurBasicFilter.instance(p).applyTo(p);
//		BlurBasicFilter.instance(p).applyTo(p);
//		BlurBasicFilter.instance(p).applyTo(p);
//		BlurBasicFilter.instance(p).applyTo(p);
		
		// hide ControlP5
//		p.translate(-1000, 0);
	}
	
	
	protected float distance( float x1, float y1, float z1, float x2, float y2, float z2 ) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;
	    return P.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	protected float getNoise(float x, float y, float z ) {
		return p.noise(
				p.frameCount * noiseSpeed + x * noiseScale, 
				p.frameCount * noiseSpeed + y * noiseScale, 
				p.frameCount * noiseSpeed + z * noiseScale
		);
	}
	
}
