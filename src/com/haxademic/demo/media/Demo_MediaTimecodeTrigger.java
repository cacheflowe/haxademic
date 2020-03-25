package com.haxademic.demo.media;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.MediaTimecodeTrigger;
import com.haxademic.core.media.MediaTimecodeTrigger.IMediaTimecodeTriggerDelegate;
import com.haxademic.core.media.audio.playback.WavPlayer;

import processing.video.Movie;

public class Demo_MediaTimecodeTrigger
extends PAppletHax
implements IMediaTimecodeTriggerDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// audio
	protected WavPlayer audioPlayer;
	protected String AUDIO_FILE;
	protected String AUDIO_MIDDLE = "AUDIO_MIDDLE";
	protected String AUDIO_RESTART = "AUDIO_RESTART";
	
	// video 
	protected Movie videoLoop;
	protected String VIDEO_FILE;
	protected String VIDEO_MIDDLE = "VIDEO_MIDDLE";
	protected String VIDEO_RESTART = "VIDEO_RESTART";
	
	// shared
	protected ArrayList<MediaTimecodeTrigger> timecodeTriggers; 

	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// loop audio
		AUDIO_FILE = FileUtil.getPath(DemoAssets.audioBiggerLoop); // make sure we're pulling from absolute project data path
		audioPlayer = new WavPlayer(false);
		audioPlayer.playWav(AUDIO_FILE, 1, WavPlayer.PAN_CENTER, true, 0);

		// loop movie
		videoLoop = DemoAssets.movieFractalCube();
		videoLoop.loop();
		VIDEO_FILE = videoLoop.filename;
		
		// set up timecode triggers
		timecodeTriggers = new ArrayList<MediaTimecodeTrigger>();
		timecodeTriggers.add(new MediaTimecodeTrigger(AUDIO_FILE, 0f,    AUDIO_RESTART, this));
		timecodeTriggers.add(new MediaTimecodeTrigger(AUDIO_FILE, 6f,    AUDIO_MIDDLE, this));
		timecodeTriggers.add(new MediaTimecodeTrigger(VIDEO_FILE, 0f,    VIDEO_RESTART, this));
		timecodeTriggers.add(new MediaTimecodeTrigger(VIDEO_FILE, 1f,    VIDEO_MIDDLE, this));
	}
	

	protected void drawApp() {
		background(0);
		
		// update audio time on triggers
		float audioPositionSFX = audioPlayer.position(AUDIO_FILE) / 1000f;
		DebugView.setValue("audioPlayer.position", audioPositionSFX);
		for (int i = 0; i < timecodeTriggers.size(); i++) {
			timecodeTriggers.get(i).update(AUDIO_FILE, audioPositionSFX);
		}
		
		// update video time on triggers
		p.image(videoLoop, 0, 0);
		DebugView.setValue("videoLoop.time()", videoLoop.time());
		for (int i = 0; i < timecodeTriggers.size(); i++) {
			timecodeTriggers.get(i).update(VIDEO_FILE, videoLoop.time());
		}
	}

	//////////////////////////////////////////////////////
	// IMediaTimecodeTriggerDelegate callback
	//////////////////////////////////////////////////////
	
	public void mediaTimecodeTriggered(String mediaId, float time, String action) {
		P.out(mediaId, time, action);
	}
	
}





