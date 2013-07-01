package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.audio.WaveformData;

public class WaveformLine
extends ElementBase 
implements IVizElement {
	
	protected WaveformData _waveformData;
	protected float _width;
	protected float _amp;
	protected float _strokeWeight;
	protected float _spacing;
	
	public WaveformLine( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		// grab reference to Haxademic waveform data array object and split circle up into number of segments
		_waveformData = ( (PAppletHax)p )._waveformData;
		
		// set some defaults
		_width = 800;
		_spacing = _width / _waveformData._waveform.length;
		_amp = 20;
		_strokeWeight = 1;
	}

	public void setDrawProps(float strokeWeight, float width, float amp) {
		_strokeWeight = strokeWeight;
		_width = width;
		_amp = amp;
		_spacing = _width / _waveformData._waveform.length;
	}

	public void update() {
		float startX = -_width/2;
		p.strokeWeight( _strokeWeight );
		p.beginShape();
		for (int i = 0; i < _waveformData._waveform.length; i++ ) {
			float curY = _waveformData._waveform[i] * _amp;
//			float nextY = _waveformData._waveform[i+1] * _amp;
//			p.line( startX + i * _spacing, curY, startX + (i+1) * _spacing, nextY );
			p.vertex(startX + i * _spacing, curY);
		}
		p.endShape();
	}

	public void reset() {
		
	}

	public void dispose() {
		_waveformData = null;
	}
	
	public void setWaveform( WaveformData waveformData ) {
		_waveformData = waveformData;
	}

}
