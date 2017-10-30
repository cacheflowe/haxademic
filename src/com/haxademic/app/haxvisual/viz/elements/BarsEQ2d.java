package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import processing.core.PConstants;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;

public class BarsEQ2d
extends ElementBase 
implements IVizElement {
	
	protected float _amp;
	
	protected float _cols = 32;
	protected TColor _baseColor = null;
	protected TColor _fillColor = null;


	public BarsEQ2d( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		// set some defaults
	}
	
	public void setDrawProps(float width, float height) {

	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_fillColor = _baseColor.copy();
		_fillColor.alpha = 0.2f;
	}
	
	public void update() {
		DrawUtil.resetGlobalProps( p );
//		DrawUtil.setTopLeft( p );
		p.pushMatrix();
		
		p.noStroke();
		p.fill( 0 );
		
		p.rectMode(PConstants.CENTER);

		// draw bars
		p.pushMatrix();
		drawBars();
		p.popMatrix();

		p.pushMatrix();
		p.translate( 0, p.height );
		p.rotateX( (float) Math.PI );
//		p.rotateY( (float) Math.PI );
		drawBars();
		p.popMatrix();
		
		p.popMatrix();
	}

	public void drawBars() {
		// draw bars
		float halfH = p.height * 0.5f;
		float halfW = p.width * 0.5f;
		float cellW = (float)p.width/(float)_cols;
		float cellX = 0;
		float cellH = p.height/6f;
		int spectrumInterval = (int) ( 128f / _cols );	// 128 keeps it in the bottom quarter of the spectrum since the high ends is so overrun
		
		p.beginShape();
		p.vertex( cellX, -halfH );
		for (int i = 0; i < _cols; i++) {
			float eqAmp = _audioData.getFFT().spectrum[i*spectrumInterval] * cellH;
			p.vertex( cellX, eqAmp );
			cellX += cellW;
		}		
		p.vertex( cellX, 0 );
		p.vertex( cellX, -halfH );
		p.vertex( -halfW, -halfH );
		p.endShape(P.CLOSE);
	}

	public void reset() {
		
	}

	public void dispose() {
		_audioData = null;
	}
	
}
