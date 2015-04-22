package com.haxademic.core.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import krister.Ess.AudioInput;
import krister.Ess.Ess;
import krister.Ess.FFT;
import processing.core.PApplet;

public class AudioInputWrapper
{

	public BeatDetect detector;

	PApplet p;
	public int _bufferSize=512;
	float _limitDiff;
	int _numAverages;
	float _myDamp = .13f;
	float _maxLimit, _minLimit;
	FFT _myFFT;
	public AudioInput _myInput;
	int _gain = 0;
	final int GAIN_STEP = 3;

	public int[] beats = { 0, 0, 0, 0 }; 
	public int[] curBeats = new int[4];

	public Boolean _isRendering = false;

	public AudioInputWrapper( PApplet p5, Boolean isRendering )
	{
		p = p5;
		_isRendering = isRendering;
		init();
	}

	public void init() {		
		// start up Ess
		Ess.start( p ); 
		_myInput = new AudioInput( _bufferSize );
		_myFFT = new FFT( _bufferSize * 2 );
		_myFFT.equalizer(true);
		// set default props
		_minLimit = .005f;
		_maxLimit = .05f;
		_myFFT.limits( _minLimit, _maxLimit );
		_myFFT.damp( _myDamp );
		
		setNumAverages( 13 );
		_limitDiff = _maxLimit - _minLimit;

		detector = new BeatDetect(p,_bufferSize,44100);
		detector.detectMode("SOUND_ENERGY");//FREQ_ENERGY

		// TODO: move this into a sketch so audio and renderer are separate
		// listen realtime if not rendering
		// P.println("AudioInputWrapper._isRendering = "+_isRendering);
		if( _isRendering == false ) {
			_myInput.start();
		}
		
		setGain(_gain);
	}

	public void debugInfo() {
		Mixer mixer = AudioSystem.getMixer(null); // default mixer
		try {
			mixer.open();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.printf("Supported SourceDataLines of default mixer (%s):\n\n", mixer.getMixerInfo().getName());
		for(Line.Info info : mixer.getSourceLineInfo()) {
		    if(SourceDataLine.class.isAssignableFrom(info.getLineClass())) {
		        SourceDataLine.Info info2 = (SourceDataLine.Info) info;
		        System.out.println(info2);
		        System.out.printf("  max buffer size: \t%d\n", info2.getMaxBufferSize());
		        System.out.printf("  min buffer size: \t%d\n", info2.getMinBufferSize());
		        AudioFormat[] formats = info2.getFormats();
		        System.out.println("  Supported Audio formats: ");
		        for(AudioFormat format : formats) {
		            System.out.println("    "+format);
//		          System.out.printf("      encoding:           %s\n", format.getEncoding());
//		          System.out.printf("      channels:           %d\n", format.getChannels());
//		          System.out.printf(format.getFrameRate()==-1?"":"      frame rate [1/s]:   %s\n", format.getFrameRate());
//		          System.out.printf("      frame size [bytes]: %d\n", format.getFrameSize());
//		          System.out.printf(format.getSampleRate()==-1?"":"      sample rate [1/s]:  %s\n", format.getSampleRate());
//		          System.out.printf("      sample size [bit]:  %d\n", format.getSampleSizeInBits());
//		          System.out.printf("      big endian:         %b\n", format.isBigEndian());
//		          
//		          Map<String,Object> prop = format.properties();
//		          if(!prop.isEmpty()) {
//		              System.out.println("      Properties: ");
//		              for(Map.Entry<String, Object> entry : prop.entrySet()) {
//		                  System.out.printf("      %s: \t%s\n", entry.getKey(), entry.getValue());
//		              }
//		          }
		        }
		        System.out.println();
		    } else {
		        System.out.println(info.toString());
		    }
		    System.out.println();
		}

		mixer.close();

	}


	public void setNumAverages( int numAvgs )
	{
		_numAverages = numAvgs;
		_myFFT.averages( _numAverages );
	}

	public void setDampening( float damp )
	{
		_myDamp = damp;
		_myFFT.damp( _myDamp );
	}
	
	public void setGain(int gain) {
		_gain = gain;
		_myInput.gain( _gain );
	}
	
	public void gainUp()
	{
		_gain += GAIN_STEP;
		setGain(_gain);
	}
	
	public void gainDown()
	{
		_gain -= GAIN_STEP;
		setGain(_gain);
	}


	public void stop()
	{
		Ess.stop();
	}

	public FFT getFFT() {
		return _myFFT;
	}
	
	public AudioInput getAudioInput() {
		return _myInput;
	}

	public int[] getBeatDetection()
	{
		// store num of beats
		if( detector.isKick() == true ) beats[0]++;
		if( detector.isSnare() == true ) beats[1]++;
		if( detector.isHat() == true ) beats[2]++;
		if( detector.isOnset() == true ) beats[3]++;

		// if new beats, transmit the new beat count
		if( detector.isKick() == true )  curBeats[0] = beats[0]; else curBeats[0] = 0;
		if( detector.isSnare() == true ) curBeats[1] = beats[1]; else curBeats[1] = 0;
		if( detector.isHat() == true )   curBeats[2] = beats[2]; else curBeats[2] = 0;
		if( detector.isOnset() == true ) curBeats[3] = beats[3]; else curBeats[3] = 0;
		return curBeats;
	}
	
	public BeatDetect getDetector()
	{
		return detector;
	}
	
}

