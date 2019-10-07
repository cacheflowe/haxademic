package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.dmx.DMXFixture.DMXMode;
import com.haxademic.core.hardware.dmx.editor.DMXEditor;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.video.Movie;

public class Demo_DmxEditor
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXEditor editor;
	protected PGraphics textureMap;
	protected PGraphics dmxUI;
	protected Movie video;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 960);
		p.appConfig.setProperty(AppSettings.HEIGHT, 540);
	}

	public void setupFirstFrame() {
		textureMap = PG.newPG(960/2, 540/2);	// can be smaller, but should be same aspect ratio as pgUI
		video = DemoAssets.movieFractalCube();
		video.loop();
		dmxUI = PG.newPG(960, 540);
		editor = new DMXEditor("COM3", 9600, DMXMode.SINGLE_CHANNEL, "text/dmx/dmx-lights-editor.txt", dmxUI, textureMap, DemoAssets.textureNebula());
	}

	public void drawApp() {
		p.background(0, 127, 0);
		if(video.width > 20) ImageUtil.cropFillCopyImage(video, textureMap, true);	// Update light map texture 
		editor.update();															// Update DMXEditor
		p.image(dmxUI, 0, 0);														// Draw editor to screen
	}
}
