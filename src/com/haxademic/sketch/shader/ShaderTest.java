package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class ShaderTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected float CURSOR_PIE_DIAMETER = 34;
	protected float CURSOR_RING_DIAMETER = 44;
	protected float CURSOR_OUTER_DIAMETER = 54;
	protected float CURSOR_EASING_FACTOR = 7;
	protected float CURSOR_STROKE = 4;
	
	PGraphics _bg;

	protected PShader invert;
	protected PShader vignette;
	protected PShader kaleido;
	protected PShader edge;
	protected PShader dotScreen;
	protected PShader pixelate;
	protected PShader badtv;
	protected PShader radialBlur;
	protected PShader deformHoles;
	protected PShader deformRelief;
	protected PShader warping;
	
	protected PShader glowwave;
	protected PShader swirl;
	protected PShader coffeeswirl;
	protected PShader clouds;
	protected PShader stars;
	
	protected PImage _image;
	
	protected float _timeEaseInc = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 5);
	protected float _autoTime = 0;
	
	protected void config() {
		Config.setProperty( AppSettings.FILLS_SCREEN, "false" );
		Config.setProperty( AppSettings.WIDTH, "800" );
		Config.setProperty( AppSettings.HEIGHT, "600" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void firstFrame() {
	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );
		
		_bg = p.createGraphics(p.width, p.height, P.P2D);
		_bg.smooth( OpenGLUtil.SMOOTH_HIGH );
		
		invert = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/invert.glsl" ); 
		
		kaleido = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/kaleido.glsl" ); 
		kaleido.set("sides", 6.0f);
		kaleido.set("angle", 0.0f);
		
		vignette = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/vignette.glsl" );
		vignette.set("darkness", 0.85f);
		vignette.set("spread", 0.15f);

		edge = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/edge.glsl" ); 
		
		dotScreen = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/dotscreen.glsl" ); 
		dotScreen.set("tSize", 256f, 256f);
		dotScreen.set("center", 0.5f, 0.5f);
		dotScreen.set("angle", 1.57f);
		dotScreen.set("scale", 1f);

		pixelate = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/pixelate.glsl" ); 
		pixelate.set("divider", p.width/20f, p.height/20f);

		radialBlur = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/radial-blur-iq.glsl" ); 
		radialBlur.set("time", _timeEaseInc );
		radialBlur.set("resolution", 1f, (float)(p.width/p.height));
		
		warping = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/warping-iq.glsl" ); 
		warping.set("time", _timeEaseInc );

		deformHoles = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/deform-holes-iq.glsl" ); 
		deformHoles.set("time", _timeEaseInc );
		deformHoles.set("resolution", 1f, (float)(p.width/p.height));
		deformHoles.set("mouse", 0.5f, 0.5f);

		deformRelief = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/fisheye.glsl" ); 
		deformRelief.set("time", _timeEaseInc );

		badtv = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/badtv.glsl" ); 
		badtv.set("time", p.frameCount * 0.1f);
		badtv.set("grayscale", 0);
		badtv.set("nIntensity", 0.75f);
		badtv.set("sIntensity", 0.55f);
		badtv.set("sCount", 4096.0f);

		_image = p.loadImage( FileUtil.getHaxademicDataPath() + "images/green-screen-2.png" );

		
		glowwave = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/glowwave.glsl" ); 
		swirl = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/swirl.glsl" ); 
		coffeeswirl = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/inversion-iq.glsl" ); 
		clouds = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/clouds-iq.glsl" ); 

		//		glowwave.set("mouse", float(mouseX), float(mouseY));
	}

	public void drawApp() {
		background(0);
		
		PG.setColorForPImage( p );
		PG.resetPImageAlpha( p );
		PG.setPImageAlpha(p, 1f);		
		
		updateTime();
		generateTexture();
		postProcess();
	}
	
	protected void updateTime() {
//		if( p.frameCount % 45 == 0 ) _timeEaser.setTarget(_timeEaser.value() + 10);
		_timeEaser.update();
		_timeEaseInc = _timeEaser.value();
//		_timeInc += _audioInput.getFFT().averages[10] / 5f;
		_autoTime = millis() / 1000.0f;
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') _timeEaser.setTarget(_timeEaser.value() + 10);
	}
	
	protected void generateTexture() {
		_bg.resetShader();
//		_bg.clear();

		glowwave = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/glowwave.glsl" ); 
		glowwave.set("time", _timeEaseInc);
		_bg.filter(glowwave);
		
//		swirl.set("time", _timeInc );
//		swirl.set("resolution", 1f, (float)(p.width/p.height));
//		p.filter(swirl);
		
//		coffeeswirl.set("time", _autoTime);	// _timeInc
//		coffeeswirl.set("resolution", 1f, (float)(p.width/p.height));
//		p.filter(coffeeswirl);

//		clouds.set("time", _timeEaseInc/5f);	// 
//		clouds.set("resolution", 1f, (float)(p.width/p.height));
//		clouds.set("mouse", (float)mouseX/p.width, (float)mouseY/p.height - 0.5f);		
//		p.filter(clouds);
		
//		stars = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/to-convert/star-field.glsl" );
//		stars.set("time", _timeEaseInc);
//		_bg.filter(stars);

		p.image( _bg,  0,  0 );

	}
	
	protected void postProcess() {
//		radialBlur.set("time", _autoTime );
//		radialBlur.set("resolution", 1f, (float)(p.width/p.height));
//		p.filter(radialBlur);

//		deformHoles.set("mouse", (float)mouseX, (float)mouseY);
////		deformHoles.set("mouse", 1f/(float)mouseX, 1f/(float)mouseY);
//		deformHoles.set("time", _autoTime );
//		deformHoles.set("resolution", 1f, (float)(p.width/p.height));
//		p.filter(deformHoles);
		
//		warping.set("time", _timeEaseInc );
//		p.filter(warping);

//		deformRelief.set("time", _autoTime );
//		deformRelief.set("resolution", 1f, (float)(p.width/p.height));
//		p.filter(deformRelief);
		
		
		p.filter(invert);
		p.filter(kaleido);
		p.filter(vignette);
		p.filter(invert);
		
//		edge.set("aspect", 1f/(float)p.width, 1f/(float)p.height);
//		p.filter(edge);

//		kaleido.set("sides", Math.round((float)Math.sin(p.frameCount*0.001f) * 5 + 12));
//		p.filter(kaleido);
//		p.filter(dotScreen);
		
//		p.filter(pixelate);

//		badtv.set("time", p.frameCount * 0.1f);
//		badtv.set("nIntensity", 0.1f);
//		badtv.set("sIntensity", 0.8f);
//		p.filter(badtv);
//		
//		p.filter(vignette);

	}


}
