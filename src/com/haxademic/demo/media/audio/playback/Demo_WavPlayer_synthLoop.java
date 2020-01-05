package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;

public class Demo_WavPlayer_synthLoop
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected WavPlayer player1;
	protected String soundbed = "data/audio/communichords/bass/operator-organ-bass.aif";
	protected WavPlayer player2;
	protected String soundMid = "data/audio/communichords/mid/operator-mello-flute-winds.aif";
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 400 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// create looping players
		player1 = new WavPlayer();
		player1.loopWav(soundbed);
		player2 = new WavPlayer();
		player2.loopWav(soundMid);
		
		// send Beads audio player analyzer to PAppletHax
		AudioIn.instance(new AudioInputBeads(WavPlayer.sharedContext()));
	}
	
	protected void drawApp() {
		p.background(0);
		
		// adjust audio loops' volume & pitch 
		player1.setVolume(soundbed, Mouse.xNorm);
		player2.setVolume(soundMid, 1f - Mouse.xNorm);
		player1.setPitch(soundbed, P.round(-12f + 24f * Mouse.yNorm));
		player2.setPitch(soundMid, P.round(-12f + 24f * Mouse.yNorm));
		
		// set glide time manually for testing
		player2.setGlideTime(soundMid, 200);
		
		// show debug audio view (and keep it open)
		DebugView.active(true);
		p.image(AudioIn.instance().audioInputDebugBuffer(), 240, 100);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') player2.pauseToggle(soundMid);
		if(p.key == 'c') player2.stop(soundMid);
		if(p.key == 's') player2.loopWav(soundMid);
	}
}
