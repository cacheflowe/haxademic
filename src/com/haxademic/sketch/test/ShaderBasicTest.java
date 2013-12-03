package com.haxademic.sketch.test;

import processing.core.PGraphics;
import processing.opengl.PShader;

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
		
		_textureShaderFile = FileUtil.getHaxademicDataPath() + "shaders/textures/firey-spiral.glsl";
		_filterShaderFile = FileUtil.getHaxademicDataPath() + FILTER_BLUR_VERT;
		
		_buffer = createGraphics( width,  height, P2D );
		_textureShader = p.loadShader( _textureShaderFile );
		_filterShader = p.loadShader( _filterShaderFile );
	}

	public void draw() {
		background(0);
	
		_timeEaser.update();
		_timeEaseInc = _timeEaser.value();

		_textureShader = loadShader( _textureShaderFile );	// this sucks but will be fixed in the next version of processing.
		applyTime( _textureShader );
		// updateBwEyeJacker01(_textureShader);
		// applyResolution( _textureShader );
		// applyMouse( _textureShader );
		
		_filterShader = p.loadShader( _filterShaderFile );	// this sucks but will be fixed in the next version of processing.
		updateBlurVertFilter( _filterShader );
		
		_buffer.filter( _textureShader );
		
		image( _buffer,  0,  0 );
		p.filter( _filterShader );
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
	
	public static final String FILTER_BLUR_HORIZ = "shaders/filters/blur-horizontal.glsl";
	public void updateBlurHorizFilter( PShader shader ) {
		shader.set( "h", 1f/p.width );
	}
	
	public static final String FILTER_BLUR_VERT = "shaders/filters/blur-vertical.glsl";
	public void updateBlurVertFilter( PShader shader ) {
		shader.set( "v", 1f/p.height );
	}
	
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

