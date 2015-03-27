package com.haxademic.app.ophelias;

import processing.opengl.PShader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.kinect.KinectSilhouetteBasic;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class OpheliasSilhouetteTester
extends PAppletHax{
	
	protected KinectSilhouetteBasic _silhouette;

	protected boolean _isDebug = false;
	
	protected PShader _desaturate;
	protected PShader _vignette;
	protected PShader _postBrightness;
	protected PShader _clipBrightness;
	protected PShader _threshold;
	protected PShader _blurH;
	protected PShader _blurV;
	protected PShader _invert;
	protected PShader _badTV;
	protected PShader edge;
	protected PShader _halftone;
	protected PShader _mirror;
	protected PShader _contrast;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "kinect_active", "true" );
		_appConfig.setProperty( "kinect_mirrored", "true" );
	}

	public void setup() {
		super.setup();
		_silhouette = new KinectSilhouetteBasic();
	}
	
	public void drawApp() {
		p.background(0);
		setShaderValues();
		_silhouette.update();
		if(!_isDebug) {
			p.image(_silhouette._canvas, 0, 0);
		} else {
			p.image(_silhouette._kinectPixelated, 0, 0);
		}
//		postProcessEffects();
	}
	

	
	// Video player ================ 
	protected void buildShaders() {
		
		// build shaders
		_desaturate = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/saturation.glsl" );
		_threshold = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blackandwhite.glsl" );
		_threshold.set("cutoff", 0.5f);
		_blurV = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-vertical.glsl" );
		_blurV.set( "v", 5f/p.height );
		_blurH = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-horizontal.glsl" );
		_blurH.set( "h", 5f/p.width );
		_invert = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/invert.glsl" );
		
		_badTV = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/badtv.glsl" );
		_badTV.set("time", millis() / 1000.0f);
		_badTV.set("grayscale", 0);
		_badTV.set("nIntensity", 0.75f);
		_badTV.set("sIntensity", 0.55f);
		_badTV.set("sCount", 4096.0f);


		edge = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/edges.glsl" ); 
		
		_halftone = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/dotscreen.glsl" ); 
		_halftone.set("tSize", 256f, 256f);
		_halftone.set("center", 0.5f, 0.5f);
		_halftone.set("angle", 1.57f);
		_halftone.set("scale", 1f);

		_mirror = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/mirror.glsl" ); 

		_contrast = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/contrast.glsl" ); 
		_contrast.set("contrast", 1.4f);
		
		_vignette = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.85f);
		_vignette.set("spread", 0.25f);

		_postBrightness = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_postBrightness.set("brightness", 1.0f );

		_clipBrightness = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_clipBrightness.set("brightness", 2.0f );
		
	}
	
	protected void setShaderValues() {
		int brightnessKnob = 48;
		if(p.midi.midiCCPercent(0, brightnessKnob) != 0) {
			_postBrightness.set("brightness", p.midi.midiCCPercent(0, brightnessKnob) * 5f);
		}

		int halftoneKnob = 47;
		if(p.midi.midiCCPercent(0, halftoneKnob) != 0) {
			_halftone.set("scale", p.midi.midiCCPercent(0, halftoneKnob) * 5f);
		}
		
		int thresholdKnob = 46;
		if(p.midi.midiCCPercent(0, thresholdKnob) != 0) {
			_threshold.set("cutoff", p.midi.midiCCPercent(0, thresholdKnob));
		}
	}
	
	protected void postProcessEffects() {
		p.filter(_vignette);
		
//		_curFrameMaskInverse.filter(_clipBrightness);
//		_curFrameMaskInverse.filter(_contrast);
//		_curFrameMaskInverse.filter(_threshold);
//		_curFrameMaskInverse.filter(_blurH);
//		_curFrameMaskInverse.filter(_blurV);
//		_curFrameMaskInverse.filter(_invert);

//		_badTV.set("time", millis() / 1000.0f);
//		p.filter(_badTV);
		p.filter( _postBrightness );		
	}
	
	protected void handleInput( boolean isMidi ) {
		super.handleInput( isMidi );
		
		if( p.key == 'd' ){
			_isDebug = !_isDebug;
		}
	}
	
}