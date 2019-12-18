package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;

public class Demo_WavPlayer_loopLengthMatch
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected WavPlayer player;
	protected String beat1 = "data/audio/breakbeats/break01.wav";
	protected String beat2 = "data/audio/breakbeats/break02.wav";

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 400 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
		// create looping players
		player = new WavPlayer();
		player.loopWav(beat1);
		player.loopWav(beat2);
		
		// debug output audio lengths
		p.debugView.setValue("beat1 Length", player.duration(beat1));
		p.debugView.setValue("beat2 Length", player.duration(beat2));

		// send Beads audio player analyzer to PAppletHax
		AudioIn.instance(new AudioInputBeads(player.context()));
	}
	
	public void drawApp() {
		p.background(0);
		
		
		// adjust audio loops' volume & pitch 
		if(p.mousePercentX() > 0.5f) {
			// match beat 2 to beat 1
			float bpm = 10f + p.mousePercentY() * 200f;
			float bpmToMs = 60000f / bpm * 4; // times 4 because they're only 1 bar loops
			// formula from https://math.stackexchange.com/a/1205895
			float syncRatio1 = (bpmToMs) / player.duration(beat1);
			float syncRatio2 = (bpmToMs) / player.duration(beat2);
			float pitchShift = P.log(syncRatio1) / P.log(2f);
			player.setPitch(beat1, -pitchShift * 12f);
			pitchShift = P.log(syncRatio2) / P.log(2f);
			player.setPitch(beat2, -pitchShift * 12f);
			p.debugView.setValue("syncRatio1", syncRatio1);
			p.debugView.setValue("syncRatio2", syncRatio2);
			p.debugView.setValue("pitchShift", pitchShift);
		}
		// chop sample - not great but does work
		if(p.mousePercentX() > 0.9f) {
			p.debugView.setValue("player.progress(beat2)", player.progress(beat2));
			if(player.progress(beat2) % 0.25f >= 0.248f) {
				P.out("chop!");
				player.seekToProgress(beat2, MathUtil.randRange(0, 8) * 0.25f/2f);
			}
		}
		
		// show debug audio view (and keep it open)
		p.debugView.active(true);
		p.image(AudioIn.instance().audioInputDebugBuffer(), 240, 100);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			// seek to different times in synced players
			player.seekToProgress(beat1, 0);
			player.seekToProgress(beat2, MathUtil.randRange(0, 8) * 0.25f/2f);
			
			// match beat 2 to beat 1
			// formula from https://math.stackexchange.com/a/1205895
			float syncRatio = player.duration(beat1) / player.duration(beat2);
			float pitchShift = P.log(syncRatio) / P.log(2f);
			player.setPitch(beat1, 0);
			player.setPitch(beat2, -pitchShift * 12f);
			p.debugView.setValue("syncRatio", syncRatio);
			p.debugView.setValue("pitchShift", pitchShift);
		} else if(p.key == 'r') {
			player.restart(beat1);
			player.restart(beat2);
		}
	}
}
