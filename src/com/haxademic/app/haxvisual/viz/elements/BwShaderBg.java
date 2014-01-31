package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

public class BwShaderBg
extends ElementBase 
implements IVizElement  {
	
	protected PGraphics _image;
	protected PShader _patternShader;
	protected PShader _vignette;
	protected PShader _brightness;
	protected int _timingFrame = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 15);
	protected EasingFloat _brightEaser = new EasingFloat(0, 10);
	protected int _mode = 0;

	public BwShaderBg( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		_image = p.createGraphics( p.width/2, p.height/2, P.P2D );
		loadShaders();
		updateShaders();
	}
	
	protected void loadShaders() {
		_patternShader = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/textures/bw-eye-jacker-01.glsl" ); 
		_patternShader.set("time", _timeEaser.value() );
		_patternShader.set("mode", _mode);

		_vignette = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

		_brightness = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_brightness.set("brightness", _brightEaser.value() );
	}

	protected void updateShaders() {
		_patternShader.set("time", _timeEaser.value() );
		_patternShader.set("mode", _mode);

		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

		_brightness.set("brightness", _brightEaser.value() );
	}

	public void setDrawProps(float strokeWeight, float width, float amp) {

	}
	
	public void update() {
		DrawUtil.resetGlobalProps(p);
		DrawUtil.setCenterScreen(p);
		DrawUtil.setDrawCenter(p);
		DrawUtil.setPImageAlpha(p, 1);
		p.resetMatrix();
		
		_timeEaser.update();
		_brightEaser.update();
		loadShaders();		
//		updateShaders();
		_image.filter( _patternShader );
		_image.filter( _brightness );
		_image.filter( _vignette );
		_image.filter( _vignette );

		p.pushMatrix();
		p.translate(0, 0, -2000);
		p.scale(5.0f);
		p.rotateX(P.PI);
		p.image(_image, 0, 0);
		p.popMatrix();
		
	}
		
	public void reset() {
	}

	public void dispose() {
	}

	public void updateLineMode() {
//		_kaleido.set("sides", MathUtil.randRange(1, 2));
	}

	public void updateCamera() {
		// TODO Auto-generated method stub
	}
	
	public void updateTiming() {
		if( _timingFrame % 4 == 0 ) {
			_brightEaser.setCurrent(1.1f);
			_timeEaser.setTarget( _timeEaser.value() + 4 );
		} else {
			_brightEaser.setCurrent(0.8f);
			_timeEaser.setTarget( _timeEaser.value() + 1 );
		}
		_brightEaser.setTarget(0.25f);
		_timingFrame++;
	}
	
	public void updateSection() {
		_timingFrame = 0;
		_mode++;
		if(_mode >= 3) _mode = 0;
//		_patternShader.set("mode", _mode);
	}

}
