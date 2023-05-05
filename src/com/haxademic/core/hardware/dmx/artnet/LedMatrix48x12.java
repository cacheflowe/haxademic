package com.haxademic.core.hardware.dmx.artnet;

import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class LedMatrix48x12 {
	
	protected ArtNetDataSender artNetDataSender1;
	protected ArtNetDataSender artNetDataSender2;
	protected PGraphics ledTexture;
	protected int numPixels;
	
	protected String BRIGHTNESS = "BRIGHTNESS";
	protected String FLIP_H = "FLIP_H";
	protected String FLIP_V = "FLIP_V";
	protected String ROT_180 = "ROT_180";

	public LedMatrix48x12() {
		// prepare ArtNetSender & matrix texture
		ledTexture = PG.newPG(48, 12, false, false);
		PG.setNearestNeighborScaling(ledTexture);

		// Set up artnet outputs.
		// On Advatek pixel controller, make sure each output is set to
		// use the exact number of pixels you're addressing
		numPixels = ledTexture.width * ledTexture.height;
		DebugView.setTexture("ledTexture", ledTexture);
		artNetDataSender1 = new ArtNetDataSender("192.168.0.184", 0, numPixels / 2);
		artNetDataSender2 = new ArtNetDataSender("192.168.0.184", 6, numPixels / 2);

		// Add UI
		UI.addTitle("Matrix48x12 Config");
		UI.addSlider(BRIGHTNESS, 0.2f, 0, 1, 0.01f, false);
		UI.addToggle(FLIP_H, false, false);
		UI.addToggle(FLIP_V, false, false);
		UI.addToggle(ROT_180, false, false);
	}

	public void update(PImage sourceImg) {
		// ImageUtil.drawImageCropFillRotated90deg(sourceImg, ledTexture, true, true, true);
		ImageUtil.cropFillCopyImage(sourceImg, ledTexture, true);

		// rotation correction operations
		if (UI.valueToggle(FLIP_H)) ImageUtil.flipH(ledTexture);
		if (UI.valueToggle(FLIP_V)) ImageUtil.flipV(ledTexture);
		if (UI.valueToggle(ROT_180)) ImageUtil.rotate180(ledTexture);

		// reduce brightness
		BrightnessFilter.instance().setBrightness(UI.value(BRIGHTNESS));
		BrightnessFilter.instance().applyTo(ledTexture);

		// send it!
		artNetDataSender1.sendMatrixFromBuffer(ledTexture);
		artNetDataSender2.sendMatrixFromBuffer(ledTexture, ledTexture.width, ledTexture.height, 0, 0, ledTexture.height / 2, false, true);
	}

}
