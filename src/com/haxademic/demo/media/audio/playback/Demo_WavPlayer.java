package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioHistoryTexture;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.render.FrameLoop;

public class Demo_WavPlayer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected WavPlayer player;
	protected WavPlayer player2;
	protected String[] oneshots = new String[] {
		"data/audio/kit808/kick.wav",	
		"data/audio/kit808/snare.wav",	
		"data/audio/communichords/cacheflowe/mid-buzz-synth.wav",	
	};
	protected String soundbed = "data/audio/communichords/bass/operator-organ-bass.aif";
	
	public AudioHistoryTexture history;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
			// make sure we're selecting the proper audio device
		AudioUtil.printMixerInfo();
		AudioUtil.setPrimaryMixer();
		
		// build WavPlayer objects, and have them share an AudioContext.
		// this ensures that audio analysis can be done on the shared context's output
		player = new WavPlayer();
		player2 = new WavPlayer(WavPlayer.sharedContext());
//		player2 = new WavPlayer(WavPlayer.newAudioContext());

		// send Beads audio player analyzer to PAppletHax.
		// this automatically writes audio data to the global 
		AudioIn.instance(new AudioInputBeads(player.context()));
		AudioIn.drawBufferFFT();
		AudioIn.drawBufferWaveform();

		// audio history texture. used for shader effects
		history = new AudioHistoryTexture();
		
		// add audio texture to DebugView
		// DebugView.setTexture("bufferDebug", AudioIn.bufferDebug());
		DebugView.setTexture("bufferWaveform", AudioIn.bufferWaveform());
		DebugView.setTexture("bufferFFT", AudioIn.bufferFFT());
		DebugView.setTexture("historyFFT", history.textureFFT());
		DebugView.setTexture("historyWaveform", history.textureWaveform());
	}
	
	protected void drawApp() {
		p.background(0);
		player.setVolume(soundbed, Mouse.xNorm);
		
		DebugView.setValue("AudioContext :: numinputs", player.activeConnections());
		
		if(Mouse.yNorm > 0.9f) autoPlay();
		history.updateFFT();
		history.updateWaveform();
	}
	
	protected void autoPlay() {
		if(FrameLoop.frameModLooped(30)) {
			if(FrameLoop.frameModLooped(60)) {
				player.playWav(oneshots[0], 1, WavPlayer.PAN_CENTER, false, 0, 0, 0, 0, 0);
			} else {
				player.playWav(oneshots[0], 1, WavPlayer.PAN_CENTER, false, 0, 0, 0, 0, 0);
				player.playWav(oneshots[1], 1, WavPlayer.PAN_CENTER, false, 0, 0, 0, 0, 0);
			}
		}
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') player.playWav(oneshots[0], 1, WavPlayer.PAN_CENTER, false, MathUtil.randRange(-10, 10), 0, 0, 0, 0);
		if(p.key == '2') player2.playWav(oneshots[1], 1, WavPlayer.PAN_RIGHT, false, 0, 0, 0, 0, MathUtil.randRangeDecimal(0, 100));
		if(p.key == '3') player.playWav(oneshots[1], 1, WavPlayer.PAN_LEFT, false, MathUtil.randRange(-10, 10), MathUtil.randRange(0, 500), 0, 0, 0);
		if(p.key == '4') player.playWav(oneshots[1], 1, MathUtil.randRangeDecimal(-1, 1), false, 0, 0, 0, 0, 0);
		if(p.key == '5') player.playWav(oneshots[2], 1, MathUtil.randRangeDecimal(-1, 1), false, 0, 0, 200, 200, 0);
		if(p.key == '6') player.stop(oneshots[2]);
		if(p.key == '7') player.playWav(oneshots[1]);
		if(p.key == '8') player.loopWav(soundbed);
		if(p.key == '9') player.pauseToggle(soundbed);
		if(p.key == '0') player.stop(soundbed);
		if(p.key == '-') player.fadeOut(soundbed);
		if(p.key == '=') player.playWav(oneshots[2], 1, WavPlayer.PAN_CENTER, true, 0, 0, 5000, 5000, 0);
	}
}
