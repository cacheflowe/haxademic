package com.haxademic.core.media.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.system.JavaInfo;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.KeyEvent;

public class AudioIn {
	
	public enum AudioInputLibrary {
		Beads,
		ESS,
		Minim,
		Processing,
	}
	
	// static arrays for singleton-style usage
	public static IAudioInput audioInput;
	public static float[] frequencies;
	public static float[] waveform;
	
	/////////////////////////////
	// static instance & initializer for quick & easy access
	/////////////////////////////
	
	public static AudioIn instance;
	
	public static AudioIn instance(AudioInputLibrary lib) {
		if(instance != null) return instance;
		instance = new AudioIn(lib, P.p);
		return instance;
	}
	
	public static AudioIn instance(AudioInputLibrary lib, PApplet p) {
		if(instance != null) return instance;
		instance = new AudioIn(lib, p);
		return instance;
	}
	
	public static AudioIn instance(IAudioInput input) {
		if(instance != null) return instance;
		instance = new AudioIn(input, P.p);
		return instance;
	}
	
	public static AudioIn instance(IAudioInput input, PApplet p) {
		if(instance != null) return instance;
		instance = new AudioIn(input, p);
		return instance;
	}
	
	public static AudioIn instance() {
		if(instance != null) return instance;
		instance = new AudioIn();
		return instance;
	}
	
	/////////////////////////////
	// normal initialization
	/////////////////////////////
	
	public AudioIn() {
		this(AudioInputLibrary.ESS);
	}
	
	public AudioIn(AudioInputLibrary lib) {
		this(initAudioInput(lib), P.p);
	}
	
	public AudioIn(AudioInputLibrary lib, PApplet p) {
		this(initAudioInput(lib), p);
	}
	
	public AudioIn(IAudioInput input) {
		this(input, P.p);
	}
	
	public AudioIn(IAudioInput input, PApplet p) {
		AudioIn.audioInput = input;
		AudioIn.audioInput.update();	// force a build of the internal AudioStreamData object
		AudioIn.frequencies = AudioIn.audioInput.audioData().frequencies;
		AudioIn.waveform = AudioIn.audioInput.audioData().waveform;

		// subscribe for auto draw() updates
		p.registerMethod(PRegisterableMethods.pre, this);
		p.registerMethod(PRegisterableMethods.keyEvent, this);
	}
	
	// audio object factory
	
	protected static IAudioInput initAudioInput(AudioInputLibrary lib) {
		switch (lib) {
			case Beads:
				return new AudioInputBeads();
			case ESS:
				return new AudioInputESS();
			case Minim:
				return new AudioInputMinim();
			case Processing:
				return new AudioInputProcessing();
		}
		return null;
	}
	
	/////////////////////////////
	// public interface
	/////////////////////////////
	
	// getters
	
	public IAudioInput audioInput() {
		return audioInput;
	}
	
	// static getters
	
	public static float audioFreq(int index) {
		return audioFreqMod(index, frequencies.length);
	}
		
	public static float audioFreqMod(int index, int mod) {
		return frequencies[index % mod];
	}

	public static float amplitude() {
		return audioInput.audioData().amp();
	}
	
	public static boolean isBeat() {
		return audioInput.audioData().isBeat();
	}
	
	public static void drawDebugBuffer() {
		audioInput.drawDebugBuffer();
	}
	
	public static void drawDataBuffers() {
		audioInput.drawDataBuffers();
	}
	
	public static void drawBufferFFT() {
		audioInput.audioData().drawBufferFFT();
	}
	
	public static void drawBufferWaveform() {
		audioInput.audioData().drawBufferWaveform();
	}

	public static PGraphics bufferDebug() {
		return audioInput.audioData().debugBuffer;
	}

	public static PGraphics bufferFFT() {
		return audioInput.audioData().bufferFFT;
	}
	
	public static PGraphics bufferWaveform() {
		return audioInput.audioData().bufferWaveform;
	}
	
	// debug
	
	public static void debugAudio() {
		JavaInfo.printAudioInfo();
	}
	
	public static void setDrawDebugBuffers() {
	}

	public static void drawDebugBuffers() {
	}

	/////////////////////////////
	// update
	/////////////////////////////
	
	public void pre() {
		// update data & data buffers
		audioInput.update();
		audioInput.drawDataBuffers();
		
		// store static props for global audio input
		AudioIn.frequencies = AudioIn.audioInput.audioData().frequencies;
		AudioIn.waveform = AudioIn.audioInput.audioData().waveform;
		
		// update debug buffer if DebugView is showing
		boolean shouldDrawDebug = (P.isHaxApp() && DebugView.active() == true);
		if(shouldDrawDebug) {
			audioInput.drawDebugBuffer();
			// show main debug view in debug panel
			DebugView.setTexture("AudioIn.bufferDebug", AudioIn.bufferDebug());
			// if fft/waveform data is being drawn, show in debug panel. currently it's always drawn for the main/singular AudioIn object
			DebugView.setTexture("AudioIn.bufferFFT", AudioIn.bufferFFT());
			DebugView.setTexture("AudioIn.bufferWaveform", AudioIn.bufferWaveform());
		}
	}

	/////////////////////////////
	// input
	/////////////////////////////
	
	public void keyEvent(KeyEvent e) {
		if(e.getAction() == KeyEvent.PRESS) {
			if(e.getKey() == ',') {
				audioInput.audioData().setGain(audioInput.audioData().gain() - 0.05f);
				DebugView.setValue("audioData.gain()", audioInput.audioData().gain());
			} else if(e.getKey() == '.') {
				audioInput.audioData().setGain(audioInput.audioData().gain() + 0.05f);
				DebugView.setValue("audioData.gain()", audioInput.audioData().gain());
			}
		}
	}

}
