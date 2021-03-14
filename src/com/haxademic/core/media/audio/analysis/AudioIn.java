package com.haxademic.core.media.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.system.JavaInfo;

import processing.core.PGraphics;
import processing.event.KeyEvent;

public class AudioIn {
	
	public enum AudioInputLibrary {
		Beads,
		ESS,
		Minim,
		Processing,
	}
	protected PGraphics audioInputDebugBuffer;
	
	// static arrays for singleton-style usage
	public static IAudioInput audioInput;
	public static float[] frequencies;
	public static float[] waveform;
	
	// extra optional features
	protected boolean drawsFFT;
	protected PGraphics bufferFFT;
	protected boolean drawsWaveform;
	protected PGraphics bufferWaveform;
	
	/////////////////////////////
	// static instance & initializer for quick & easy access
	/////////////////////////////
	
	public static AudioIn instance;
	
	public static AudioIn instance(AudioInputLibrary lib) {
		if(instance != null) return instance;
		instance = new AudioIn(lib);
		return instance;
	}
	
	public static AudioIn instance(IAudioInput input) {
		if(instance != null) return instance;
		instance = new AudioIn(input);
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
		this(initAudioInput(lib));
	}
	
	public AudioIn(IAudioInput input) {
		AudioIn.audioInput = input;
		AudioIn.audioInput.update(null);	// force a build of the internal AudioStreamData object
		AudioIn.frequencies = AudioIn.audioInput.audioData().frequencies;
		AudioIn.waveform = AudioIn.audioInput.audioData().waveform;

		// build debug buffer
		audioInputDebugBuffer = PG.newPG((int) AudioStreamData.debugW, (int) AudioStreamData.debugH);
		DebugView.setTexture("Audio Input", audioInputDebugBuffer);
		
		// subscribe for auto draw() updates
		P.p.registerMethod(PRegisterableMethods.pre, this);
		P.p.registerMethod(PRegisterableMethods.keyEvent, this);
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
	
	public void drawBufferFFT(boolean drawsFFT) {
		this.drawsFFT = drawsFFT;
		if(this.drawsFFT && bufferFFT == null) {
			bufferFFT = PG.newPG32(AudioIn.frequencies.length, 8, false, false);
			DebugView.setTexture("AudioIn.bufferFFT", bufferFFT);
		}
	}
	
	public void drawBufferWaveform(boolean drawsWaveform) {
		this.drawsWaveform = drawsWaveform;
		if(this.drawsWaveform && bufferWaveform == null) {
			bufferWaveform = PG.newPG32(AudioIn.waveform.length, 8, false, false);
			DebugView.setTexture("AudioIn.bufferWaveform", bufferWaveform);
		}

	}
	
	public IAudioInput audioInput() {
		return audioInput;
	}
	
	public PGraphics audioInputDebugBuffer() {
		return audioInputDebugBuffer;
	}
	
	// static getters
	
	public static float audioFreq(int index) {
		return audioFreqMod(index, frequencies.length);
	}
		
	public static float audioFreqMod(int index, int mod) {
		return frequencies[index % mod];
	}

	public static boolean isBeat() {
		return audioInput.audioData().isBeat();
	}
	
	// debug
	
	public static void debugAudio() {
		JavaInfo.printAudioInfo();
	}

	/////////////////////////////
	// update
	/////////////////////////////
	
	public void pre() {
		updateAudioData();
		AudioIn.frequencies = AudioIn.audioInput.audioData().frequencies;
		AudioIn.waveform = AudioIn.audioInput.audioData().waveform;
		drawBuffers();
	}
	
	protected void updateAudioData() {
		// only draw if debugging
		PGraphics audioBuffer = (P.isHaxApp() && DebugView.active() == true) ? audioInputDebugBuffer : null;
		// set up context
		if(audioBuffer != null) {
			audioBuffer.beginDraw();
			audioBuffer.background(0);
		}
		// update actual audio data and draw if we have a buffer passed in
		audioInput.update(audioBuffer);
//		audioData = audioInput.audioData();
		// close context
		if(audioBuffer != null) audioBuffer.endDraw();
	}

	protected void drawBuffers() {
		if(drawsFFT) {
	    	bufferFFT.beginDraw();
	    	bufferFFT.background(0);
	    	bufferFFT.noStroke();
	    	for (int i = 0; i < AudioIn.frequencies.length; i++) {
	    		bufferFFT.fill(255f * AudioIn.frequencies[i] * 1f, 255);
	    		bufferFFT.rect(i, 0, 1, bufferFFT.height);
	    	}
	    	bufferFFT.endDraw();
	    	
	    	// re-draw with only a lower portion of the FFT spectrum... upper range is generally useless
	    	bufferFFT.copy(0, 0, 100, bufferFFT.height, 0, 0, bufferFFT.width, bufferFFT.height);
		}
		if(drawsWaveform) {
	        bufferWaveform.beginDraw();
	        bufferWaveform.background(0);
	        bufferWaveform.noStroke();
	        for (int i = 0; i < AudioIn.waveform.length; i++) {
	            bufferWaveform.fill(127 + 127f * AudioIn.waveform[i] * 10f);
	            bufferWaveform.rect(i, 0, 1, bufferWaveform.height); // bufferWaveform.height
	        }
	        bufferWaveform.endDraw();
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
