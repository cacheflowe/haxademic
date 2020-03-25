package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;

public class Demo_WavPlayer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected WavPlayer player;
	protected WavPlayer player2;
	protected String[] oneshots = new String[] {
		"data/audio/kit808/kick.wav",	
		"data/audio/kit808/snare.wav",	
	};
	protected String soundbed = "data/audio/communichords/bass/operator-organ-bass.aif";
	
	protected void firstFrame() {
		player = new WavPlayer();
		player2 = new WavPlayer(WavPlayer.newAudioContext());
		// send Beads audio player analyzer to PAppletHax
		AudioIn.instance(new AudioInputBeads(player2.context()));
	}
	
	protected void drawApp() {
		p.background(0);
		player.setVolume(soundbed, Mouse.xNorm);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') player.playWav(oneshots[0]);
		if(p.key == '2') player2.playWav(oneshots[1], 1, WavPlayer.PAN_RIGHT, false, 0);
		if(p.key == '3') player.playWav(oneshots[1], 1, WavPlayer.PAN_LEFT, false, 0);
		if(p.key == '4') player.playWav(oneshots[1], 1, MathUtil.randRange(-1, 1), false, 0);
		if(p.key == '5') player.playWav(oneshots[1]);
		if(p.key == '6') {
			player.loopWav(soundbed);
		}
	}
}
