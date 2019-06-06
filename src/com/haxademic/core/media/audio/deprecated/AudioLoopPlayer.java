package com.haxademic.core.media.audio.deprecated;

import com.haxademic.core.app.P;

import ddf.minim.AudioMetaData;
import ddf.minim.AudioOutput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import ddf.minim.effects.LowPassSP;
import ddf.minim.signals.SquareWave;
import processing.core.PApplet;

public class AudioLoopPlayer {

	protected PApplet p;
	
	// audio
	protected Minim _minim;
	protected AudioPlayer _audioPlayer;
	protected AudioMetaData _meta;
	protected FFT _fft;
	protected AudioOutput _out;
	protected SquareWave _square;
	protected LowPassSP _lowpass;
	

	
	public AudioLoopPlayer( PApplet p5 ) {
		p = p5;
		setupAudioObjects();
	}
	
	public void setupAudioObjects(){
		_minim = new Minim(p);
//		loadAudioFile();
//		initAudioAnalysisForAudioFile();
//		generateMinimAudio();

	}
	
	public void loadAudioFile() {
//		// groove.mp3 would be in the sketches data folder
		_audioPlayer = _minim.loadFile("wav/JackSplash.wav", 512);
		_meta = _audioPlayer.getMetaData();
		P.println("File Name: " + _meta.fileName());
		P.println("Length (in milliseconds): " + _meta.length());
		P.println("Title: " + _meta.title());
		P.println("Author: " + _meta.author());
		P.println("Album: " + _meta.album());
		P.println("Date: " + _meta.date());
		P.println("Comment: " + _meta.comment());
		P.println("Track: " + _meta.track());
		P.println("Genre: " + _meta.genre());
		P.println("Copyright: " + _meta.copyright());
		P.println("Disc: " + _meta.disc());
		P.println("Composer: " + _meta.composer());
		P.println("Orchestra: " + _meta.orchestra());
		P.println("Publisher: " + _meta.publisher());
		P.println("Encoded: " + _meta.encoded());
		_audioPlayer.play();

	}
	
	public void initAudioAnalysisForAudioFile() {
		_fft = new FFT(_audioPlayer.bufferSize(), _audioPlayer.sampleRate());
	}
	
	protected void drawMinimAudio() {
		// first perform a forward fft on one of song's buffers
		// I'm using the mix buffer
		// but you can use any one you like
		_fft.forward(_audioPlayer.mix);

		p.stroke(255, 0, 0, 128);
		// draw the spectrum as a series of vertical lines
		// I multiple the value of getBand by 4
		// so that we can see the lines better
		for(int i = 0; i < _fft.specSize(); i++)
		{
			p.line(i, p.height, i, p.height - _fft.getBand(i)*4);
		}
		// we draw the waveform by connecting neighbor values with a line
		// we multiply each of the values by 50
		// because the values in the buffers are normalized
		// this means that they have values between -1 and 1.
		// If we donÕt scale them up our waveform
		// will look more or less like a straight line.
//		PG.setCenter( p );
		p.stroke( 255 );
		p.strokeWeight( 10 );
		for(int i = 0; i < _audioPlayer.bufferSize() - 1; i++)
		{
			p.line(i, 50 + _audioPlayer.left.get(i)*50, i+1, 50 + _audioPlayer.left.get(i+1)*50);
			p.line(i, 150 + _audioPlayer.right.get(i)*50, i+1, 150 + _audioPlayer.right.get(i+1)*50);
		}
	}
	

	
	protected void generateMinimAudio(){
		// get a stereo line out with a sample buffer of 512 samples
		_out = _minim.getLineOut(Minim.STEREO, 2048);

		// create a SquareWave with a frequency of 440 Hz,
		// an amplitude of 1 and the same sample rate as out
		_square = new SquareWave(440, 0.2f, _out.sampleRate());

		// create a LowPassSP filter with a cutoff frequency of 200 Hz
		// that expects audio with the same sample rate as out
//		lowpass = new LowPassSP(200, 44100);

		// now we can attach the square wave and the filter to our output
		_out.addSignal(_square);
//		out.addEffect(lowpass);
	}
	
	public void drawSynthOut() {
		if( _out == null ) return;
		for(int i = 0; i < _out.bufferSize() - 1; i++)
		{
			p.line(i, 50 + _out.left.get(i)*50, i+1, 50 + _out.left.get(i+1)*50);
			p.line(i, 150 + _out.right.get(i)*50, i+1, 150 + _out.right.get(i+1)*50);
		}
	}
}
