package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class WaveformShapes 
extends ElementBase 
implements IVizElement {
	
	protected WaveformLine _wave;
	protected WaveformCircle _waveCircle;
	protected TColor _baseColor;
	protected ColorGroup _curColors;
	protected EasingFloat _baseWaveCircleRadius = new EasingFloat( 50f, 50f );
	protected EasingFloat _baseWaveLineSpacing = new EasingFloat( 20f, 20f );
	protected boolean _fgMode = true;
	protected float _rotation = 0;
	protected float _rotationTarget = 0;
	
	protected final int MODE_CIRCLES = 0;
	protected final int MODE_LINES = 1;
	protected float _drawMode = MODE_CIRCLES;


	public WaveformShapes( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}
	
	public void init() {
		_wave = new WaveformLine( p, toxi, _audioData );
		_waveCircle = new WaveformCircle( p, toxi, _audioData );
		reset();
	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_baseColor.alpha = 0.85f;
		_curColors = colors;
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		
		_baseWaveCircleRadius.update();
		_baseWaveLineSpacing.update();
		
		float zDepth = 400;
		p.translate(0, 0, -zDepth);
		
		p.stroke( _baseColor.toARGB() );
		p.noFill();
		p.pushMatrix();
//		p.rotateX(10);
		
		if( _drawMode == MODE_CIRCLES ) {
			// draw circles
			float curRadius = _baseWaveCircleRadius.value();
			float _strokeWidth = 3;
			for(int i=0; i < 10; i++) {
				p.stroke( _curColors.getColorFromIndex(i % 4).toARGB() );

				_waveCircle.setDrawProps(_strokeWidth, curRadius, 25f);
				curRadius += _baseWaveCircleRadius.value();
				_strokeWidth -= 0.3;
				_waveCircle.update();
			}
		} else if( _drawMode == MODE_LINES ) {
			float curSpacing = _baseWaveLineSpacing.value();
			float _strokeWidth = 3;
			for(int i=0; i < 10; i++) {
				_wave.setColor( _curColors.getColorFromIndex(i % 4).toARGB() );
				p.pushMatrix();
				p.translate(0, -curSpacing, 0);
				_wave.setDrawProps(_strokeWidth, p.width + zDepth, 20);
				_wave.update();
				p.translate(0, curSpacing * 2, 0);
				_wave.setDrawProps(_strokeWidth, p.width + zDepth, 20);
				_wave.update();
				_strokeWidth -= 0.3;
				curSpacing += _baseWaveLineSpacing.value();
				p.popMatrix();
			}
		}
		p.popMatrix();
	}
	
	public void reset() {
		_baseWaveCircleRadius.setTarget( p.random( p.width/50, p.width/10 ) );
		_baseWaveLineSpacing.setTarget( p.random( p.height/65, p.height/15 ) );
//		_drawMode = ( p.random( 0f, 4 ) > 3 ) ? 0 : 1;
		_drawMode = ( MathUtil.randBoolean( p ) == true ) ? 0 : 1;
	}
	
	public void dispose() {
		super.dispose();
	}
	
}