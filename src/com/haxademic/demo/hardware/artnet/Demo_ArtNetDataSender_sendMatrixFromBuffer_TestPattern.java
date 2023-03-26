package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_ArtNetDataSender_sendMatrixFromBuffer_TestPattern
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
	protected PGraphics ledTexture;
	protected int numPixels;

	protected String BRIGHTNESS = "BRIGHTNESS";
	protected String FLIP_H = "FLIP_H";
	protected String FLIP_V = "FLIP_V";
	protected String ROT_180 = "ROT_180";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		// prepare ArtNetSender & matrix texture
		ledTexture = PG.newPG(48, 12);
		numPixels = ledTexture.width * ledTexture.height;
		DebugView.setTexture("ledTexture", ledTexture);
		artNetDataSender = new ArtNetDataSender("192.168.1.101", 6, numPixels);

		// build textures
		// video
		DemoAssets.movieTestPattern().loop();
		DebugView.setTexture("movieTestPattern", DemoAssets.movieTestPattern());
		
		// Add UI
		UI.addTitle("LED Config");
		UI.addSlider(BRIGHTNESS, 0.2f, 0, 1, 0.01f, false);
		UI.addToggle(FLIP_H, false, false);
		UI.addToggle(FLIP_V, false, false);
		UI.addToggle(ROT_180, false, false);
	}

	protected void drawApp() {
		p.background(0);
		
		// video test pattern
		if(DemoAssets.movieTestPattern().width > 10) {
			ImageUtil.copyImage(DemoAssets.movieTestPattern(), ledTexture);
		}
		
		// rotation correction operations
		if(UI.valueToggle(FLIP_H)) ImageUtil.flipH(ledTexture);
		if(UI.valueToggle(FLIP_V)) ImageUtil.flipV(ledTexture);
		if(UI.valueToggle(ROT_180)) ImageUtil.rotate180(ledTexture);
		
		
		// reduce brightness
		BrightnessFilter.instance().setBrightness(UI.value(BRIGHTNESS));
		BrightnessFilter.instance().applyTo(ledTexture);
		
		// send it!
		artNetDataSender.sendMatrixFromBuffer(ledTexture);
		
		// show original texture on screen
		ImageUtil.cropFillCopyImage(ledTexture, p.g, false);
	}
	
}