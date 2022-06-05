package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.playback.WavPlayer;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WavPlayer_breakbeatChop
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Metronome metronome;
	protected WavPlayer player;
//	protected String beat1 = "data/audio/breakbeats/break01.wav";
//	protected String beat1 = "data/audio/breakbeats/broken_down.wav";
	protected String beat1 = "data/audio/breakbeats/dnb_loop006.wav";

	protected void config() {
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
		
		P.store.addListener(this);
		
		P.store.setNumber(Interphase.BPM, 90);
		metronome = new Metronome();
		metronome.togglePlay();
		Interphase.TEMPO_MOUSE_CONTROL = true;
		
		// create looping players
		player = new WavPlayer();
		player.loopWav(beat1);

		// send Beads audio player analyzer to PAppletHax
		AudioIn.instance(new AudioInputBeads(player.context()));
	}
	
	protected void drawApp() {
		p.background(0);
		
		// adjust audio loops' pitch to match Metronome 
		P.store.setNumber(Interphase.BPM, P.map(Mouse.xNorm, 0, 1, 60, 170));
		float bpm = P.store.getNumber(Interphase.BPM).floatValue();
		Metronome.shiftPitchToMatchBpm(player, beat1, bpm, 4);
		
		// show debug audio view (and keep it open)
		DebugView.active(true);
		p.image(AudioIn.bufferDebug(), 260, 100);
		
		// show progress
		float progress = player.progress(beat1);
		p.fill(255);
		p.rect(0, p.height - 20, p.width * progress, 20);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			player.restart(beat1);
		}
	}
	
	////////////////////////////////////////////
	// IAppStoreListeners
	////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.BEAT)) {
			P.out("BEAT");
			if(val.intValue() % 8 == 0) {
				// chop it up on the beat!
//				player.seekToProgress(beat1, MathUtil.randRange(0, 3) * 0.25f);
//				player.stop(beat1);
//				player.playWav(beat1);
//				player.seekToProgress(beat1, 3 * 0.25f);
			}
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
