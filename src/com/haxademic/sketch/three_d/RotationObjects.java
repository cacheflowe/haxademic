package com.haxademic.sketch.three_d;

import processing.core.PApplet;
import processing.core.PConstants;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.elements.RotatorShape;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.ColorHax;
import com.haxademic.core.render.Renderer;

public class RotationObjects 
extends PApplet
{
	ToxiclibsSupport toxi;
	PApplet p;
	
	RotatorShape _rotator;
	RotatorShape _rotatorBG;
	ColorHax _color;
	ColorHax _colorBG;
	
	int _numRotations = 6;
	float[] _fakeAudioData;
	
	Renderer _render;

	public void setup ()
	{
		p = this;
		// set up stage and drawing properties
//		p.size( 800, 600, "hipstersinc.P5Sunflow" );				//size(screen.width,screen.height,P3D);
		p.size( 800, 600, PConstants.OPENGL );				//size(screen.width,screen.height,P3D);
		p.frameRate( 30 );
		p.colorMode( PConstants.RGB, 255, 255, 255, 255 );
		p.background( 0 );
		//p.shininess(1000); 
		p.lights();
		p.noStroke();
		//p.noLoop();
		p.smooth();
		toxi = new ToxiclibsSupport( p );
		
//		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		
		_rotator = new RotatorShape( p, toxi, null, _numRotations );
		_rotatorBG = new RotatorShape( p, toxi, null, _numRotations );
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

	public void draw() 
	{
		
		p.rectMode(PConstants.CENTER);
		p.noStroke();
		p.background(0,0,0,255);

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

		
		 // render movie
		if( _render != null ) {
			_render.renderFrame();
			if( p.frameCount == 300 ) {
				p.println( "done!" );
				_render.stop();
				exit();
			} else {
				for( int i = 0; i < 100; i++ ) p.println( "rendering frame: " + p.frameCount );
			}
		}
	}
}
