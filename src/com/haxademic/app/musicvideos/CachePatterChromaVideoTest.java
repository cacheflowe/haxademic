package com.haxademic.app.musicvideos;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.VideoFrameGrabber;

import controlP5.ControlP5;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class CachePatterChromaVideoTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PGraphics _movieBuffer;
	PImage _movieBufferImg;
	
	VideoFrameGrabber _videoFrames;
	boolean _fadeInNextSegmetWithShader = false;
	
	PGraphics _bg;
	PShader _clouds;
	PShader _fxaa;
	PShader _chromaKeyFilter;
	PShader _desaturate;
	PShader _resaturate;
	PShader _brightness;
	PShader _opacity;
	PShader _vignette;
	
	MotionBlurPGraphics _pgMotionBlur;


	protected ControlP5 _cp5;
	public float thresholdSensitivity;
	public float smoothing;
	public float colorToReplaceR;
	public float colorToReplaceG;
	public float colorToReplaceB;
	public float darkness;
	public float spread;

	float _startMovieFrame = 0;
	float _addMovieFrame = 0;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "720" );
		p.appConfig.setProperty( AppSettings.WIDTH, "960" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "540" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}
	
	public void setup() {
		super.setup();
	
		_pgMotionBlur = new MotionBlurPGraphics(6);

		// video frame buffer graphics
		_movieBuffer = p.createGraphics(p.width, p.height, P.P2D);

		// video scrubber
		_videoFrames = new VideoFrameGrabber(p, FileUtil.getHaxademicDataPath() + "video/patter/ultrasoft-selects-540.mp4", 30, 0);

		_vignette = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/vignette.glsl" );
		_desaturate = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/saturation.glsl" );
		_resaturate = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/saturation.glsl" );

		_videoFrames.setFrameIndex(_startMovieFrame);

		_cp5 = new ControlP5(this);
		int cp5W = 160;
		int cp5X = 20;
		int cp5Y = 20;
		int cp5YSpace = 40;
		_cp5.addSlider("thresholdSensitivity").setPosition(cp5X,cp5Y).setWidth(cp5W).setRange(0,1f).setValue(0.75f);
		_cp5.addSlider("smoothing").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.26f);
		_cp5.addSlider("colorToReplaceR").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.29f);
		_cp5.addSlider("colorToReplaceG").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.93f);
		_cp5.addSlider("colorToReplaceB").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.14f);
		_cp5.addSlider("darkness").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.4f);
		_cp5.addSlider("spread").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.35f);
	}
	
	public void drawApp() {
		p.background(255);

		_addMovieFrame += 0.5f;
		_videoFrames.setFrameIndex(_startMovieFrame + _addMovieFrame);
		P.println(_startMovieFrame + _addMovieFrame);

		// shaders
		_chromaKeyFilter = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/chroma-gpu.glsl" );
		
		_chromaKeyFilter.set("thresholdSensitivity", 0.65f);
		_chromaKeyFilter.set("smoothing", 0.19f);
		_chromaKeyFilter.set("colorToReplace", 0.48f,0.8f,0.2f);
		
		_chromaKeyFilter.set("thresholdSensitivity", 0.65f);
		_chromaKeyFilter.set("smoothing", 0.26f);
		_chromaKeyFilter.set("colorToReplace", 0.29f,0.93f,0.14f);
				
		_chromaKeyFilter.set("thresholdSensitivity", thresholdSensitivity);
		_chromaKeyFilter.set("smoothing", smoothing);
		_chromaKeyFilter.set("colorToReplace", colorToReplaceR, colorToReplaceG, colorToReplaceB);
		
//		_vignette = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.4f);
		_vignette.set("spread", 0.35f);

		_vignette.set("darkness", darkness);
		_vignette.set("spread", spread);

		_movieBuffer.beginDraw();
		_movieBuffer.image(_videoFrames.movie(), 0, 0, p.width, p.height);
		_movieBuffer.endDraw();
		_movieBuffer.filter(_chromaKeyFilter);
		_movieBuffer.filter(_desaturate);
		
//		p.image(_movieBuffer, 0, 0);
		_pgMotionBlur.updateToCanvas(_movieBuffer, p.g, 0.3f);

		p.filter(_vignette);
		
		
		float _opacity = MathUtil.getPercentWithinRange(p.height * 0.75f, p.height, p.mouseY);//(p.mouseY, p.height * 0.75f, p.height, 0, 1f);
		P.println(_opacity);

	}
	
	public void mousePressed() {
		super.mousePressed();
		if(p.mouseY < p.height * 0.75f) return;
		_addMovieFrame = 0;
		_startMovieFrame = Math.round(((float)p.mouseX/(float)p.width) * (_videoFrames.movie().duration() * 30.0f));
		_videoFrames.setFrameIndex(_startMovieFrame);
//		_videoFrames.setTimeFromPercent((float)p.width/(float)p.mouseX);
	}
}