package com.haxademic.app.musicvideos;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.VideoFrameGrabber;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class CachePatterChromaVideoTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

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


	public String thresholdSensitivity = "thresholdSensitivity";
	public String smoothing = "smoothing";
	public String colorToReplaceR = "colorToReplaceR";
	public String colorToReplaceG = "colorToReplaceG";
	public String colorToReplaceB = "colorToReplaceB";
	public String darkness = "darkness";
	public String spread = "spread";

	float _startMovieFrame = 0;
	float _addMovieFrame = 0;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "720" );
		p.appConfig.setProperty( AppSettings.WIDTH, "960" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "540" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.SHOW_SLIDERS, true );
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

		p.ui.addSlider(thresholdSensitivity, 0.75f, 0, 1, 0.01f, false);
		p.ui.addSlider(smoothing, 0.26f, 0, 1, 0.01f, false);
		p.ui.addSlider(colorToReplaceR, 0.29f, 0, 1, 0.01f, false);
		p.ui.addSlider(colorToReplaceG, 0.93f, 0, 1, 0.01f, false);
		p.ui.addSlider(colorToReplaceB, 0.14f, 0, 1, 0.01f, false);
		p.ui.addSlider(darkness, 0.4f, 0, 1, 0.01f, false);
		p.ui.addSlider(spread, 0.35f, 0, 1, 0.01f, false);
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
				
		_chromaKeyFilter.set("thresholdSensitivity", p.ui.value(thresholdSensitivity));
		_chromaKeyFilter.set("smoothing", p.ui.value(smoothing));
		_chromaKeyFilter.set("colorToReplace", p.ui.value(colorToReplaceR), p.ui.value(colorToReplaceG), p.ui.value(colorToReplaceB));
		
//		_vignette = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.4f);
		_vignette.set("spread", 0.35f);

		_vignette.set("darkness", p.ui.value(darkness));
		_vignette.set("spread", p.ui.value(spread));

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