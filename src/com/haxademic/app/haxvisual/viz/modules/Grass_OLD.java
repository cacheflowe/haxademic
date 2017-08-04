package com.haxademic.app.haxvisual.viz.modules;

import processing.core.PConstants;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.hardware.midi.MidiWrapper;

public class Grass_OLD 
extends ModuleBase
implements IVizModule
{
	// class props
	int _numAverages = 12;

	int _curMode;
	final int MODE_GRASS = 0;
	final int MODE_SPIRAL = 1;
	final int MODE_SPIRAL2 = 2;
	final int MODE_CEILING = 3;
	float _r, _g, _b;

	float bgColor = 0;
	final int NUM_BLADES = _numAverages;
	GrassBlade[] blades;
//	RotatorShape _rotator;
	
	public Grass_OLD( )
	{
		super();
		// store and init audio engine
		initAudio();

		// init viz
		init();
	}

	public void init() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );

		blades = new GrassBlade[NUM_BLADES];
		for(int i = 0; i < NUM_BLADES; i++)
		{
			blades[i] = new GrassBlade(i);   
		}
		
//		_rotator = new RotatorShape( NUM_BLADES );
	}

	public void initAudio()
	{
		_audioData.setNumAverages( _numAverages );
		_audioData.setDampening( .13f );
	}

	public void focus() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		p.camera();
		initAudio();
		pickNewColors();
	}

	public void update() {
		p.rectMode(PConstants.CENTER);
		p.noStroke();
		p.background(0,0,0,1);

		// draw graphics
		for(int i = 0; i < NUM_BLADES; i++)
			blades[i].update( _audioData.getFFT().averages[i] * 2 );   
		
		
		p.translate(p.width/2, p.height/2);
		p.fill( 1, 0.1f );
		p.stroke( 1, 0.5f );
		p.strokeWeight(2);
//		_rotator.updateEQArray( _audioData.getFFT().averages );
//		_rotator.update();

		// lets us use the keyboard to funk it up
		if( p.keyPressed ) handleKeyboardInput();
		
		p.noStroke();
	}

	public void handleKeyboardInput()
	{
		if ( p.key == 'm' || p.key == 'M' || p.midi.midiPadIsOn( MidiWrapper.PAD_04 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_04 ) == 1 ) {
			pickMode();
		}
		if ( p.key == 'c' || p.key == 'C' || p.midi.midiPadIsOn( MidiWrapper.PAD_01 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_01 ) == 1 ) {
			pickNewColors();
		}
	}

	//  pick new p.random colors
	void  pickNewColors()
	{
		_r = p.random( 0.5f, 0.8f );
		_g = p.random( 0.8f, 1.0f );
		_b = p.random( 0.5f, 0.8f );
		_r += 0.2f;
		_g += 0.2f;
		_b += 0.2f;
		
		for(int i = 0; i < NUM_BLADES; i++)
			blades[i].setBaseColors(_r,_g,_b);
	}
	
	void pickMode()
	{
		_curMode = p.round( p.random( 0, 3 ) );
		
		for(int i = 0; i < NUM_BLADES; i++)
		{
			blades[i].setMode( _curMode );
		}
		
//		_rotator.reset();
	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
	{

	}

	class GrassBlade {
		// common props
		int bladeIndex;
		int drawMode;
		float originX;
		float originY;
		float curSize;
		float bladeShrinkSpeed;
		// color props
		float baseRed;
		float baseGreen;
		float baseBlue;
		float curRed;
		float redVelocity;
		float curGreen;
		float greenVelocity;
		float curBlue;
		float blueVelocity;
		// grass props
		float waveIncrementerBase = 0;
		float waveIncrementer;
		float waveIncrementerAmount;
		// spiral props
		float constantRotationBase = 0;

		// Cell Constructor
		GrassBlade(int index) {
			bladeIndex = index;
			setMode( MODE_GRASS );
			resetVars();
		}

		// Oscillation means increase angle
		void update( float amplitude ) 
		{
			switch( drawMode )
			{
				case MODE_GRASS :
					drawGrassBlade( amplitude );
					break;
				case MODE_SPIRAL :
				case MODE_SPIRAL2 :
					drawSpiral( amplitude );
					break;
				case MODE_CEILING :
					drawCeilingSpike( amplitude );
					break;
			}
			
		}
		
		void resetVars()
		{
			switch( drawMode )
			{
				case MODE_GRASS :
					resetBladeDrawing();
					break;
				case MODE_SPIRAL :
				case MODE_SPIRAL2 :
					resetSpiralDrawing();
					break;
				case MODE_CEILING :
					resetCeilingDrawing();
					break;
			}
		}
		
		void drawGrassBlade( float amplitude )
		{
			float inc = 0;  // used to start not wiggling at bottom, but increase wiggling towards top
			while(curSize > 0)	// only draw till the blade's tip is gone
			{
				// waviness is tied to amplitude
				float curX = originX + ( p.cos( waveIncrementerBase + waveIncrementer ) * (amplitude * 4) ) * inc;
				originY -= amplitude * 10;
				waveIncrementer += waveIncrementerAmount;

				//draw the shape
				p.fill( curRed, curGreen, curBlue );
				p.ellipse( curX, originY, curSize, curSize );

				// draw up
				curSize -= bladeShrinkSpeed;

				// fade from light -> dark green
				if( curGreen > bgColor ) curGreen -= greenVelocity;
				if( curRed > 0 ) curRed -= redVelocity;
				if( curBlue > 0 ) curBlue -= blueVelocity;

				inc += .2;
			}

			waveIncrementerBase += .5 * ( amplitude * 7 ) ;

			resetBladeDrawing();
			resetColors();
		}

		void resetBladeDrawing(){
			// place grass at bottom
			originX = ( p.width / NUM_BLADES ) * bladeIndex + ( p.width / NUM_BLADES )*.57f;
			originY = p.height + 10;

			// reset blade-drawing drawing vars
			curSize = 20;
			bladeShrinkSpeed = .15f;
			waveIncrementerBase += .001;
			waveIncrementer =  p.PI;//random( 0, 2* p.PI );
			waveIncrementerAmount = .05f;//random( .002, .05 );
		}
		
		void drawSpiral( float amplitude )
		{
			float spiralTwistInc = constantRotationBase + ( (p.PI * 2) / NUM_BLADES ) * bladeIndex;
			float curRadius = 0;
			float curX = p.width / 2;
			float curY = p.height / 2;
			
			if( _curMode == MODE_SPIRAL )
			{
				while( curSize > 0 )	// only draw till the blade's tip is gone
				{
					// waviness is tied to amplitude
					curX = originX + p.sin( spiralTwistInc ) * curRadius;//(amplitude * 400);
					curY = originY + p.cos( spiralTwistInc ) * curRadius;//(amplitude * 400);
	
					//draw the shape
					p.fill( curRed, curGreen, curBlue );
					p.ellipse( curX, curY, curSize, curSize );
	
					// rotate and shrink
					spiralTwistInc += .3f * amplitude * p.sin(constantRotationBase/5);
					curSize -= bladeShrinkSpeed;
					
					// grow radius
					curRadius += amplitude * 5;
					
					// fade from light -> dark green
					if( curGreen > bgColor ) curGreen -= greenVelocity;
					if( curRed > 0 ) curRed -= redVelocity;
					if( curBlue > 0 ) curBlue -= blueVelocity;
				}
			}
			else if( _curMode == MODE_SPIRAL2 )
			{
				while( curSize > 0 && curX > 0 && curX < p.width )	// only draw till the blade's tip is gone
				{
					// waviness is tied to amplitude
					curX = originX + p.sin( spiralTwistInc ) * curRadius;//(amplitude * 400);
					curY = originY + p.cos( spiralTwistInc ) * curRadius;//(amplitude * 400);
	
					//draw the shape
					p.fill( curRed, curGreen, curBlue );
					p.ellipse( curX, curY, curSize, curSize );
	
					// rotate and shrink
					spiralTwistInc += .3f * amplitude * p.sin(constantRotationBase/5);
					curSize -= bladeShrinkSpeed * 10;
					
					// grow radius
					curRadius += 5 + amplitude * 5;
					
					// fade from light -> dark green
					if( curGreen > bgColor ) curGreen -= greenVelocity * 2;
					if( curRed > 0 ) curRed -= redVelocity * 2;
					if( curBlue > 0 ) curBlue -= blueVelocity * 2;
				}
			}
			

			resetSpiralDrawing();
			resetColors();
		}

		void resetSpiralDrawing(){
			// place grass at bottom
			originX = p.width / 2;
			originY = p.height / 2;

			// reset blade-drawing drawing vars
			if( _curMode == MODE_SPIRAL )
			{
				curSize = p.random(15,30);
			}
			if( _curMode == MODE_SPIRAL2 )
			{
				curSize = p.random(70,100);
			}
			bladeShrinkSpeed = .1f;
			constantRotationBase -= .01;
		}
		
		void drawCeilingSpike( float amplitude )
		{
			float inc = 0;  // used to start not wiggling at bottom, but increase wiggling towards top
			while(curSize > 0)	// only draw till the blade's tip is gone
			{
				// waviness is tied to amplitude
				float curX = originX;// + ( p.cos( waveIncrementerBase + waveIncrementer ) * (amplitude * 4) ) * inc;
				originY += amplitude * 10;
				waveIncrementer += waveIncrementerAmount;

				//draw the shape
				p.fill( curRed, curGreen, curBlue );
				p.ellipse( curX, originY, curSize, curSize );

				// draw up
				curSize -= bladeShrinkSpeed;

				// fade from light -> dark green
				if( curGreen > bgColor ) curGreen -= greenVelocity;
				if( curRed > bgColor ) curRed -= redVelocity;
				if( curBlue > bgColor ) curBlue -= blueVelocity;

				inc += .2;
			}

			waveIncrementerBase += .5 * ( amplitude * 7 ) ;

			resetCeilingDrawing();
			resetColors();
		}

		void resetCeilingDrawing(){
			// place grass at bottom
			originX = ( p.width / NUM_BLADES ) * bladeIndex + ( p.width / NUM_BLADES )*.57f;
			originY = -10;

			// reset blade-drawing drawing vars
			curSize = 40;
			bladeShrinkSpeed = .4f;
			waveIncrementerBase += .001;
			waveIncrementer =  p.PI;//random( 0, 2* p.PI );
			waveIncrementerAmount = .05f;//random( .002, .05 );
		}
		
		void resetColors(){
			// reset colors 
			curGreen = baseGreen;
			greenVelocity = .01f;
			curRed = baseRed;
			redVelocity = greenVelocity * p.random( 0.5f, 1.5f );
			curBlue = baseBlue;
			blueVelocity = greenVelocity * p.random( 0.5f, 1.5f );
		}
		
		public void setMode( int mode )
		{
			drawMode = mode;
			resetVars();
			//p.background(0,0,0,1);
			//update(0);
		}
		
		public void setBaseColors( float r, float g, float b )
		{
			baseRed = r;
			baseGreen = g;
			baseBlue = b;
			
			resetColors();
		}
	}
	
}