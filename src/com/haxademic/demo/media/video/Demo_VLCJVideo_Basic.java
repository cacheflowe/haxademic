package com.haxademic.demo.media.video;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import VLCJVideo.VLCJVideo;
import uk.co.caprica.vlcj.player.base.State;

public class Demo_VLCJVideo_Basic 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected VLCJVideo video;
	protected int videoStartTime;
	protected boolean videoPlaying = false;
	
	protected void firstFrame() {
		// HAP video:
		// ffmpeg -i yourSourceFile.mov -c:v hap outputName.mov
		// https://hap.video/using-hap.html
		// https://gist.github.com/dlublin/e4585b872dd136ae88b2aa51a6a89aac
		video = new VLCJVideo(p);
//		video.open(FileUtil.getPath(DemoAssets.movieFractalCubePath));
	}
	
	public void keyPressed() {
		super.keyPressed();
		
		if(p.key == '1') {
			// restart video
			video.setPosition(0);
			video.play();
			video.setRepeat(true);
			video.setVolume(-20);
			videoStartTime = p.millis();
			videoPlaying = true;
		} else if(p.key == '2') {
			video.pause();
		} else if(p.key == '3') {
			video.play();
		} else if(p.key == '4') {
			video.setTime(0);
		}
	}

	protected void drawApp() {
		// clear background
		p.background(0);
		p.noStroke();
		
		// check finished time
		boolean videoHasFinished = video.position() > 0.95f || p.millis() > (videoStartTime + video.duration());
		videoHasFinished = video.state() == State.ENDED || video.state() == State.STOPPED;
		if(videoPlaying && videoHasFinished) {
			p.background(255, 0, 0);
		}
		
		// show video
		ImageUtil.drawImageCropFill(video, p.g, false);
//		pg.image(video, 0, 0);
		
		// draw progress bar
		p.fill(255);
		p.rect(0, p.height, p.width * video.position(), -10);
		
		// draw info
		p.fill(0, 180);
		p.rect(0, 0, 140, 140);
		
		p.fill(255);
		p.text(
				"Position: " + video.position() + FileUtil.NEWLINE + 
				"Time: " + MathUtil.roundToPrecision(video.time() / 1000f, 2) + "s" + FileUtil.NEWLINE +
				"Duration: " + MathUtil.roundToPrecision(video.duration() / 1000f, 2) + "s" + FileUtil.NEWLINE + 
				"Length: " + MathUtil.roundToPrecision(video.length() / 1000f, 2) + "s" + FileUtil.NEWLINE +
				"isPlaying: " + video.isPlaying() + FileUtil.NEWLINE +
				"state: " + video.state() + FileUtil.NEWLINE +
				"isSeekable: " + video.isSeekable() + FileUtil.NEWLINE +
				"Size: " + video.width + " x " + video.height
				, 10, 20);
	}
	
}
