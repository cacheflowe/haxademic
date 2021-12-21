package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_ArtNetDataSender_ChannelTester
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
//	protected int numPixels = 256 * 3;//400;
	protected int numPixels = 600;
	
	protected String SINGLE_LED_MODE = "SINGLE_LED_MODE";
	protected String CHANNEL_SINGLE = "CHANNEL_SINGLE";
	protected String CHANNEL_RANGE_START = "CHANNEL_RANGE_START";
	protected String CHANNEL_RANGE_END = "CHANNEL_RANGE_END";
	protected String COLOR_R = "COLOR_R";
	protected String COLOR_G = "COLOR_G";
	protected String COLOR_B = "COLOR_B";
	
	protected PGraphics debugPG;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		// build artnet obj
		artNetDataSender = new ArtNetDataSender("192.168.1.100", 0, numPixels);
		
		// build debug buffer for visualizing artnet data array
		debugPG = PG.newPG(128, 128);
		DebugView.setTexture("debugPG", debugPG);
		
		// UI
		UI.addTitle("ArtNetDataSender");
		UI.addToggle(SINGLE_LED_MODE, true, false);
		UI.addSlider(CHANNEL_SINGLE, 0, 0, numPixels, 1, false);
		UI.addSlider(CHANNEL_RANGE_START, 0, 0, numPixels, 1, false);
		UI.addSlider(CHANNEL_RANGE_END, 9, 0, numPixels, 1, false);
		UI.addSlider(COLOR_R, 50, 0, 255, 1, false);
		UI.addSlider(COLOR_G,  0, 0, 255, 1, false);
		UI.addSlider(COLOR_B,  0, 0, 255, 1, false);
	}

	protected void drawApp() {
		background(0);
		createColors();
		artNetDataSender.send();
		artNetDataSender.drawDebug(debugPG, true);
	}
	
	protected void createColors() {
		// if automating...
//		UI.setValue(CHANNEL_SINGLE, FrameLoop.osc(0.1f, 180, 195));
//		UI.setValue(CHANNEL_RANGE_START, FrameLoop.osc(0.3f, 180, 185));
//		UI.setValue(CHANNEL_RANGE_END, FrameLoop.osc(0.2f, 190, 195));
		
		// get rgb colors from UI
		float r = UI.value(COLOR_R);
		float g = UI.value(COLOR_G);
		float b = UI.value(COLOR_B);
		
		// build entire LED data, to loop through afterwards
		for(int i=0; i < numPixels; i++) {
			// set data
			if(UI.valueToggle(SINGLE_LED_MODE) == true) {
				// SINGLE CHANNEL TEST
				if(i == UI.valueInt(CHANNEL_SINGLE)) {
					artNetDataSender.setColorAtIndex(i, r, g, b);
				} else {
					artNetDataSender.setColorAtIndex(i, 0, 0, 0);
				}
			} else {
				// CHANNEL RANGE TEST
				if(i >= UI.valueInt(CHANNEL_RANGE_START) && i <= UI.valueInt(CHANNEL_RANGE_END)) {
					artNetDataSender.setColorAtIndex(i, r, g, b);
				} else {
					artNetDataSender.setColorAtIndex(i, 0, 0, 0);
				}
			}
		}
		
		// debug text
		FontCacher.setFontOnContext(p.g, FontCacher.getFont(DemoAssets.fontOpenSansPath, 42), p.color(255), 1.2f, PTextAlign.LEFT, PTextAlign.TOP);
		String rgbText = FileUtil.NEWLINE + "R: " + (int) r + FileUtil.NEWLINE + "G: " + (int) g + FileUtil.NEWLINE + "B: " + (int) b;
		p.translate(300, 30);
		if(UI.valueToggle(SINGLE_LED_MODE) == true) {
			p.text("Channel: " + UI.valueInt(CHANNEL_SINGLE) + rgbText, 0, 0);
		} else {
			p.text("Channels: " + UI.valueInt(CHANNEL_RANGE_START) + " - " + UI.valueInt(CHANNEL_RANGE_END) + rgbText, 0, 0);
		}
	}
}