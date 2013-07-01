package com.haxademic.sketch.is_staff_mtg;

import java.util.Vector;

import krister.Ess.AudioInput;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import com.haxademic.core.audio.AudioInputWrapper;

public class FaceAudio
	extends PApplet
{
	private static final long serialVersionUID = 1L;
	// global vars
	protected int _fps = 30;
	protected AudioInputWrapper _audioInput;
	protected Vector<PImage> _faceBgs;
	protected PImage _faceCurBg;
	protected Vector<PImage> _faceMouths;
	protected PImage _faceCurMouth;
	protected Vector<Integer> _mouthTravels;
	protected float _curMouthTravel;
	protected int _imageIndex = 0;
	protected int _numImages;
	protected Strawberry[] _strawberries;
	protected int NUM_BERRIES = 100;

	public void setup ()
	{
		// set up stage
		size( 800, 600, P3D );				//size(screen.width,screen.height,P3D);
		frameRate( _fps );
		colorMode( PConstants.RGB, 1, 1, 1 );
		background( 0, 0, 0 );
		//lights();
		noStroke();
		frameRate( _fps );
		
		// create image vectors
		_faceBgs = new Vector<PImage>();
		_faceMouths = new Vector<PImage>();
		_mouthTravels = new Vector<Integer>();
		
		// create images collection
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/gene_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/gene_mouth.png") );
		_mouthTravels.add( new Integer( 50 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/matt_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/matt_mouth.png") );
		_mouthTravels.add( new Integer( 20 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/algore_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/algore_mouth.png") );
		_mouthTravels.add( new Integer( 50 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/justin_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/justin_mouth.png") );
		_mouthTravels.add( new Integer( 20 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/fdl_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/fdl_mouth.png") );
		_mouthTravels.add( new Integer( 0 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/ryan_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/ryan_mouth.png") );
		_mouthTravels.add( new Integer( 50 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/parker_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/parker_mouth.png") );
		_mouthTravels.add( new Integer( 35 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/brad_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/brad_mouth.png") );
		_mouthTravels.add( new Integer( 35 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/peter_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/peter_mouth.png") );
		_mouthTravels.add( new Integer( 35 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/sarah_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/sarah_mouth.png") );
		_mouthTravels.add( new Integer( 35 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/parker_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/parker_mouth.png") );
		_mouthTravels.add( new Integer( 35 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/fdl_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/fdl_mouth.png") );
		_mouthTravels.add( new Integer( 0 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/jeremy_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/jeremy_mouth.png") );
		_mouthTravels.add( new Integer( 30 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/ironman_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/ironman_mouth.png") );
		_mouthTravels.add( new Integer( 60 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/kris1_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/kris1_mouth.png") );
		_mouthTravels.add( new Integer( 40 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/shawnwhite_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/shawnwhite_mouth.png") );
		_mouthTravels.add( new Integer( 40 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/kris1_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/kris1_mouth.png") );
		_mouthTravels.add( new Integer( 40 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/fdl_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/fdl_mouth.png") );
		_mouthTravels.add( new Integer( 0 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/don_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/don_mouth.png") );
		_mouthTravels.add( new Integer( 50 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/fdl_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/fdl_mouth.png") );
		_mouthTravels.add( new Integer( 0 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/gene_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/gene_mouth.png") );
		_mouthTravels.add( new Integer( 50 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/bennett_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/bennett_mouth.png") );
		_mouthTravels.add( new Integer( 20 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/kanye_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/kanye_mouth.png") );
		_mouthTravels.add( new Integer( 40 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/gene_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/gene_mouth.png") );
		_mouthTravels.add( new Integer( 50 ) );
		_faceBgs.add(    loadImage("img/is_staff_mtg/final/fdl_bg.png") );
		_faceMouths.add( loadImage("img/is_staff_mtg/final/fdl_mouth.png") );
		_mouthTravels.add( new Integer( 0 ) );
		
		// set initial active
		_faceCurBg = _faceBgs.get( 0 );
		_faceCurMouth = _faceMouths.get( 0 );
		_curMouthTravel = _mouthTravels.get( 0 ).intValue();
		
		// keep track of how many images we're loading
		_numImages = _faceMouths.size();
		
		// set up audio input
		_audioInput = new AudioInputWrapper( this, false );
		_audioInput.setNumAverages( 3 );
		_audioInput.setDampening(.4f);
	}
	
	/**
	 * Auto-initialization of the root class.
	 * @param args
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "--hide-stop", "--bgcolor=000000", "com.haxademic.sketch.is_staff_mtg.FaceAudio" });	//"--present", 
	}

	public void draw() 
	{
		// get volume
		float volume = _audioInput.getFFT().averages[1];
		
		// clear bg
		background(0);
		
		// draw face bg
		image( _faceCurBg, 0, 0, width, height );
		// draw mouth
		image( _faceMouths.get( _imageIndex ), 0, volume * _curMouthTravel, width, height );
		
		// run strawberries
		if( _strawberries != null ) animateBerries();
	}
	
	// handle cycling through images
	public void keyPressed()
	{
		// cycle through images
		if( key == ',' ) _imageIndex--;
		if( _imageIndex < 0 ) _imageIndex = _faceMouths.size() - 1;
		if( key == '.' ) _imageIndex++;
		if( _imageIndex >= _faceMouths.size() ) _imageIndex = 0;
		
		// set new images after cycling
		_faceCurBg = _faceBgs.get( _imageIndex );
		_faceCurMouth = _faceMouths.get( _imageIndex );
		_curMouthTravel = _mouthTravels.get( _imageIndex ).intValue() * 1.6f;
		
		// launch strawberries
		if( key == 's' ) launchStrawberries();
	}
	
	// launches 100 strawberries
	public void launchStrawberries()
	{
		PImage berry = loadImage( "img/is_staff_mtg/final/strawberry.png" );
		
		_strawberries = new Strawberry[ NUM_BERRIES ];
		for( int i = 0; i < NUM_BERRIES; i++ )
			_strawberries[i] = new Strawberry( berry );

	}
	
	// animates the strawberries
	public void animateBerries()
	{
		for( int i = 0; i < NUM_BERRIES; i++ )
			if( _strawberries[i]._y < height ) _strawberries[i].update();
	}
	
	// strawberries
	class Strawberry 
	{
		protected PImage _image;
		protected float _speed;
		protected float _waviness;
		protected float _size;
		public float _x;
		public float _y;

		public Strawberry( PImage berry ) 
		{
			_image = berry;
			_speed = random( 5, 15 );
			_waviness = random( 0.0005f, 0.01f );
			_size = random( 25, 100 );
			_x = random( 0, width );
			_y = random( -550, -100 );
		} 

		public void update() 
		{
			_y += _speed;
			
			pushMatrix();
			image( _image, _x + 15 * sin( _y * _waviness ), _y, _size, _size );
			popMatrix();
			
		}
	}

	
	// PApp-level listener for audio input data ------------------------ 
	public void audioInputData( AudioInput theInput ) {
		_audioInput.getFFT().getSpectrum(theInput);
		_audioInput.detector.detect(theInput);
	}
	
}
