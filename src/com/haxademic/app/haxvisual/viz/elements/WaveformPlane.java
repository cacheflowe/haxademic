package com.haxademic.app.haxvisual.viz.elements;

import java.util.ArrayList;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.audio.WaveformData;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.TColorBlendBetween;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.EasingFloat3d;

public class WaveformPlane 
extends ElementBase 
implements IVizElement {
	
	protected WaveformLine _wave;
	protected WaveformData _waveformData;
	protected ArrayList<WaveformData> _waveformDataHistory;
	protected ColorGroup _curColors;
	protected TColorBlendBetween _color;
	protected EasingFloat _baseWaveLineSpacing = new EasingFloat( 5f, 5f );
	protected EasingFloat3d _rotation = new EasingFloat3d( 0, 0, 0, 5f );
	
	protected final int MODE_LINES = 1;
	protected float _drawMode = MODE_LINES;
	
	protected final int NUM_LINES = 10;
	protected final float _ninteyDeg = p.PI / 2f;


	public WaveformPlane( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}
	
	public void init() {
		_wave = new WaveformLine( p, toxi, _audioData );
		_waveformDataHistory = new ArrayList<WaveformData>();
		_waveformData = ( (PAppletHax)p )._waveformData;
		for(int i = 0; i < NUM_LINES; i++) {
			_waveformDataHistory.add( new WaveformData( p, _waveformData._waveform.length ) );
		}
		_color = new TColorBlendBetween( TColor.BLACK.copy(), TColor.BLACK.copy() );
		reset();
	}

	public void updateColorSet( ColorGroup colors ) {
		_color.setColors( TColor.BLACK.copy(), colors.getRandomColor() );
		_curColors = colors;
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		
		// update audio buffers and recycle arraylist spot
		_waveformDataHistory.get( 0 ).copyFromOtherWaveformData( _waveformData, _waveformData._waveform.length );
		_waveformDataHistory.add( _waveformDataHistory.remove( 0 ) );
		
		p.pushMatrix();

		
		float zDepth = 400;
		p.translate(0, 0, -zDepth);
		
		// apply base easing rotation
		_rotation.update();
		p.rotateY( _rotation.x() );
		p.rotateX( _rotation.y() );
		p.rotateZ( _rotation.z() );
		
		// set initial darw color to fade out
		p.pushMatrix();
		
		_baseWaveLineSpacing.update();
		if( _drawMode == MODE_LINES ) {
			float curSpacing = _baseWaveLineSpacing.value();
			float _strokeWidth = 4;
			for(int i=0; i < NUM_LINES; i++) {
				// _curColors.getColorFromIndex(i % 1).toARGB()
				// set color, decreasing to black
				float alpha = 1;//((float)NUM_LINES - (float)i)/(float)NUM_LINES;
//				alpha = ( alpha >= 0 ) ? alpha : 0;
				_wave.setColor( _color.argbWithPercent( alpha ) );
				
				// set waveform history on drawing object
				_wave.setWaveform( _waveformDataHistory.get(NUM_LINES - i - 1) );
				
				// set stroke width and color
				float strokeWidth = 4f * ((float)NUM_LINES - (float)i)/(float)NUM_LINES;
				_strokeWidth = strokeWidth;
				
				p.pushMatrix();
				
				p.translate(0, -curSpacing, 0);
				p.rotateX(_ninteyDeg);
				_wave.setDrawProps(_strokeWidth, p.width/2 + p.width * (i+1)/20, 20f * (NUM_LINES - i)/NUM_LINES);
				_wave.update();
				
				p.popMatrix();
				p.pushMatrix();
				
				p.translate(0, curSpacing, 0);
				p.rotateX(_ninteyDeg);
				_wave.setDrawProps(_strokeWidth, p.width/2 + p.width * (i+1)/20, 20f * (NUM_LINES - i)/NUM_LINES);
				_wave.update();
				
				p.popMatrix();
				
				// increment distance from center
				curSpacing += _baseWaveLineSpacing.value();
			}
		}
		
		p.popMatrix();
		p.popMatrix();
	}
	
	public void updateCamera() {
		// random 45 degree angles
		_rotation.setTargetX( _ninteyDeg * MathUtil.randRange( 0, 3 ) );
		_rotation.setTargetY( _ninteyDeg * MathUtil.randRange( 0, 3 ) );
		_rotation.setTargetZ( _ninteyDeg * MathUtil.randRange( 0, 3 ) );
	}
	
	public void updateLineMode() {
		_baseWaveLineSpacing.setTarget( p.random( p.height/20f, p.height/6f ) );
	}
	
	public void reset() {
//		_drawMode = ( p.random( 0f, 4 ) > 3 ) ? 0 : 1;
//		_drawMode = ( MathUtil.randBoolean( p ) == true ) ? 0 : 1;
		updateLineMode();
		updateCamera();
	}
	
	public void dispose() {
		super.dispose();
	}
	
}