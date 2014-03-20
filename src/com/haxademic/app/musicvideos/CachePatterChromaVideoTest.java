package com.haxademic.app.musicvideos;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.render.VideoFrameGrabber;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class CachePatterChromaVideoTest
extends PAppletHax{
	
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
	protected EasingFloat _vignetteEaser = new EasingFloat(0.5f, 13);
	
	int _timingFrames = -1;
	protected float _timeConstantInc = 0.01f;
	protected EasingFloat _cloudTimeEaser = new EasingFloat(0, 13);
	protected EasingFloat _superShapeOpacityEaser = new EasingFloat(0, 17);
	protected LinearFloat _chromaOpacityEaser;
	protected LinearFloat _overallBrightnessEaser = new LinearFloat(2, 0.004f);	// (1/0.004)/30 = 8.3 seconds

	float _curMovieFrame = 1800;
	float _playbackFrameCountInc = 1f/3f;

	float _songLengthFrames = 4520; // really 4519, but we're letting Renderer shut this down at the end of the audio file

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "1280" );
		_appConfig.setProperty( "height", "720" );
		_appConfig.setProperty( "rendering", "false" );
	}
	
	public void setup() {
		super.setup();
		
		// video frame buffer graphics
		_movieBuffer = p.createGraphics(p.width, p.height, P.P2D);

		// video scrubber
		_videoFrames = new VideoFrameGrabber(p, "/Users/cacheflowe/Documents/workspace/plasticsoundsupply/resources/_releases/PSS020 - ambient compilation/patter-video/selectes-reference-all-export-264.mov", 30, 0);

		_vignette = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.5f);
		_vignette.set("spread", 0.15f);

	}
	
	public void drawApp() {
		p.background(255);


		_videoFrames.setFrameIndex( p.frameCount + 1950 );
		
		// shaders
		_chromaKeyFilter = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/chroma-gpu.glsl" );
		_chromaKeyFilter.set("thresholdSensitivity", 0.65f);
//		_chromaKeyFilter.set("thresholdSensitivity", (float)p.mouseY/(float)p.height);
		_chromaKeyFilter.set("smoothing", 0.19f);
//		_chromaKeyFilter.set("smoothing", (float)p.mouseY/(float)p.height);
		_chromaKeyFilter.set("colorToReplace", 0.48f,0.8f,0.2f);
//		_chromaKeyFilter.set("colorToReplace", (float)p.mouseY/(float)p.height,(float)p.mouseX/(float)p.width,0.1f);
		
		_vignette = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.5f);
		_vignette.set("spread", (float)p.mouseX/(float)p.width);
		P.println("== mouse == (y) = "+(float)p.mouseY/(float)p.height+"  (x) = "+(float)p.mouseX/(float)p.width);

		_movieBuffer.beginDraw();
		_movieBuffer.image(_videoFrames.movie(), 0, 0, p.width, p.height);
		_movieBuffer.endDraw();
		_movieBuffer.filter(_chromaKeyFilter);
		
		p.image(_movieBuffer, 0, 0);
		p.filter(_vignette);
	}
}