package com.haxademic.demo.media.audio.playback;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.AmbientAudioLoop;
import com.haxademic.core.media.audio.playback.WavPlayer;

public class Demo_AmbientAudioLoop
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// audio
	protected AmbientAudioLoop ambientLoop;
	protected String audioDir = "audio/communichords/mid";
	protected String[] audioFiles;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 512 );
		Config.setProperty( AppSettings.HEIGHT, 520 );
		Config.setProperty( AppSettings.SHOW_DEBUG, false );
	}

	protected void firstFrame() {
		loadSounds();
		ambientLoop = new AmbientAudioLoop(audioFiles[0]);

		// send Beads audio player analyzer to PAppletHax
		AudioIn.instance(new AudioInputBeads(WavPlayer.sharedContext()));
	}

	protected void loadSounds() {
		// load audio directory
		P.out("DroneSampler loading sounds from:", audioDir);
		ArrayList<String> sounds = FileUtil.getFilesInDirOfTypes(FileUtil.getPath(audioDir), "wav,aif");
		audioFiles = new String[sounds.size()];
		for (int i = 0; i < sounds.size(); i++) {
			audioFiles[i] = sounds.get(i);
			P.out("Loading...", audioFiles[i]);
		}
	}

	protected void drawApp() {
		p.background(0);
		ambientLoop.update();
		DebugView.setValue("volume().value()", ambientLoop.volume().value());
		DebugView.setValue("volume().target()", ambientLoop.volume().target());
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') ambientLoop.start(0).setFadeSeconds(5);
		if(p.key == '2') ambientLoop.release().setFadeSeconds(1);
		if(p.key == '3') ambientLoop.start(5).setFadeSeconds(2);
		if(p.key == '4') ambientLoop.release().setFadeSeconds(5);
		if(p.key == '5') ambientLoop.soundForceStop();
	}
	
}
