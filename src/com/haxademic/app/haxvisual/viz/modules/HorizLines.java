package com.haxademic.app.haxvisual.viz.modules;

import processing.core.PConstants;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.cameras.CameraBasic;
import com.haxademic.core.cameras.CameraDefault;
import com.haxademic.core.cameras.CameraOscillate;
import com.haxademic.core.cameras.CameraSpotter;
import com.haxademic.core.hardware.midi.MidiWrapper;

public class HorizLines 
extends ModuleBase
implements IVizModule
{
	// class props
	int _numAverages = 32;

	int _curMode;
	final int MODE_DEFAULT = 0;
	float _r, _g, _b;
	float _rows = _numAverages;

	public HorizLines()
	{
		super();
		// store and init audio engine
		initAudio();

		// init viz
		init();
	}

	public void init() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		p.camera();
	}

	public void initAudio()
	{
		_audioData.setNumAverages( _numAverages );
		_audioData.setDampening( .13f );
	}

	public void focus() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		p.rectMode(PConstants.CORNER);
		initAudio();
		p.background( 0 );
		pickNewColors();
		newCamera();
	}

	public void update() {
		if( _curCamera != null ) _curCamera.update();
		
		p.rectMode(PConstants.CORNER);
		
		// draw different per mode
		switch( _curMode ){
		case MODE_DEFAULT :
			
			break;
		default :
			break;
		}

		// _audioData.getFFT().averages[i]
		p.background( 0 );
		p.noStroke();

		// draw our bars
		float cellH = p.height/_rows;
		for(int i=0; i<_rows; i++) {
			// red bottom row
			float curAlpha = 1.6f * _audioData.getFFT().averages[i];
			p.fill( _r, _g, _b, curAlpha );
			p.rect( -500, i*cellH, p.width + 1000, cellH );
		}
	}

	public void handleKeyboardInput()
	{
		if ( p.key == 'c' || p.key == 'C' || p.midi.midiPadIsOn( MidiWrapper.PAD_01 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_01 ) == 1 ) {
			 pickNewColors();
		}
		if ( p.key == 'v' || p.key == 'V' || p.midi.midiPadIsOn( MidiWrapper.PAD_02 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_02 ) == 1 ) {
			newCamera();
		}
		if ( p.key == 'b' || p.key == 'B' || p.midi.midiPadIsOn( MidiWrapper.PAD_03 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_03 ) == 1 ) {
			newBlocks();
		}
		if ( p.key == 'm' || p.key == 'M' || p.midi.midiPadIsOn( MidiWrapper.PAD_04 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_04 ) == 1 ) {
			pickNewColors();
			newCamera();
			newBlocks();
		}
	}

	void pickNewColors()
	{
		_r = p.random( .8f, 1 );
		_g = p.random( .6f, 1 );
		_b = p.random( .6f, 1 );
	}

	void newCamera()
	{
		int randCamera = p.round( p.random( 0, 3 ) );
		if( randCamera == 0 ) _curCamera = new CameraBasic( p, 0, 0, 0 );
		else if( randCamera == 1 ) _curCamera = new CameraOscillate( p, 0, 0, 50, 30 );
		else if( randCamera == 2 ) _curCamera = new CameraSpotter( p, 0, 0, 50 );
		else if( randCamera == 3 ) _curCamera = new CameraDefault( p, 0, 0, 50 );
		if( _curCamera != null ) _curCamera.reset();
	}
	
	void newBlocks()
	{
		_rows = _numAverages = p.round( p.random(20, 100) );
		_audioData.setNumAverages( _numAverages );
	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
	{

	}

}