package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.ImageCyclerBuffer;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;

public class Demo_ImageCyclerBuffer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ImageCyclerBuffer imageCycler;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 720 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RETINA, false );
	}

	public void setupFirstFrame() {

		
		PImage[] images = new PImage[] {
				p.loadImage(FileUtil.getFile("images/textures/space/sun.jpg")),
				p.loadImage(FileUtil.getFile("images/textures/space/sun-nasa.jpg")),
				p.loadImage(FileUtil.getFile("images/textures/grayscale/shader-1.jpg")),
		};

		imageCycler = new ImageCyclerBuffer(640, 280, images, 300, 0.5f);
	}
	
	public void drawApp() {
		p.background(0);
		

		imageCycler.update();
		p.image(imageCycler.image(), 20, 20);
	}
	
}
