package com.haxademic.sketch.test;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageCyclerBuffer;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;

public class ImageSwapperBufferTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ImageCyclerBuffer imageCycler;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 720 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RETINA, false );
	}

	public void setup() {
		super.setup();
		
		PImage[] images = new PImage[] {
				p.loadImage(FileUtil.getFile("images/justin-home.jpg")),
				p.loadImage(FileUtil.getFile("images/justin-home.png")),
		};

		imageCycler = new ImageCyclerBuffer(640, 280, images, 300, 0.5f);
	}
	
	public void drawApp() {
		p.background(0);
		

		imageCycler.update();
		p.image(imageCycler.image(), 20, 20);
	}
	
}
