package com.haxademic.app.haxvisual.viz.modules;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.app.P;
import com.haxademic.core.camera.CameraBasic;
import com.haxademic.core.camera.CameraDefault;
import com.haxademic.core.camera.CameraOscillate;
import com.haxademic.core.camera.CameraSpotter;

import processing.core.PConstants;

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
//		audioData.setNumAverages( _numAverages );
//		audioData.setDampening( .13f );
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
			float curAlpha = 1.6f * P.p.audioFreq(i);
			p.fill( _r, _g, _b, curAlpha );
			p.rect( -500, i*cellH, p.width + 1000, cellH );
		}
	}

	public void handleKeyboardInput()
	{
		if ( p.key == 'c' || p.key == 'C') {
			 pickNewColors();
		}
		if ( p.key == 'v' || p.key == 'V') {
			newCamera();
		}
		if ( p.key == 'b' || p.key == 'B') {
			newBlocks();
		}
		if ( p.key == 'm' || p.key == 'M') {
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
//		audioData.setNumAverages( _numAverages );
	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
	{

	}

}