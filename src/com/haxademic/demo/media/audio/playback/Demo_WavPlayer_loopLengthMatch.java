package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.playback.WavPlayer;

public class Demo_WavPlayer_loopLengthMatch
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected WavPlayer player;
	protected String beat1 = "data/audio/breakbeats/break01.wav";
	protected String beat2 = "data/audio/breakbeats/break02.wav";

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 400 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();

		// create looping players
		player = new WavPlayer();
		player.loopWav(beat1);
		player.loopWav(beat2);
		
		// debug output audio lengths
		DebugView.setValue("beat1 Length", player.duration(beat1));
		DebugView.setValue("beat2 Length", player.duration(beat2));

		// send Beads audio player analyzer to PAppletHax
		AudioIn.instance(new AudioInputBeads(player.context()));
	}
	
	protected void drawApp() {
		p.background(0);
		
		
		// adjust audio loops' volume & pitch 
		if(Mouse.xNorm > 0.5f) {
			// match beat 2 to beat 1
			float bpm = 10f + Mouse.yNorm * 200f;
			Metronome.shiftPitchToMatchBpm(player, beat1, bpm, 4);
			Metronome.shiftPitchToMatchBpm(player, beat2, bpm, 4);
		}
		
		// show debug audio view (and keep it open)
		DebugView.active(true);
		p.image(AudioIn.bufferDebug(), 240, 100);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			// match beat 2 to beat 1 at original tempo
			// formula from https://math.stackexchange.com/a/1205895
			player.setPitch(beat1, 0);
			Metronome.shiftPitchToMatchOtherPlayer(player, beat2, player, beat1);
			player.restart(beat1);
			player.restart(beat2);
		} else if(p.key == 'r') {
			player.restart(beat1);
			player.restart(beat2);
		}
	}
}
