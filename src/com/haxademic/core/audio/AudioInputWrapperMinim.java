package com.haxademic.core.audio;

import processing.core.PApplet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;

public class AudioInputWrapperMinim {

	// new! -----------------------
	protected PAppletHax p;
	protected Minim _minim;
	protected AudioInput _audioInput;
	protected FFT _fft;
	protected String windowName;
	protected BeatDetect _beatDetection;

	protected float _gain = 0;
	final float GAIN_STEP = 0.1f;
	
	public int[] beats = { 0, 0, 0, 0 }; 
	public int[] curBeats = new int[4];

	protected Boolean _isRendering = false;
	protected Boolean _isBeat = false;

	public AudioInputWrapperMinim( PAppletHax p, Boolean isRendering ) {
		this.p = p;
		_isRendering = isRendering;
		init();
	}

	public void init()
	{
		_minim = p.minim;
		_audioInput = _minim.getLineIn();
		
		_fft = new FFT(_audioInput.bufferSize(), _audioInput.sampleRate());
		windowName = "Rectangular Window";

		// a beat detection object song SOUND_ENERGY mode with a sensitivity of 300 milliseconds
		_beatDetection = new BeatDetect();
		_beatDetection.setSensitivity(300);

		// TODO: move this into a sketch so audio and renderer are separate
		// listen realtime if not rendering
		// P.println("AudioInputWrapper._isRendering = "+_isRendering);
		if( _isRendering == false ) {
			// _myInput.start();
		}

		_gain = 1;//_audioInput.getGain();
		setGain(_gain);
	}
	
	public void update() {
		_fft.forward( _audioInput.mix );
		_beatDetection.detect( _audioInput.mix );
		_isBeat = ( _beatDetection.isOnset() == true ) ? true : false;
		// String monitoringState = _audioInput.isMonitoring() ? "enabled" : "disabled";
	}

	public void setGain( float gain ) {
		P.println("_gain",_gain);
		_gain = gain;
//		_audioInput.setGain( _gain );
	}

	public void gainUp() {
		_gain += GAIN_STEP;
		setGain(_gain);
	}

	public void gainDown() {
		_gain -= GAIN_STEP;
		setGain(_gain);
	}

	public void stop() {
	}

	public float getEqBand( int index ) {
		return _gain * _fft.getBand( index % _audioInput.bufferSize() );
	}

	public AudioInput getAudioInput() {
		return _audioInput;
	}

	public boolean isBeat() {
		return _isBeat;
	}
	
	public int bufferSize() {
		return _audioInput.bufferSize();
	}
	
}
