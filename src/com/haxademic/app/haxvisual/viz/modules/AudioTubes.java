package com.haxademic.app.haxvisual.viz.modules;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.cameras.CameraDefault;
import com.haxademic.core.hardware.midi.MidiWrapper;

import processing.core.PApplet;
import processing.core.PConstants;

public class AudioTubes 
extends ModuleBase
implements IVizModule
{
	private int _numAverages = 50;

	int _curMode;
	final int MODE_DEFAULT = 0;
	float _r, _g, _b;

	float bgBlue = .1f;
	int num_blades = _numAverages;
	Tube[] _blades;
	float _dirX;
	float _dirY;
	int _bgMode = 0;

	public AudioTubes()
	{
		super();

		initAudio();

		// init viz
		init();
	}
	
	public void init()
	{
		// init basic app settings
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		_curCamera = new CameraDefault(p, 0, 0, 0);
		p.translate(0, 0);
		p.background( 0, 0, 0 );
		// create objects
		_blades = new Tube[ num_blades ];
		for(int i = 0; i < num_blades; i++)
		{
			_blades[i] = new Tube(i);   
		}

		newMode();
		newBgMode();
	}

	public void initAudio()
	{
		_audioData.setNumAverages( _numAverages );
		_audioData.setDampening( .12f );
	}

	public void focus() {
		p.camera();
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		initAudio();
		_curCamera = new CameraDefault(p, 0, 0, 0);
		p.translate(0, 0);
		p.background(0);
		p.background(0,0,0,1);
		newBgMode();
	}

	public void update()
	{
		// draw bg
		p.rectMode( PConstants.CORNER );
		switch( _bgMode ){
		case 0 : 
			//p.background(0);
			//break;
		case 1 : 
			p.fill( 0, 0, 0, .01f );
			p.rect( -200, 0, p.width + 400, p.height );
			break;
		case 2 :
			p.fill( 0, 0, 0, .25f );
			p.rect( -200, 0, p.width + 400, p.height );
			break;
		case 3 :
			p.fill( 0, 0, 0, .2f );
			p.rect( -200, 0, p.width + 400, p.height );
			break;
		case 4 :
			// do nothing
			break;
		default :
			break;
		}
		p.rectMode( PConstants.CENTER );
		
		// move tubes
		for(int i = 0; i < num_blades; i++)
		{
			_blades[i].update();   
		}

	}

	public void handleKeyboardInput()
	{
		if (p.key == 'c' || p.key == 'C' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_01 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_01 ) == 1 ) {
			 pickNewColors();
		}
		if (p.key == 'm' || p.key == 'M' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_04 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_04 ) == 1 ) {
			newMode();
		}
		if (p.key == 'b' || p.key == 'B' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_03 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_03 ) == 1 ) {
			newBgMode();
		}
		if (p.key == 'v' || p.key == 'V' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_02 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_02 ) == 1 ) {
			newDirection();
		}
	}

	//  p.PIck new p.random colors
	public void  pickNewColors()
	{
		for(int i = 0; i < num_blades; i++)
		{
			_blades[i].newColors();   
		}
	}

	public void newMode()
	{
		_curMode = PApplet.round( p.random( 0, 4 ) );
		newDirection();
	}

	public void newBgMode()
	{
		_bgMode = PApplet.round( p.random( 0, 4 ) );
	}

	public void newDirection()
	{
		_dirX = p.random( -4, 4 );
		_dirY = p.random( -4, 4 );
	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
	{
//		if( p.getIsAutopilot() == true )
//		{
//			if( (int)(isKickCount % 30) == 0 && isKickCount != 0 ) newDirection();
//			if( (int)(isKickCount % 120) == 0 && isKickCount != 0 ) newBgMode();
//			if( (int)(isSnareCount % 10) == 0 && isSnareCount != 0 ) pickNewColors();
//		}
	}

	public class Tube {
		// A cell object knows about its location in the grid as well as its size with the variables x,y,w,h.
		public PAppletHax p;
		public int grassIndex;
		public float x;
		public float y;
		public float curLength;
		public float xVelocity;
		public float yVelocity;
		public float waveIncrementer;
		public float waveIncrementerAmount;
		public float curSize;
		public float sizeVelocity;
		public float curRed;
		public float redVelocity;
		public float curGreen;
		public float greenVelocity;
		public float curBlue;
		public float blueVelocity;
		public float bgBlue = .2f;

		// Cell Constructor
		Tube( int index ) {
			p = P.p;
			grassIndex = index;

			reset();
			x = p.random(0, p.width);
			y = p.random(0, p.height);
		} 

		// Oscillation means increase angle
		public void update() {
			// use some perlin noise with the sin for some natural waviness
			//			float curY = y + p.noise(waveIncrementer*.2f) * 20 + PApplet.sin(waveIncrementer) * 10;
			y += _dirY * yVelocity + _audioData.getFFT().averages[grassIndex] * 5 * p.getFpsFactor();
			x += _dirX * xVelocity + _audioData.getFFT().averages[grassIndex] * 5 * p.getFpsFactor();

			// increment properties
			curLength -= xVelocity;
			curSize -= sizeVelocity;
			curSize = _audioData.getFFT().averages[grassIndex] * 200;
			if(curSize > 100)
			{
				curSize = 10; 
			}

			//draw the shape
			p.stroke(0,0,0,.2f);
			p.fill(curRed,curGreen,curBlue, .75f);
			p.ellipse(x,y,curSize,curSize);

			// make gradient
			curRed -= redVelocity;
			curGreen -= greenVelocity;
			curBlue -= blueVelocity;

			waveIncrementer += waveIncrementerAmount;

			// start next blade
			if( x > p.width + 50 && _dirX > 0 ) reset();
			if( y > p.height + 50 && _dirY > 0 ) reset();
			if( x < -50 && _dirX < 0 ) reset();
			if( y < -50 && _dirY < 0 ) reset();
		}

		public void reset(){
			// place grass at bottom
			if( x > p.width + 50 && _dirX > 0 ) x = -50;
			else if( x < -50 && _dirX < 0 ) x = p.width + 50;
			if( y > p.height + 50 && _dirY > 0 ) y = -50;
			else if( y < -50 && _dirY < 0 ) y = p.height + 50;
			curLength = 0;
			xVelocity = p.random(.2f,2.7f);
			yVelocity = p.random(.2f,2.7f);
			curSize = 20;
			sizeVelocity = p.random(.2f, .4f);
			waveIncrementer = p.random( 0, 2* p.PI );
			waveIncrementerAmount = p.random( .001f, .2f );


			newColors();
		}

		public void newColors()
		{
			// draw different per mode
			switch( _curMode ){
			case MODE_DEFAULT :
				// reset colors
				curBlue = p.random(.3f, .7f);
				curRed = p.random(.0f, .6f);
				curGreen = curRed;
				blueVelocity = p.random( .00001f, .0001f );
				redVelocity = p.random( .00001f, .0001f );
				greenVelocity = redVelocity;
				break;
			case 1 :
				// reset colors
				curBlue = p.random(.0f, .5f);
				curRed = p.random(.0f, .6f);
				curGreen = curRed;
				blueVelocity = p.random( .00001f, .0001f );
				redVelocity = p.random( .00001f, .0001f );
				greenVelocity = redVelocity;
				break;
			case 2 :
				// reset colors
				curBlue = p.random(.6f, .9f);
				curGreen = p.random(.1f, .6f);
				curRed = curBlue;
				blueVelocity = p.random( .00001f, .0001f );
				greenVelocity = p.random( .00001f, .0001f );
				redVelocity = blueVelocity;
				break;
			case 3 :
				// reset colors
				curRed = p.random(.2f, .6f);
				curGreen = p.random(.2f, .5f);
				curBlue = p.random(.1f, .2f);
				redVelocity = p.random( .00001f, .0001f );
				greenVelocity = p.random( .00001f, .0001f );
				blueVelocity = redVelocity;
				break;
			case 4 :
				// reset colors
				curRed = p.random(0, 1);
				curGreen = p.random(0f, .5f);
				curBlue = p.random(.1f, .6f);
				redVelocity = p.random( .001f, .01f );
				greenVelocity = p.random( .001f, .01f );
				blueVelocity = redVelocity;
				break;
			default :
				break;
			}
			
			curBlue += 0.5f;
			curRed += 0.5f;
			curGreen += 0.5f;

		}

	}
}
