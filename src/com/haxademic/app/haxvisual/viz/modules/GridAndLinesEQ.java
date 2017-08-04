package com.haxademic.app.haxvisual.viz.modules;

import processing.core.PConstants;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.camera.CameraDefault;
import com.haxademic.core.camera.CameraSpotter;
import com.haxademic.core.hardware.midi.MidiWrapper;
import com.haxademic.core.hardware.osc.OscWrapper;

public class GridAndLinesEQ 
extends ModuleBase
implements IVizModule
{
	// class props
	Cell[][] _grid;
	int _numGridQuares = 512;
	int _numAverages = 23;
	float _cols = _numAverages;
	float _rows = _numAverages;
	float _r, _g, _b;

	int _curMode;
	final int MODE_DEFAULT = 0;
	final int MODE_FADE = 1;
	final int MODE_FADE_CHOP = 2;
	protected int NUM_MODES = 3;
	
	int _curShapeMode;
	final int DRAW_MODE_RECT = 0;
	final int DRAW_MODE_BOX = 1;
	final int DRAW_MODE_PYRAMID = 2;
	protected int NUM_DRAW_MODES = 3;
	
	int _hasStroke;

	public GridAndLinesEQ()
	{
		super();
		// store and init audio engine
		initAudio();

		// init viz
		init();
	}

	public void init() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );

		pickNewColors();
		pickMode();

		// create background cell objects
		_grid = new Cell[p.round(_cols)][p.round(_rows)];
		float cellW = p.width/_cols;
		float cellH = p.height/_rows;
		int tmpIndex = 0;
		for (int i = 0; i < _cols; i++) {
			for (int j = 0; j < _rows; j++) {
				// Initialize each object
				_grid[i][j] = new Cell(i*cellW,j*cellH,cellW,cellH,tmpIndex);
				tmpIndex++;
			}
		}
	}

	public void initAudio()
	{
		_audioData.setNumAverages( _numAverages );
		_audioData.setDampening( .13f );
	}

	public void focus() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		p.rectMode( PConstants.CORNER);
		initAudio();
		pickNewColors();
		pickMode();
		newCamera();
	}

	public void update() {
		p.rectMode( PConstants.CORNER);
		
		// draw different backgrounds per mode
		switch( _curMode ){
		case MODE_DEFAULT :
			p.background( _r, _g, _b, 1 );
			break;
		case MODE_FADE :
			p.fill( _r, _g, _b, .1f );
		case MODE_FADE_CHOP :
			p.fill( _r, _g, _b, .2f );
		default :
			break;
		}
		
		p.strokeWeight(1);
		if( _hasStroke == 1 ) p.stroke(.5f);
		else p.noStroke();

		// draw grid  
		for (int i = 0; i < _cols; i++) {
			for (int j = 0; j < _rows; j++) {
				// Oscillate and display each object
				_grid[i][j].update();
			}
		}

		// draw our bars (a little closer to the camera)
		p.pushMatrix();
		p.translate(0,0,20);

		float cellW = p.width/_cols;
		for(int i=0; i<_cols; i++) {
			// red bottom row
			p.fill( _r, _g, _b, 1 );
			p.rect( i * cellW, p.height, cellW, _audioData.getFFT().averages[i] * -500 );
			// green top row
			p.fill( _r, _g, _b, 1 );
			p.rect( i * cellW, 0, cellW, _audioData.getFFT().averages[i] * 500 );
		}

		p.popMatrix();

	}

	public void handleKeyboardInput()
	{
		// clear screen!
		if ( p.key == 'm' || p.key == 'M' || p.midi.midiPadIsOn( MidiWrapper.PAD_04 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_04 ) == 1 || p._oscWrapper.oscMsgIsOn( OscWrapper.MSG_MODE ) == 1 ) {
			pickNewColors();
			pickMode();
			newCamera();
		}
		if ( p.key == 'c' || p.key == 'C' || p.midi.midiPadIsOn( MidiWrapper.PAD_01 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_01 ) == 1 || p._oscWrapper.oscMsgIsOn( OscWrapper.MSG_COLOR ) == 1 ) {
			pickNewColors();
		}
		if ( p.key == 'v' || p.key == 'V' || p.midi.midiPadIsOn( MidiWrapper.PAD_02 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_02 ) == 1 || p._oscWrapper.oscMsgIsOn( OscWrapper.MSG_CAMERA ) == 1 ) {
			newCamera();
		}
		if ( p.key == ' ' || p.midi.midiPadIsOn( MidiWrapper.PAD_07 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_07 ) == 1 ) {
			//p.background( _r, _g, _b );
		}
		if ( p.key == 'l' || p.midi.midiPadIsOn( MidiWrapper.PAD_08 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_08 ) == 1 || p._oscWrapper.oscMsgIsOn( OscWrapper.MSG_LINES ) == 1 ) {
			pickStroke();
		}
		if ( p.key == '1' || p.midi.midiPadIsOn( MidiWrapper.PAD_09 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_09 ) == 1 ) {
			_curMode = MODE_DEFAULT;
		} else if ( p.key == '2' || p.midi.midiPadIsOn( MidiWrapper.PAD_10 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_10 ) == 1 ) {
			_curMode = MODE_FADE;
		} else if ( p.key == '3' || p.midi.midiPadIsOn( MidiWrapper.PAD_11 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_11 ) == 1 ) {
			_curMode = MODE_FADE_CHOP;
		}
	}



	//  p.PIck new p.random colors
	void  pickNewColors()
	{
		_r = p.random( 0, .2f );
		_g = p.random( 0, .2f );
		_b = p.random( 0, .2f );
	}

	//  p.PIck a p.random mode
	void  pickMode()
	{
		_curMode = p.round( p.random( 0, NUM_MODES - 1 ) );
		_curShapeMode = p.round( p.random( 0, NUM_DRAW_MODES - 1 ) );
		pickStroke();
	}
	
	void pickStroke()
	{
		_hasStroke = (int)p.random( 0, 1.47f );
	}

	void newCamera()
	{
		int lastMode = _curMode;
		
		int randCamera = p.round( p.random( 0, 1 ) );
		if( randCamera == 0 )
		{
			_curCamera = new CameraDefault( p, 0, 0, 0 );
			_curMode = MODE_DEFAULT;
		}
		else if( randCamera == 1 )
		{
			_curCamera = new CameraSpotter( p, 0, 0, 0 );
			_curMode = MODE_DEFAULT;
		}
		//_curCamera.reset();
	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
	{

	}

	// A Cell object
	class Cell {
		// A cell object knows about its location in the _grid as well as its size with the variables x,y,w,h.
		float x,y;   // x,y location
		float w,h;   // p.width and p.height
		int index; // angle for oscillating brightness

		// Cell Constructor
		Cell(float tempX, float tempY, float tempW, float tempH, int tempIndex) {
			x = tempX;
			y = tempY;
			w = tempW;
			h = tempH;
			index = tempIndex;
			if(index >= _numGridQuares)
			{
				index = _numGridQuares - 1; 
			}
		} 

		void update() {

			// amount of alpha depends on EQ value
			float alphaVal = _audioData.getFFT().spectrum[index];
			switch( _curMode ){
			case MODE_DEFAULT :
				alphaVal *= 5;
				break;
			case MODE_FADE :
				alphaVal *= 2;
			case MODE_FADE_CHOP :
				alphaVal *= .2;
			default :
				break;
			}
			// draw square
			p.fill(  1, 1, 1, alphaVal * 1 );
			p.rect( x, y, w, h ); 
		}
	}

}
