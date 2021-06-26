package com.haxademic.demo.media.video;

import org.freedesktop.gstreamer.elements.PlayBin;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.media.DemoAssets;

import processing.video.Movie;

public class Demo_MovieFinishedVanilla
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie video; 

	protected void config() {
	}

	protected void firstFrame() {
		video = DemoAssets.movieFractalCube();
		video.loop();
		
		// add finish listener
		video.playbin.connect(FINISHING);
		// video.playbin.disconnect(FINISHING);
	}
	
	PlayBin.ABOUT_TO_FINISH FINISHING = new PlayBin.ABOUT_TO_FINISH() {
		@Override
		public void aboutToFinish(PlayBin playbin) {
			if(playbin == video.playbin) {
				P.out("Video finishing!");
			}
		}
	};
	
	protected void drawApp() {
		p.background(0);
		p.fill(255);
		
		p.image(video, 0, 0);
	}
	
}

