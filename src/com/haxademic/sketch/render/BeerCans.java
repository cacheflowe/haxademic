package com.haxademic.sketch.render;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import com.haxademic.core.render.Renderer;

public class BeerCans
	extends PApplet
{
	private static final long serialVersionUID = 1L;
	// global vars
	protected int _fps = 30;
	protected BeerCanParticle[] _beerCans;
	protected int NUM_CANS = 100;
	protected Renderer _render;

	public void setup ()
	{
		// set up stage
		size( 960, 540, P3D );
		frameRate( _fps );
		colorMode( PConstants.RGB, 1, 1, 1 );
		background( 1 );
		noStroke();
		frameRate( _fps );
		imageMode( PConstants.CENTER );
		
		// launch cans
		launchCans();
		
		// set up renderer
		_render = new Renderer( this, _fps, Renderer.OUTPUT_TYPE_MOVIE, "bin/output/" );
		_render.startRenderer();
	}
	
	/**
	 * Auto-initialization of the root class.
	 * @param args
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "--hide-stop", "--bgcolor=ffffff", "com.haxademic.sketch.media_services_staff_mtg.BeerCans" });
	}

	public void draw() 
	{
		// clear bg
		background(1);
		
		// run cans animation and check to see if we're done
		boolean isFinished = true;
		for( int i = 0; i < NUM_CANS; i++ )
		{
			_beerCans[i].update();
			if( _beerCans[i]._alive ) isFinished = false;
		}
		
		// render movie
		_render.renderFrame();
		
		// quit if we're finished
		if( isFinished )
		{
			_render.stop();
			exit();
		}
	}
	
	// launches 100 beer cans
	public void launchCans()
	{
		PImage beerCanImg = loadImage( "img/media_services_staff_mtg/pabst.png" );
		PImage strawberryImg = loadImage( "img/media_services_staff_mtg/strawberry.png" );
		
		_beerCans = new BeerCanParticle[ NUM_CANS ];
		for( int i = 0; i < NUM_CANS; i++ )
		{
			if( i == round( NUM_CANS / 2 ) )
				_beerCans[i] = new BeerCanParticle( strawberryImg );
			else
				_beerCans[i] = new BeerCanParticle( beerCanImg );
		}
	}
	
	// beer cans
	class BeerCanParticle 
	{
		protected PImage _image;
		protected float _speed;
		protected float _waviness;
		protected float _size;
		public float _x;
		public float _y;
		public float _rot = random( -0.4f, 0.4f );
		public boolean _alive = true;

		public BeerCanParticle( PImage beerCanImage ) 
		{
			_image = beerCanImage;
			_speed = random( 5, 15 );
			_waviness = random( 0.0005f, 0.01f );
			_size = random( 40, 180 );
			_x = random( 0, width );
			_y = random( -550, -100 );
		} 

		public void update() 
		{
			// handle particle life
			if( !_alive ) return;
			else if( _y > height + 200 ) _alive = false; 
			
			
			pushMatrix();
			
			// move forward and draw
			_y += _speed;
			translate( _x + 15 * sin( _y * _waviness ), _y );
			rotate( _rot );
			image( _image, 0, 0, _size, _size );
			
			popMatrix();
			
		}
	}
	
}
