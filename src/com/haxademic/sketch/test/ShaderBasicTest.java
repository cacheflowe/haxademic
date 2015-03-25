package com.haxademic.sketch.test;

import processing.core.PGraphics;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class ShaderBasicTest
extends PAppletHax {
	
	protected PGraphics _buffer;
	protected PShader _textureShader;
	protected String _textureShaderFile;
	protected PShader _filterShader;
	protected String _filterShaderFile;
	protected float _timeEaseInc = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 15);

	public void setup() {
		super.setup();
		
		_textureShaderFile = FileUtil.getHaxademicDataPath() + "shaders/textures/basic-checker.glsl";
		_filterShaderFile = FileUtil.getHaxademicDataPath() + FILTER_WOBBLE;
		
		_buffer = createGraphics( width,  height, P2D );
		_textureShader = p.loadShader( _textureShaderFile );
		_filterShader = p.loadShader( _filterShaderFile );
	}

	public void draw() {
		background(0);
	
		_timeEaser.update();
		_timeEaseInc = _timeEaser.value();

//		applyTime( _textureShader );
		// updateBwEyeJacker01(_textureShader);
		// applyResolution( _textureShader );
		// applyMouse( _textureShader );
		
		updateWobbleFilter( _filterShader );
		
		_buffer.filter( _textureShader );
		_buffer.filter( _filterShader );
		image( _buffer, 0, 0);
//		p.filter( _filterShader );
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
	
	public static final String FILTER_BAD_TV = "shaders/filters/badtv.glsl";
	public void updateBadTvFilter( PShader shader ) {
		shader.set("time", millis() / 1000.0f);
		shader.set("grayscale", 0);
		shader.set("nIntensity", 0.75f);
		shader.set("sIntensity", 0.55f);
		shader.set("sCount", 4096.0f);
	}
	
	public static final String FILTER_VIGNETTE = "shaders/filters/vignette.glsl";
	public void updateVignetteFilter( PShader shader ) {
		shader.set("darkness", 0.85f);
		shader.set("spread", 0.15f);
	}
	
	public static final String FILTER_BLUR_HORIZ = "shaders/filters/blur-horizontal.glsl";
	public void updateBlurHorizFilter( PShader shader ) {
		shader.set( "h", 1f/p.width );
	}
	
	public static final String FILTER_BLUR_VERT = "shaders/filters/blur-vertical.glsl";
	public void updateBlurVertFilter( PShader shader ) {
		shader.set( "v", 1f/p.height );
	}
	
	public static final String FILTER_SATURATION = "shaders/filters/saturation.glsl";
	public void updateSaturationFilter( PShader shader ) {
		shader.set( "saturation", 2f );	// 0-2
	}
	
	public static final String FILTER_TEXTURE_TUNNEL = "shaders/textures/to-convert/bw-checker-tunnel.glsl";
	public void updateTestureTunnelFilter( PShader shader ) {
		shader.set("time", _timeEaseInc);
		shader.set("texture", _buffer);
	}
		
	public static final String FILTER_WOBBLE = "shaders/filters/wobble.glsl";
	public void updateWobbleFilter( PShader shader ) {
		shader.set("time", _timeEaseInc * 2f);
		shader.set("speed", 1.0f + (0.5f * P.sin(p.frameCount/90f)));
		shader.set("strength", 0.001f + (0.0005f * P.sin(p.frameCount/80f)));
		shader.set("size", 100f + (50f * P.sin(p.frameCount/70f)));
	}
	
	// TEXTURES =====================================================================

	public static final String TEXTURE_BW_EYE_JACKER_01 = "shaders/textures/bw-eye-jacker-01.glsl";
	public void updateBwEyeJacker01( PShader shader ) {
		shader.set("time", millis() / 1000.0f);
		shader.set("mode", 2);
	}

}

