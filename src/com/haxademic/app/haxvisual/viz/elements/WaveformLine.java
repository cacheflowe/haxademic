//package com.haxademic.app.haxvisual.viz.elements;
//
//import processing.core.PApplet;
//import processing.core.PShape;
//import processing.core.PVector;
//import toxi.processing.ToxiclibsSupport;
//
//import com.haxademic.app.haxvisual.viz.ElementBase;
//import com.haxademic.app.haxvisual.viz.IVizElement;
//import com.haxademic.core.app.P;
//import com.haxademic.core.app.PAppletHax;
//import com.haxademic.core.audio.AudioInputWrapper;
//import com.haxademic.core.audio.WaveformData;
//import com.haxademic.core.draw.color.ColorGroup;
//
//public class WaveformLine
//extends ElementBase 
//implements IVizElement {
//	
////	protected WaveformData _waveformData;
//	protected float _width;
//	protected float _amp;
//	protected float _strokeWeight;
//	protected float _spacing;
//	protected PShape _waveShape;
//	
//	public WaveformLine( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
//		super( p, toxi, audioData );
//		init();
//	}
//
//	public void init() {
//		// grab reference to Haxademic waveform data array object and split circle up into number of segments
////		_waveformData = ( (PAppletHax)p )._waveformData;
//		
//		// set some defaults
//		_width = 800;
//		_spacing = _width / P.p.audioData.waveform().length;
//		_amp = 20;
//		_strokeWeight = 1;
//		
//		// create/cache the geometry
//		_waveShape = p.createShape();
//		_waveShape.beginShape();
//		_waveShape.noFill();
//		_waveShape.stroke(255);
//		_waveShape.strokeWeight(4);
//		float startX = -_width/2;
//		for (int i = 0; i < P.p.audioData.waveform().length; i++ ) {			
//			_waveShape.vertex(startX + i * _spacing, 0);
//		}
//		_waveShape.endShape();
//	}
//
//	public void setDrawProps(float strokeWeight, float width, float amp) {
//		_strokeWeight = strokeWeight;
//		_width = width;
//		_amp = amp;
//		_spacing = _width / P.p.audioData.waveform().length;
//		
//		_waveShape.setStrokeWeight( _strokeWeight );
//	}
//	
//	public void setColor( int color ) {
//		_waveShape.setStroke( color );
//	}
//
//	public void update() {
//		float startX = -_width/2;
//		for (int i = 0; i < _waveShape.getVertexCount(); i++) {
//			_waveShape.setVertex( i, startX + i * _spacing,  P.p.audioData.waveform()[i] * _amp );
//		}
//		p.shape( _waveShape );
//	}
//
//	public void reset() {
//		
//	}
//
//	public void dispose() {
////		_waveformData = null;
//	}
//	
//	public void setWaveform( WaveformData waveformData ) {
////		_waveformData = waveformData;
//	}
//
//}
