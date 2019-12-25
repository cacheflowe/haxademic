package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.BadTVLinesFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeTwoColorsFilter;
import com.haxademic.core.draw.filters.pshader.KaleidoFilter;
import com.haxademic.core.draw.filters.pshader.ReflectFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.opengl.PShader;
import processing.opengl.Texture;

public class ShaderBasicTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics _buffer;
	protected PShader _textureShader;
	protected String _textureShaderFile;
	protected float _timeEaseInc = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 15);
	float _frames = 100;
	protected PShader _postFilter;

	protected void config() {
		Config.setProperty( AppSettings.FILLS_SCREEN, false );
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 600 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(600 + _frames - 1) );
	}

	public void firstFrame() {

		
		_textureShaderFile = FileUtil.getHaxademicDataPath() + "haxademic/shaders/textures/bw-clouds.glsl";
		_textureShader = p.loadShader( _textureShaderFile );
		_postFilter = p.loadShader( FileUtil.getPath("haxademic/shaders/filters/escher-repeat.glsl"));
		
		_buffer = createGraphics( width,  height, P2D );
	}

	public void drawApp() {
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
		
//		ColorCorrectionFilter.instance(p).setBrightness(0.1f * P.cos(radsComplete));
//		ColorCorrectionFilter.instance(p).setContrast(1f + 0.1f * P.sin(radsComplete));
//		ColorCorrectionFilter.instance(p).setGamma(1f + 0.2f * P.sin(radsComplete));
//		ColorCorrectionFilter.instance(p).applyTo(filterTargetCanvas);

//		BrightnessFilter.instance(p).setBrightness(1f + 0.5f * P.sin(radsComplete));
//		BrightnessFilter.instance(p).applyTo(filterTargetCanvas);
//		VignetteFilter.instance(p).setDarkness(-0.75f + 0.25f * P.sin(radsComplete));
//		VignetteFilter.instance(p).applyTo(filterTargetCanvas);
//		BlurHFilter.instance(p).applyTo(filterTargetCanvas);
//		BlurHFilter.instance(p).setBlur(1f / (filterTargetCanvas.width*1.2f + filterTargetCanvas.width * P.cos(radsComplete)));
//		BlurVFilter.instance(p).applyTo(filterTargetCanvas);
//		BlurVFilter.instance(p).setBlur(1f / (filterTargetCanvas.width*1.2f + filterTargetCanvas.width * P.cos(radsComplete)));

//		WobbleFilter.instance(p).setTime( _timeEaseInc * 2f);
//		WobbleFilter.instance(p).setSpeed( 1.0f + 0.5f * P.sin(radsComplete));
//		WobbleFilter.instance(p).setStrength( 0.001f + 0.0005f * P.sin(radsComplete));
//		WobbleFilter.instance(p).setSize( 200f + 25f * P.sin(radsComplete));
//		WobbleFilter.instance(p).applyTo(filterTargetCanvas);
//		KaleidoFilter.instance(p).setAngle(radsComplete);
//		KaleidoFilter.instance(p).setSides(P.round(6 + 2f * P.sin(radsComplete)));
		KaleidoFilter.instance(p).applyTo(filterTargetCanvas);
		ReflectFilter.instance(p).applyTo(filterTargetCanvas);
//		InvertFilter.instance(p).applyTo(filterTargetCanvas);
//		RadialRipplesFilter.instance(p).setTime( _timeEaseInc / 5f);
//		RadialRipplesFilter.instance(p).setAmplitude(0.4f + 0.4f * P.sin(radsComplete));
//		RadialRipplesFilter.instance(p).applyTo(filterTargetCanvas);
//		DeformTunnelFanFilter.instance(p).setTime(p.frameCount / 40f);
//		DeformTunnelFanFilter.instance(p).applyTo(p);
//		SphereDistortionFilter.instance(p).setAmplitude(0.45f + 0.45f * P.sin(radsComplete));
//		SphereDistortionFilter.instance(p).applyTo(filterTargetCanvas);
//		ColorDistortionFilter.instance(p).setTime( _timeEaseInc / 5f);
//		ColorDistortionFilter.instance(p).setAmplitude(1.5f + 1.5f * P.sin(radsComplete));
//		ColorDistortionFilter.instance(p).applyTo(filterTargetCanvas);
//		WarperFilter.instance(p).setTime( _timeEaseInc / 5f);
//		WarperFilter.instance(p).applyTo(filterTargetCanvas);
//		OpenGLUtil.setTextureRepeat(_buffer);
//		HalftoneFilter.instance(p).applyTo(filterTargetCanvas);
//		CubicLensDistortionFilter.instance(p).setTime( _timeEaseInc);
//		CubicLensDistortionFilter.instance(p).applyTo(filterTargetCanvas);

//		ThresholdFilter.instance(p).applyTo(filterTargetCanvas);
//		FXAAFilter.instance(p).applyTo(filterTargetCanvas);
//		EmbossFilter.instance(p).applyTo(filterTargetCanvas);
//		RadialBlurFilter.instance(p).setTime( _timeEaseInc / 5f);
//		RadialBlurFilter.instance(p).applyTo(filterTargetCanvas);
//		ChromaColorFilter.instance(p).applyTo(filterTargetCanvas);
//		SaturationFilter.instance(p).setSaturation(1f + 1f * P.sin(radsComplete));
//		SaturationFilter.instance(p).setSaturation(0);
//		SaturationFilter.instance(p).applyTo(filterTargetCanvas);

//		HueFilter.instance(p).setHue(360f * percentComplete);
//		HueFilter.instance(p).applyTo(filterTargetCanvas);
		BadTVLinesFilter.instance(p).applyTo(filterTargetCanvas);
//		EdgesFilter.instance(p).applyTo(filterTargetCanvas);
//		EdgeColorFadeFilter.instance(p).setSpreadX(0.65f);
//		EdgeColorFadeFilter.instance(p).setSpreadY(0.65f);
//		EdgeColorFadeFilter.instance(p).applyTo(filterTargetCanvas);
//		EdgeColorDarkenFilter.instance(p).setSpreadX(0.3f);
//		EdgeColorDarkenFilter.instance(p).setSpreadY(0.3f);
//		EdgeColorDarkenFilter.instance(p).applyTo(filterTargetCanvas);
//		PixelateFilter.instance(p).setDivider(8f, filterTargetCanvas.width, filterTargetCanvas.height);
//		PixelateFilter.instance(p).applyTo(filterTargetCanvas);
//		ContrastFilter.instance(p).setContrast(1.2f);
//		ContrastFilter.instance(p).applyTo(filterTargetCanvas);

		ColorizeTwoColorsFilter.instance(p).setColor1(1f, 1f, 0f);
		ColorizeTwoColorsFilter.instance(p).setColor2(0f, 1f, 1f);
		ColorizeTwoColorsFilter.instance(p).setCrossfadeMode(0);
		ColorizeTwoColorsFilter.instance(p).applyTo(filterTargetCanvas);
		
		filterTargetCanvas.textureWrap(Texture.REPEAT);
//		RotateFilter.instance(p).setAspect(filterTargetCanvas.width, filterTargetCanvas.height);
		RotateFilter.instance(p).setRotation(p.frameCount * 0.01f);
		RotateFilter.instance(p).setZoom(1f + 0.25f * P.sin(p.frameCount * 0.01f));
		RotateFilter.instance(p).setOffset(0.5f * P.cos(P.PI + p.frameCount * 0.01f), 0.5f * P.sin(p.frameCount * -0.01f));
		RotateFilter.instance(p).applyTo(filterTargetCanvas);

//		filterTargetCanvas.filter(_postFilter);
		
		
		p.image( _buffer, 0, 0);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			boolean forward = MathUtil.randBoolean();
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
	

	
			
	public static final String FILTER_TEXTURE_TUNNEL = "haxademic/shaders/textures/to-convert/bw-checker-tunnel.glsl";
	public void updateTestureTunnelFilter( PShader shader ) {
		shader.set("time", _timeEaseInc);
		shader.set("texture", _buffer);
	}
			
	// TEXTURES =====================================================================

	public static final String TEXTURE_BW_EYE_JACKER_01 = "haxademic/shaders/textures/bw-eye-jacker-01.glsl";
	public void updateBwEyeJacker01( PShader shader ) {
		shader.set("time", millis() / 1000.0f);
		shader.set("mode", 2);
	}

}

