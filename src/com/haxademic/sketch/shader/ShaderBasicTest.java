package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.filters.shaders.BadTVLinesFilter;
import com.haxademic.core.image.filters.shaders.BrightnessFilter;
import com.haxademic.core.image.filters.shaders.ChromaColorFilter;
import com.haxademic.core.image.filters.shaders.ColorCorrectionFilter;
import com.haxademic.core.image.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.image.filters.shaders.DeformTunnelFanFilter;
import com.haxademic.core.image.filters.shaders.HueFilter;
import com.haxademic.core.image.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.image.filters.shaders.SaturationFilter;
import com.haxademic.core.image.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.image.filters.shaders.VignetteFilter;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

@SuppressWarnings("serial")
public class ShaderBasicTest
extends PAppletHax {
	
	protected PGraphics _buffer;
	protected PShader _textureShader;
	protected String _textureShaderFile;
	protected float _timeEaseInc = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 15);
	float _frames = 100;

	public void setup() {
		super.setup();
		
		_textureShaderFile = FileUtil.getHaxademicDataPath() + "shaders/textures/flame-wisps.glsl";
		_textureShader = p.loadShader( _textureShaderFile );
		
		_buffer = createGraphics( width,  height, P2D );
	}

	public void draw() {
		background(0, 0, 0);
	
		// rendering progress
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radsComplete = P.TWO_PI * percentComplete;

		_timeEaser.update();
		_timeEaseInc = _timeEaser.value();

		applyTime( _textureShader );
		// applyResolution( _textureShader );
		// applyMouse( _textureShader );
		
		
		_buffer.filter( _textureShader );		
		PGraphics filterTargetCanvas = _buffer;
		
		ColorCorrectionFilter.instance(p).setBrightness(0.1f * P.cos(radsComplete));
		ColorCorrectionFilter.instance(p).setContrast(1f + 0.1f * P.sin(radsComplete));
		ColorCorrectionFilter.instance(p).setGamma(1f + 0.2f * P.sin(radsComplete));
		ColorCorrectionFilter.instance(p).applyTo(filterTargetCanvas);

		BrightnessFilter.instance(p).setBrightness(1f + 0.5f * P.sin(radsComplete));
		BrightnessFilter.instance(p).applyTo(filterTargetCanvas);
//		ContrastFilter.instance(p).setContrast(2f);
//		ContrastFilter.instance(p).applyTo(filterTargetCanvas);
		SaturationFilter.instance(p).setSaturation(1f + 1f * P.sin(radsComplete));
		SaturationFilter.instance(p).applyTo(filterTargetCanvas);
		VignetteFilter.instance(p).setDarkness(-0.75f + 0.25f * P.sin(radsComplete));
		VignetteFilter.instance(p).applyTo(filterTargetCanvas);
//		BlurHFilter.instance(p).applyTo(filterTargetCanvas);
//		BlurHFilter.instance(p).setBlur(1f / (filterTargetCanvas.width*1.2f + filterTargetCanvas.width * P.cos(radsComplete)));
//		BlurVFilter.instance(p).applyTo(filterTargetCanvas);
//		BlurVFilter.instance(p).setBlur(1f / (filterTargetCanvas.width*1.2f + filterTargetCanvas.width * P.cos(radsComplete)));

//		WobbleFilter.instance(p).setTime( _timeEaseInc * 2f);
//		WobbleFilter.instance(p).setSpeed( 1.0f + 0.5f * P.sin(radsComplete));
//		WobbleFilter.instance(p).setStrength( 0.001f + 0.0005f * P.sin(radsComplete));
//		WobbleFilter.instance(p).setSize( 200f + 25f * P.sin(radsComplete));
//		WobbleFilter.instance(p).applyTo(filterTargetCanvas);
//		KaleidoFilter.instance(p).setAngle(P.PI * P.sin(radsComplete));
//		KaleidoFilter.instance(p).setSides(P.round(6 + 2f * P.sin(radsComplete)));
//		KaleidoFilter.instance(p).applyTo(filterTargetCanvas);
//		MirrorFilter.instance(p).applyTo(filterTargetCanvas);
//		InvertFilter.instance(p).applyTo(filterTargetCanvas);
//		PixelateFilter.instance(p).setDivider(40f, 40f * filterTargetCanvas.height/filterTargetCanvas.width);
//		PixelateFilter.instance(p).applyTo(filterTargetCanvas);
		RadialRipplesFilter.instance(p).setTime( _timeEaseInc / 5f);
		RadialRipplesFilter.instance(p).setAmplitude(0.5f + 0.5f * P.sin(radsComplete));
		RadialRipplesFilter.instance(p).applyTo(filterTargetCanvas);
		BadTVLinesFilter.instance(p).applyTo(filterTargetCanvas);
		DeformTunnelFanFilter.instance(p).setTime(p.frameCount / 40f);
		DeformTunnelFanFilter.instance(p).applyTo(p);
//		EdgesFilter.instance(p).applyTo(filterTargetCanvas);
		SphereDistortionFilter.instance(p).applyTo(filterTargetCanvas);
//		ColorDistortionFilter.instance(p).setTime( _timeEaseInc / 5f);
//		ColorDistortionFilter.instance(p).setAmplitude(1.5f + 1.5f * P.sin(radsComplete));
//		ColorDistortionFilter.instance(p).applyTo(filterTargetCanvas);
//		WarperFilter.instance(p).setTime( _timeEaseInc / 5f);
//		WarperFilter.instance(p).applyTo(filterTargetCanvas);
//		OpenGLUtil.setTextureRepeat(_buffer);
//		HalftoneFilter.instance(p).applyTo(filterTargetCanvas);
		CubicLensDistortionFilter.instance(p).setTime( _timeEaseInc);
		CubicLensDistortionFilter.instance(p).applyTo(filterTargetCanvas);

//		ThresholdFilter.instance(p).applyTo(filterTargetCanvas);
//		FXAAFilter.instance(p).applyTo(filterTargetCanvas);
//		EmbossFilter.instance(p).applyTo(filterTargetCanvas);
//		RadialBlurFilter.instance(p).setTime( _timeEaseInc / 5f);
//		RadialBlurFilter.instance(p).applyTo(filterTargetCanvas);
		ChromaColorFilter.instance(p).applyTo(filterTargetCanvas);
		HueFilter.instance(p).setHue(360f * percentComplete);
		HueFilter.instance(p).applyTo(filterTargetCanvas);
		
		image( _buffer, 0, 0);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			boolean forward = MathUtil.randBoolean(p);
			if( forward ) {
				_timeEaser.setTarget(_timeEaser.value() + 3);
			} else {
				_timeEaser.setTarget(_timeEaser.value() + 3);
			}
		}
	}
	
	public void applyTime( PShader shader ) {
//		shader.set( "time", millis() / 1000.0f );
		shader.set( "time", _timeEaseInc );
	}
	
	public void applyResolution( PShader shader ) {
		shader.set("resolution", 1f, (float)(p.width/p.height));
	}
	
	public void applyMouse( PShader shader ) {
		shader.set("mouse", 1f, (float)mouseX/p.width, (float)mouseY/p.height - 0.5f);
	}
	
	// FILTERS ======================================================================
	

	
			
	public static final String FILTER_TEXTURE_TUNNEL = "shaders/textures/to-convert/bw-checker-tunnel.glsl";
	public void updateTestureTunnelFilter( PShader shader ) {
		shader.set("time", _timeEaseInc);
		shader.set("texture", _buffer);
	}
			
	// TEXTURES =====================================================================

	public static final String TEXTURE_BW_EYE_JACKER_01 = "shaders/textures/bw-eye-jacker-01.glsl";
	public void updateBwEyeJacker01( PShader shader ) {
		shader.set("time", millis() / 1000.0f);
		shader.set("mode", 2);
	}

}

