package com.haxademic.demo.audio;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.WavPlayer;

public class Demo_WavPlayer
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected WavPlayer player;
	protected String[] oneshots = new String[] {
		"data/audio/kit808/kick.wav",	
		"data/audio/kit808/snare.wav",	
	};
	protected String soundbed = "data/audio/communichords/bass/operator-organ-bass.aif";
	
	protected void overridePropsFile() {
	}
	
	public void setupFirstFrame() {
		player = new WavPlayer();
		
	}
	
	public void drawApp() {
		p.background(0);
		player.setVolume(soundbed, p.mousePercentX());
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') player.playWav(oneshots[0]);
		if(p.key == '2') player.playWav(oneshots[1]);
		if(p.key == '3') {
			player.loopWav(soundbed);
		}
	}
}
