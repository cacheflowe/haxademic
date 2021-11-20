package com.haxademic.demo.media.video;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.math.MathUtil;

import VLCJVideo.VLCJVideo;

public class Demo_VLCJVideo_TwoVideos 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected VLCJVideo video1;
	protected VLCJVideo video2;
	protected int videoStartTime;
	protected boolean videoPlaying = false;
	
	protected void firstFrame() {
		video1 = new VLCJVideo(p);
		video1.open("D:\\workspace\\google-pdx-window\\data\\video\\pixelmap-1.mov");
		video2 = new VLCJVideo(p);
		video2.open("D:\\workspace\\google-pdx-window\\data\\video\\pixelmap-2.mov");
	}
	
	public void keyPressed() {
		super.keyPressed();
		
		if(p.key == '1') {
			video1.setPosition(0);
			video1.play();
			video2.setPosition(0);
			video2.play();
		} else if(p.key == '2') {
			video1.pause();
			video2.pause();
		} else if(p.key == '3') {
			video1.play();
			video2.play();
		} else if(p.key == '4') {
			video1.setTime(0);
			video2.setTime(0);
		}
	}

	protected void drawApp() {
		// clear background
		p.background(0);
		p.noStroke();
		
		// show video
		float videoScale = MathUtil.scaleToTarget(video1.width, p.width / 2);
		p.image(video1, 0, 0, video1.width * videoScale, video1.height * videoScale);
		videoScale = MathUtil.scaleToTarget(video2.width, p.width / 2);
		p.image(video2, video1.width * videoScale, 0, video2.width * videoScale, video2.height * videoScale);
		DebugView.setValue("video2", video2.width * videoScale + ", " + video2.height * videoScale);
		
		// draw progress bar
		p.fill(255);
		p.rect(0, p.height, p.width * video1.position(), -10);
	}
	
}
