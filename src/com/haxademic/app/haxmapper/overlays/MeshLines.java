package com.haxademic.app.haxmapper.overlays;

import java.util.ArrayList;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

import com.haxademic.core.app.P;
import com.haxademic.core.audio.WaveformData;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

public class MeshLines {

	protected ArrayList<MeshLineSegment> _meshLineSegments;
	protected PGraphics _texture;
	protected ColorHaxEasing _colorEase;

	protected int _mode = 4;
	protected int _numModes = 7;
	public static final int MODE_EQ_BARS = 0;
	public static final int MODE_LINE_EXTEND = 1;
	public static final int MODE_EQ_TOTAL = 2;
	public static final int MODE_EQ_BARS_BLACK = 3;
	public static final int MODE_WAVEFORMS = 4;
	public static final int MODE_DOTS = 5;
	public static final int MODE_NONE = 6;

	public MeshLines( PGraphics pg ) {
		_texture = pg;
		_meshLineSegments = new ArrayList<MeshLineSegment>();
		_colorEase = new ColorHaxEasing( "#ffffff", 5 );
	}

	public PGraphics texture() {
		return _texture;
	}

	public void addSegment( float x1, float y1, float x2, float y2 ) {
		boolean hasLineAlready = false;
		for( int i=0; i < _meshLineSegments.size(); i++ ) {
			if( _meshLineSegments.get(i).matches( x1, y1, x2, y2 ) == true ) {
				hasLineAlready = true;
			}
		}
		if( hasLineAlready == false ) {
			_meshLineSegments.add( new MeshLineSegment( x1, y1, x2, y2 ) );
		}
	}

	public void update() {
		_colorEase.update();
//		_texture.beginDraw();
//		_texture.clear();
		DrawUtil.setDrawCenter( _texture );

		float spectrumInterval = (int) ( 256 / _meshLineSegments.size() );	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun

		for( int i=0; i < _meshLineSegments.size(); i++ ) {
			_meshLineSegments.get(i).update( _texture, _mode, _colorEase.colorInt(), P.p._audioInput.getFFT().spectrum[10], P.p._audioInput.getFFT().spectrum[P.floor(i*spectrumInterval)] );
		}

//		_texture.endDraw();
	}
	
	public void updateLineMode() {
		_mode++;
		if( _mode >= _numModes ) _mode = 0;
	}

	public void setColor( int color ) {
		_colorEase.setTargetColorInt( color );
	}

}
