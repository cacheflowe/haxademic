package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.image.ImageCyclerBuffer;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class Demo_ImageCyclerBuffer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ImageCyclerBuffer imageCycler;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 720 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.FULLSCREEN, false );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	protected void firstFrame() {
		PImage[] images = new PImage[] {
				DemoAssets.squareTexture(),
				DemoAssets.smallTexture(),
				DemoAssets.textureNebula(),
				DemoAssets.textureJupiter(),
		};

//		imageCycler = new ImageCyclerBuffer(640, 280, images, 100, 99, p.color(0,255,0), false);
//		imageCycler = new ImageCyclerBuffer(640, 280, images, 100, 99, -1, false, ImageCyclerBuffer.directional_wipe);
		imageCycler = new ImageCyclerBuffer(640, 280, images, 100, 99, -1, false, ImageCyclerBuffer.circle_open);
	}
	
	protected void drawApp() {
		p.background(255,0,0);

		imageCycler.update();
		p.image(imageCycler.image(), 20, 20);
	}
	
}
