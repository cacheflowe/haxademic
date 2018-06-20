package com.haxademic.app.haxvisual.viz.elements;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

public class BarsEQ
extends ElementBase 
implements IVizElement {
	
	protected float _width;
	protected float _barHeight;
	protected float _amp;
	
	protected float _cols = 32;
	protected TColor _baseColor = null;
	protected TColor _fillColor = null;


	public BarsEQ( PApplet p, ToxiclibsSupport toxi ) {
		super( p, toxi );
		init();
	}

	public void init() {
		// set some defaults
		_width = p.width;
		_barHeight = p.height;
	}
	
	public void setDrawProps(float width, float height) {
		_width = width;
		_barHeight = height;
	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_fillColor = _baseColor.copy();
		_fillColor.alpha = 0.2f;
	}
	
	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();
		
		p.rectMode(PConstants.CORNER);
		p.noStroke();
		
		setDrawProps(p.width, p.height/4);
		p.fill( _fillColor.toARGB() );
		
		// move back
		p.translate( 0, 0, -400 );

		// draw bars
		p.translate( 0, -p.height/2, 0 );
		drawBars();
		p.translate( 0, p.height, 0 );
		p.rotateX( (float)(Math.PI*2) / 2 );
		drawBars();
		
		p.popMatrix();
	}

	public void drawBars() {
		// draw bars
		float cellW = _width/_cols;
		float cellH = _barHeight * 2;
		float startX = -_width/2;
		int spectrumInterval = (int) ( 256 / _cols );	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun
		for (int i = 0; i < _cols; i++) {
			p.rect( startX + i * cellW, 0, cellW, P.p.audioFreq(i*spectrumInterval) * cellH );
		}		
	}

	public void reset() {
		
	}

	public void dispose() {
	}
	
}
