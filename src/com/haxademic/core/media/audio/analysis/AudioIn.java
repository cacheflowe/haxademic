package com.haxademic.core.media.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.system.JavaInfo;

import processing.core.PGraphics;

public class AudioIn {

	// TODO:
	// - Switch all apps to use singleton to grab audio data. Should this be on P.audio ?
	// - Implement keyboard listener
		/*
		if ( p.key == '.' ) {
			p.audioData.setGain(p.audioData.gain() + 0.05f);
			p.debugView.setValue("audioData.gain()", p.audioData.gain());
		}
		if ( p.key == ',' ) {
			p.audioData.setGain(p.audioData.gain() - 0.05f);
			p.debugView.setValue("audioData.gain()", p.audioData.gain());
		}
		*/
	
	public enum AudioInputLibrary {
		Beads,
		ESS,
		Minim,
		Processing,
	}
	protected IAudioInput audioInput;
	protected AudioStreamData audioData = new AudioStreamData();
	protected PGraphics audioInputDebugBuffer;
	
	/////////////////////////////
	// normal initialization
	/////////////////////////////
	
	public AudioIn() {
		this(AudioInputLibrary.ESS);
	}
	
	public AudioIn(AudioInputLibrary lib) {
		this(initAudioInput(lib));
	}
	
	public AudioIn(IAudioInput audioInput) {
		this.audioInput = audioInput;
		this.audioInput.update(null);	// force a build of the internal AudioStreamData object

		// build debug buffer
		audioInputDebugBuffer = PG.newPG((int) AudioStreamData.debugW, (int) AudioStreamData.debugH);
		P.p.debugView.setTexture("Audio Input", audioInputDebugBuffer);
		
		// subscribe for auto draw() updates
		P.p.registerMethod(PRegisterableMethods.pre, this);
		
		// set on PAppletHax if that's what type of app we are
		if(P.isHaxApp()) P.p.setAudioInput(this.audioInput);
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
	
	public IAudioInput audioInput() {
		return audioInput;
	}
	
	public static void debugAudio() {
		JavaInfo.printAudioInfo();
	}

	/////////////////////////////
	// static instance & initializer for quick & easy access
	/////////////////////////////
	
	public static AudioIn instance;
	
	public static AudioIn instance(AudioInputLibrary lib) {
		if(instance != null) return instance;
		instance = new AudioIn(lib);
		return instance;
	}
	
	public static AudioIn instance() {
		if(instance != null) return instance;
		instance = new AudioIn();
		return instance;
	}
	
	/////////////////////////////
	// update
	/////////////////////////////
	
	public void pre() {
		updateAudioData();
	}
	
	protected void updateAudioData() {
		// only draw if debugging
		PGraphics audioBuffer = (P.isHaxApp() && P.p.debugView.active() == true) ? audioInputDebugBuffer : null;
		// set up context
		if(audioBuffer != null) {
			audioBuffer.beginDraw();
			audioBuffer.background(0);
		}
		// update actual audio data and draw if we have a buffer passed in
		audioInput.update(audioBuffer);
		audioData = audioInput.audioData();
		// close context
		if(audioBuffer != null) audioBuffer.endDraw();
	}


}
