package com.haxademic.app.haxmapper.overlays;

import java.util.ArrayList;

import processing.core.PGraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

public class MeshLines {

	protected ArrayList<MeshLineSegment> _meshLineSegments;
	protected PGraphics _texture;
	protected ColorHaxEasing _colorEase;

	public enum MODE {
		MODE_PERLIN,
		MODE_PROGRESS_BAR,
//		MODE_EQ_BARS, 
		MODE_LINE_EXTEND, 
//		MODE_EQ_TOTAL,
		MODE_EQ_BARS_BLACK,
//		MODE_WAVEFORMS,
		MODE_DOTS,
		MODE_PARTICLES,
		MODE_SEGMENT_SCANNERS,
		MODE_NONE
	}
	protected int NUM_MODES = MODE.values().length;
	protected int _modeIndex;

	public MeshLines( PGraphics pg ) {
		_texture = pg;
		_meshLineSegments = new ArrayList<MeshLineSegment>();
		_colorEase = new ColorHaxEasing( "#ffffff", 5 );
		_modeIndex = 0;
	}

	public PGraphics texture() {
		return _texture;
	}
	
	public MODE mode() {
		return MODE.values()[_modeIndex];
	}

	public ArrayList<MeshLineSegment> meshLineSegments() {
		return _meshLineSegments;
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
			_meshLineSegments.get(i).update( _texture, mode(), _colorEase.colorInt(), P.p.audioIn.getEqAvgBand( 15 ), P.p.audioIn.getEqBand( 20 + P.floor(i*spectrumInterval) ) );
		}

//		_texture.endDraw();
	}
	
	public void updateLineMode() {
		_modeIndex += MathUtil.randRange(1, 2);
		if( _modeIndex >= NUM_MODES ) _modeIndex = 0;
	}

	public void resetLineMode( int index ) {
		_modeIndex = index;
	}

	public void setColor( int color ) {
		_colorEase.setTargetColorInt( color );
	}

}
