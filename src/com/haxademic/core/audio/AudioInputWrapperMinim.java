package com.haxademic.core.audio;

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
	
	float spectrumDampened[];
	float spectrumAvgDampened[];
	float spectrumDb[];
	float dampening = 0.75f;
	int _averages = 32;

	protected float _gain = 1;
	final float GAIN_STEP = 1f;
	
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
		_fft.linAverages( _averages );
		spectrumAvgDampened = new float[_averages];
		spectrumDampened = new float[_audioInput.bufferSize()];
		spectrumDb = new float[_audioInput.bufferSize()];
		for(int i = 0; i < _fft.specSize(); i++) {
			spectrumDampened[i] = 0;
			spectrumDb[i] = 0;
		}

		windowName = "Rectangular Window";

		// a beat detection object song SOUND_ENERGY mode with a sensitivity of 300 milliseconds
		_beatDetection = new BeatDetect();
//		_beatDetection.detectMode(BeatDetect.FREQ_ENERGY);
		_beatDetection.setSensitivity(300);

		// TODO: move this into a sketch so audio and renderer are separate
		// listen realtime if not rendering
		// P.println("AudioInputWrapper._isRendering = "+_isRendering);
		if( _isRendering == false ) {
			// _myInput.start();
		}

		_gain = _audioInput.getGain();
		setGain(_gain);
	}
	
	public void update() {
		_fft.forward( _audioInput.mix );
		_beatDetection.detect( _audioInput.mix );
		_isBeat = ( _beatDetection.isOnset() == true ) ? true : false;
		
		float timeSize = _fft.timeSize() * ( _fft.timeSize() / p._fps );
		
		  // calculate levels
//		float volMax = _audioInput.mix.level();  
		for(int i = 0; i < _fft.specSize(); i++) {
			spectrumDampened[i] = ( spectrumDampened[i] < _fft.getBand(i) ) ? _fft.getBand(i) : spectrumDampened[i] * dampening;
//			spectrumDampened[i] = P.constrain( spectrumDampened[i] / volMax, 0, 1 );
			spectrumDb[i] = 10 - -1f * P.log( 1 * spectrumDampened[i] / timeSize );
		}
		for(int i = 0; i < _fft.avgSize(); i++) {
			spectrumAvgDampened[i] = ( spectrumAvgDampened[i] < _fft.getAvg(i) ) ? _fft.getAvg(i) : spectrumAvgDampened[i] * dampening;
		}
		// String monitoringState = _audioInput.isMonitoring() ? "enabled" : "disabled";
	}

	public void setGain( float gain ) {
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
//		 return 1 * _fft.getBand( index % _audioInput.bufferSize() );
//		 return _gain * spectrumDb[ index % _audioInput.bufferSize() ];
		return 1f * spectrumDampened[ index % _audioInput.bufferSize() ];
	}

	public float getEqAvgBand( int index ) {
		return 1 * spectrumAvgDampened[ index % _averages ];
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
