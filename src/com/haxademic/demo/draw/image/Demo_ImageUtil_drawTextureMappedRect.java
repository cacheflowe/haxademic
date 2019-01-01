package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class Demo_ImageUtil_drawTextureMappedRect
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PImage backgroundImg;
	protected PGraphics videoBuffer;
	protected Movie movie;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1024 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 682 );
	}

	public void setup() {
		super.setup();
		p.noStroke();
		backgroundImg = p.loadImage(FileUtil.getFile("haxademic/images/billboard-mockup.jpg"));
		videoBuffer = p.createGraphics(768, 384, P.P2D);
		
		movie = DemoAssets.movieFractalCube();
		movie.loop();
	}
	
	public void drawApp() {
		// background image 
		p.image(backgroundImg, 0, 0);
		
		// draw waveform
		videoBuffer.beginDraw();
		videoBuffer.noStroke();
		ImageUtil.drawImageCropFill(movie, videoBuffer, true);
		videoBuffer.endDraw();
				
		// map video buffer to rectangle
		// TODO: build a subdivided version of this
		// TODO: and a repeating version for letterboxing
		
		ImageUtil.drawTextureMappedRect(p.g, videoBuffer, 10, 10, 271, 167, 859, 119, 853, 525, 269, 411);
//		ImageUtil.drawTextureMappedRect(p.g, videoBuffer, P.round(videoBuffer.width / 30f), P.round(videoBuffer.height / 30f), 271, 167, 859, 119, 853, 525, 269, 411);
	}
	
}
