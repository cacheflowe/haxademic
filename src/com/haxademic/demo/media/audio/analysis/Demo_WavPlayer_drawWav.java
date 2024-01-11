package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.media.audio.playback.WaveformScroller;

import processing.core.PGraphics;

public class Demo_WavPlayer_drawWav
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected WavPlayer player;
	protected String soundbed = "data/haxademic/audio/brim-beat-4.wav";
	protected PGraphics wavePg;
	protected PGraphics waveWindowPg;
	protected WaveformScroller waveformScroller;
	
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

		// send Beads audio player analyzer to PAppletHax.
		// this automatically writes audio data to the global 
		AudioIn.instance(new AudioInputBeads(player.context()));

		// add audio texture to DebugView
		DebugView.setTexture("bufferWaveform", AudioIn.bufferWaveform());
		DebugView.setTexture("bufferFFT", AudioIn.bufferFFT());
		
		// build buffers to draw waveform into
		wavePg = PG.newPG(p.width, p.height / 3);
		waveWindowPg = PG.newPG(p.width, p.height / 3);
		waveformScroller = new WaveformScroller(p.width, p.height / 3);

		DebugView.setTexture("wavePg", wavePg);
		DebugView.setTexture("waveWindowPg", waveWindowPg);
		DebugView.setTexture("waveformScroller", waveformScroller.texture());

		// play sound
		player.loopWav(soundbed);
	}
	
	protected void drawApp() {
		p.background(0);
		player.setVolume(soundbed, Mouse.xNorm);
		drawFullWave();
		drawWaveScroll();

		DebugView.setValue("AudioContext :: numinputs", player.activeConnections());
	}
	
	protected void drawFullWave() {
		player.drawWav(wavePg, soundbed);
		ImageUtil.cropFillCopyImage(wavePg, p.g, 0, 0, p.width, p.height / 2, false);

		// playhead
		float progress = player.progress(soundbed);
		progress = waveformScroller.progress(); // override with eased progress
		p.fill(0, 255, 0);
		p.rect(progress * p.width, 0, 5, p.height / 2);
	}
	
	protected void drawWaveScroll() {
		waveformScroller.scrollSpeed(1 + P.round(Mouse.yNorm * 50));
		waveformScroller.update(player.getPlayer(soundbed));
		ImageUtil.cropFillCopyImage(waveformScroller.texture(), p.g, 0, p.height / 2, p.width, p.height / 2, false);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == '8') player.loopWav(soundbed);
		if(p.key == '9') player.pauseToggle(soundbed);
		if(p.key == '0') player.stop(soundbed);
		if(p.key == '-') player.fadeOut(soundbed);
	}

}
