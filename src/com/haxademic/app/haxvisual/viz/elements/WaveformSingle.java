package com.haxademic.app.haxvisual.viz.elements;

import java.util.ArrayList;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.audio.WaveformData;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.easing.EasingFloat3d;

public class WaveformSingle
extends ElementBase 
implements IVizElement {
	
	protected WaveformLine _wave;
	protected WaveformData _waveformData;
	protected ArrayList<WaveformData> _waveformDataHistory;
	protected TColor _baseColor;
	protected ColorGroup _curColors;
	protected EasingFloat3d _rotation = new EasingFloat3d( 0, 0, 0, 5f );
	protected int NUM_LINES = 3;
	
	protected final float _ninteyDeg = P.PI / 2f;


	public WaveformSingle( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
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
		reset();
	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_baseColor.alpha = 0.9f;
		_curColors = colors;
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		
		// update audio buffers
		_waveformDataHistory.get( 0 ).copyFromOtherWaveformData( _waveformData, _waveformData._waveform.length );
		_waveformDataHistory.add( _waveformDataHistory.remove( 0 ) );
		
		p.pushMatrix();

		p.translate(0, 0, -400);
		
		// apply base easing rotation
		_rotation.update();
		p.rotateY( _rotation.x() );
		p.rotateX( _rotation.y() );
		p.rotateZ( _rotation.z() );
		
		// set initial darw color to fade out
		p.stroke( _baseColor.toARGB() );
		p.noFill();
		p.pushMatrix();
//		p.rotateX(10);
		
		TColor lineColor = new TColor( TColor.WHITE );

		float curSpacing = 5f;
		float _strokeWidth = 3;
		for(int i=0; i < NUM_LINES; i++) {
			// _curColors.getColorFromIndex(i % 1).toARGB()

			// set waveform history on drawing object
			_wave.setWaveform( _waveformDataHistory.get(NUM_LINES - i - 1) );
			_wave.setColor( lineColor.toARGB() );
			
			// set stroke width and color
			float strokeWidth = 3f * ((float)NUM_LINES - (float)i)/(float)NUM_LINES;
			_strokeWidth = strokeWidth;

			float alpha = ((float)NUM_LINES - (float)i)/(float)NUM_LINES;
			lineColor.alpha = ( alpha >= 0 ) ? alpha : 0;


			p.pushMatrix();

			p.translate(0, -curSpacing, 0);
			_wave.setDrawProps(_strokeWidth, p.width/2 + p.width, 35f * (NUM_LINES - i)/NUM_LINES);
			_wave.update();

			p.popMatrix();

			// increment distance from center
			curSpacing += 4;
		}
		p.popMatrix();
		p.popMatrix();
	}
	
	public void updateCamera() {
		// random 45 degree angles
//		_rotation.setTargetX( _ninteyDeg * MathUtil.randRange( 0, 4 ) );
//		_rotation.setTargetY( _ninteyDeg * MathUtil.randRange( 0, 4 ) );
//		_rotation.setTargetZ( _ninteyDeg * MathUtil.randRange( 0, 4 ) );
	}
	
	public void updateLineMode() {
	
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