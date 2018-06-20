package com.haxademic.sketch.three_d;

import com.haxademic.app.haxvisual.viz.elements.RotatorShape;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.ColorHax;
import com.haxademic.core.draw.context.DrawUtil;

import processing.core.PConstants;
import toxi.processing.ToxiclibsSupport;

public class RotationObjects 
extends PAppletHax
{
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	ToxiclibsSupport toxi;
	
	RotatorShape _rotator;
	RotatorShape _rotatorBG;
	ColorHax _color;
	ColorHax _colorBG;
	
	int _numRotations = 6;
	float[] _fakeAudioData;
	
	public void setupFirstFrame ()
	{
		p.noStroke();
		p.smooth();
		toxi = new ToxiclibsSupport( p );
		
//		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		
		_rotator = new RotatorShape( p, toxi, _numRotations );
		_rotatorBG = new RotatorShape( p, toxi, _numRotations );
		_color = new ColorHax( 255, 255, 155, 255 );
		_colorBG = new ColorHax( 255, 155, 255, 255 );
		_fakeAudioData = new float[ _numRotations ];
		
		for( int i = 0; i < _numRotations; i++ ) {
			_fakeAudioData[ i ] = p.random(255);
		}

		_rotator.updateColorSet( new ColorGroup( ColorGroup.BALLET ) );
		_rotatorBG.updateColorSet( new ColorGroup( ColorGroup.BALLET ) );

		// set up renderer
//		_render = new Renderer( this, 30, Renderer.OUTPUT_TYPE_MOVIE );
//		_render.startRenderer();
	}

	public void drawApp() 
	{
		
		p.rectMode(PConstants.CENTER);
		p.noStroke();
		p.background(0,0,0,255);
		DrawUtil.setBetterLights(p);
		p.translate(p.width/2, p.height/2);
		
//		p.fill( _r, _g, _b, 0.1f );
		p.pushMatrix();
		p.translate( 0, 0, -600 );
//		p.stroke( 255, 0.5f );
//		p.strokeWeight(2);
//		_rotator.updateColor( _color );
//		_rotator.updateEQArray( _fakeAudioData );
		_rotator.update();
		p.popMatrix();

		
		p.pushMatrix();
		p.translate( 0, 0, -900 );
//		p.stroke( 255, 0.5f );
//		p.strokeWeight(1);
//		_rotatorBG.updateColor( _colorBG );
//		_rotatorBG.updateEQArray( _fakeAudioData );
		_rotatorBG.update();
		p.popMatrix();
	}
}
