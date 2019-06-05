package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.dmx.DMXWrapper;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.video.Movie;

public class Demo_DmxUSBProVideoMap
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXWrapper dmx;
	
	protected Movie video;
	protected PGraphics videoBuffer;

	public void setupFirstFrame() {
		dmx = new DMXWrapper();
		
		// init video
		video = DemoAssets.movieFractalCube();
		video.loop();
	}

	public void drawApp() {
		// set context
		p.background(0);
		p.noStroke();
		
		// lazy-init video copy buffer
		if(videoBuffer == null && video.width > 50) {
			videoBuffer = p.createGraphics(video.width, video.height, P.P2D);
		}
		
		if(videoBuffer != null) {
			// copy video to buffer
			ImageUtil.copyImage(video, videoBuffer);
			
			// access bitmap pixel data
			videoBuffer.loadPixels();
			int pixelX = 250;
			int pixelY = 250;
			int colorFromVideo = ImageUtil.getPixelColor(videoBuffer, pixelX, pixelY);
			
			// draw video 
			p.fill(255);
			p.image(videoBuffer, 0, 0);
			
			// debug pixel grabbing position
			p.fill(0, 255, 0);
			p.rect(pixelX, pixelY, 3, 3);
			
			// color cycle
			dmx.setValue(1, P.round(p.red(colorFromVideo)));
			dmx.setValue(2, P.round(p.green(colorFromVideo)));
			dmx.setValue(3, P.round(p.blue(colorFromVideo)));
		}
	}

}
