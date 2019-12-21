package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class Demo_ImageUtil_drawTextureMappedRectAdvanced
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PImage backgroundImg;
	protected PGraphics videoFrameBuffer;
	protected PGraphics videoBuffer;
	protected Movie movie;
	protected TiledTexture movieRepeatTexture;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 682 );
	}

	public void firstFrame() {

		p.noStroke();
		backgroundImg = p.loadImage(FileUtil.getFile("haxademic/images/billboard-mockup.jpg"));
		
		videoFrameBuffer = p.createGraphics(768, 384, P.P2D);
		
		videoBuffer = p.createGraphics(1920, 1080, P.P2D);
		
		movie = DemoAssets.movieFractalCube();
		movie.loop();
		
		movieRepeatTexture = new TiledTexture(videoBuffer);
	}
	
	public void drawApp() {
		// background image 
		p.image(backgroundImg, 0, 0);
		
		// copy video to own buffer
		if(movie.width > 20) ImageUtil.copyImage(movie, videoBuffer);
		
		// pick video mode
		int videoMode = 0;
		if(p.frameCount % 600 > 400) videoMode = 1;
		else if(p.frameCount % 600 > 200) videoMode = 2;
		
		// draw video surface
		videoFrameBuffer.beginDraw();
		videoFrameBuffer.noStroke();
		videoFrameBuffer.background(0);
		
		if(videoMode == 0) {
			ImageUtil.drawImageCropFill(movie, videoFrameBuffer, false);
		} else if(videoMode == 1) {
			ImageUtil.drawImageCropFill(movie, videoFrameBuffer, true);
		} else {
			float movieScale = (float) videoBuffer.width / (float) videoFrameBuffer.width;
			float offset = p.frameCount * 0.001f;
			
			videoFrameBuffer.pushMatrix();
			videoFrameBuffer.translate(videoFrameBuffer.width/2, videoFrameBuffer.height/2);
			movieRepeatTexture.setSize(movieScale, movieScale * ((float) videoFrameBuffer.height / (float) videoFrameBuffer.width));
			movieRepeatTexture.setOffset(0, offset);
			movieRepeatTexture.update();
			movieRepeatTexture.drawCentered(videoFrameBuffer, videoFrameBuffer.width, videoFrameBuffer.height);
			videoFrameBuffer.popMatrix();
		}
		
		videoFrameBuffer.endDraw();
						
		// map video buffer to rectangle
		ImageUtil.drawTextureMappedRect(p.g, videoFrameBuffer, 20, 20, 271, 167, 859, 119, 853, 525, 269, 411);
		
		
		// text for video mode
		p.fill(0, 180);
		p.rect(0, p.height - 50, p.width, 50);
		p.fill(255);
		p.textSize(20);
		String mode = "Letterbox";
		if(videoMode == 1) mode = "Crop fill";
		if(videoMode == 2) mode = "Repeat";
		p.text(mode, 20, p.height - 18);
	}
}
