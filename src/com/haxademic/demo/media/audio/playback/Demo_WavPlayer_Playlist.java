package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;

public class Demo_WavPlayer_Playlist
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected WavPlayer player;
	protected String[] playlist = new String[] {
		"data/audio/kit808/tom.wav",	
		"data/audio/kit808/bass.wav",	
		"data/audio/kit808/clap.wav",	
		"data/audio/kit808/hi-hat.wav",	
		"data/audio/kit808/hi-hat-open.wav",	
		"data/audio/kit808/kick.wav",	
		"data/audio/kit808/snare.wav",
	};
	protected int playlistIndex = 0;
	
	public void firstFrame() {
		player = new WavPlayer();
		AudioIn.instance(new AudioInputBeads(player.context()));
		playNextSound();
	}
	
	public void drawApp() {
		p.background(0);
		checkPlayerComplete();
	}
	
	protected void playNextSound() {
		playlistIndex++;
		playlistIndex = playlistIndex % playlist.length;
		player.playWav(playlist[playlistIndex]);
	}

	protected void checkPlayerComplete() {
		float playerProgress = player.progress(playlist[playlistIndex]);
		DebugView.setValue("playerProgress", playerProgress);
		if(playerProgress >= 1f) {
			playNextSound();
		}
	}
	
}
