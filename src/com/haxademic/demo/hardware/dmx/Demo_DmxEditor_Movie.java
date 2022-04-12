package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.dmx.DMXFixture.DMXMode;
import com.haxademic.core.hardware.dmx.editor.DMXEditor;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class Demo_DmxEditor_Movie
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXEditor editor;
	protected PGraphics textureMap;
	protected PGraphics dmxUI;
	protected PImage floorplan;
	protected Movie video;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 960);
		Config.setProperty(AppSettings.HEIGHT, 540);
	}

	protected void firstFrame() {
		// Create buffer for DMXEditor color sampling
		// Can be smaller, but should be same aspect ratio as pgUI
		// - Buffer is treated as the same size as GUI, but scaled up for speed of sampling smaller texture
		textureMap = PG.newPG(960/2, 540/2);
		// create buffer for DMXEditor UI
		dmxUI = PG.newPG(960, 540);
		// load floorplan
		floorplan = DemoAssets.textureNebula();
		// create animation to map to floorplan & DMXEditor
		video = DemoAssets.movieFractalCube();
		video.loop();
		// build editor with all buffers & images
		editor = new DMXEditor("COM4", 9600, DMXMode.SINGLE_CHANNEL, "text/dmx/dmx-lights-editor.txt", dmxUI, textureMap, floorplan);
	}

	protected void drawApp() {
		// clear background
		p.background(0);
		// copy lights animation to DMXEditor buffer 
		if(video.width > 20) ImageUtil.cropFillCopyImage(video, textureMap, true); 
		// update & send lights to DMX hardware
		editor.update();
		// Draw editor to screen
		p.image(dmxUI, 0, 0);
	}
}
