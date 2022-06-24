package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.dmx.DMXDebug;
import com.haxademic.core.hardware.dmx.DMXWrapper;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;
import com.haxademic.core.ui.UI;

public class Demo_DmxChannelTester
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXWrapper dmx;
	protected DMXDebug dmxDebug;

	protected int value = 0;
	
	enum DMXTestmode {
		SINGLE_CHANNEL,
		RGB,
		RGB_ALL,
		ALL_PULSE,
		CYCLE,
		AUDIOREACTIVE,
		NONE,
	}
	DMXTestmode[] modes = new DMXTestmode[] {
		DMXTestmode.RGB, 
		DMXTestmode.RGB_ALL, 
		DMXTestmode.SINGLE_CHANNEL, 
		DMXTestmode.ALL_PULSE, 
		DMXTestmode.CYCLE, 
		DMXTestmode.AUDIOREACTIVE, 
		DMXTestmode.NONE
	};
	
	protected String R = "R";
	protected String G = "G";
	protected String B = "B";
	protected String CHANNEL = "CHANNEL";
	protected String MODE = "MODE";
	protected String BOOLEAN_MODE = "BOOLEAN_MODE";
	protected String BRIGHTNESS_CAP = "BRIGHTNESS_CAP";
	protected String MANUAL_BRIGHTNESS = "MANUAL_BRIGHTNESS";
	
	protected void config() {
		Config.setAppSize(1600, 800);
		Config.setProperty(AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
//		AudioIn.instance(AudioInputLibrary.Beads);
		AudioIn.instance(AudioInputLibrary.ESS);
		
		// dmx = new DMXWrapper();
		dmx = new DMXWrapper("COM6", 115200);
		dmxDebug = new DMXDebug();
		DebugView.setTexture("dmxData", dmxDebug.buffer());
		
		// ui
		UI.addSlider(CHANNEL, 1, 1, 512, 0.25f, false);
		UI.addSlider(MANUAL_BRIGHTNESS, 1, 0, 1, 1, false);
		UI.addSlider(MODE, 0, 0, modes.length - 1, 1, false);
		UI.addSlider(R, 100, 0, 255, 1, false);
		UI.addSlider(G, 100, 0, 255, 1, false);
		UI.addSlider(B, 100, 0, 255, 1, false);
		UI.addSlider(BRIGHTNESS_CAP, 255, 0, 255, 1, false);
		UI.addSlider(BOOLEAN_MODE, 0, 0, 1, 1, false);
		
		addKeyCommandInfo();
	}
	
	protected void addKeyCommandInfo() {
		DebugView.setHelpLine("__ Key Commands", "__\n");
		DebugView.setHelpLine("SPACE |", "Reset all");
	}

	protected void drawApp() {
		background(0);
		FontCacher.setFontOnContext(p.g, FontCacher.getFont(DemoAssets.fontOpenSansPath, 40), p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		
		int startChannel = UI.valueInt(CHANNEL);
		DMXTestmode testMode = modes[UI.valueInt(MODE)];

		// choose channel with mouse
//		if(p.mouseX != p.pmouseX) {
//			startChannel = 1 + P.round(Mouse.xNorm * 512);
//		}
		
		// oscillate by default
		float freq = 0.1f;
		int valueR = P.round(127 + 127 * P.sin(p.frameCount * freq));
		int valueG = P.round(127 + 127 * P.sin(p.frameCount * freq + P.HALF_PI));
		int valueB = P.round(127 + 127 * P.sin(p.frameCount * freq + P.PI));
		
		// manual control
		if(UI.valueInt(MANUAL_BRIGHTNESS) == 1) {
			valueR = UI.valueInt(R);
			valueG = UI.valueInt(G);
			valueB = UI.valueInt(B);
		}
		
		// temp: brightness cap
		if(UI.valueInt(BRIGHTNESS_CAP) < 255) {
			valueR = P.constrain(valueR, 0, UI.valueInt(BRIGHTNESS_CAP));
			valueG = P.constrain(valueG, 0, UI.valueInt(BRIGHTNESS_CAP));
			valueB = P.constrain(valueB, 0, UI.valueInt(BRIGHTNESS_CAP));
		}
		
		if(UI.valueInt(BOOLEAN_MODE) == 1) {
			valueR = (valueR >= 127) ? 255 : 0;
			valueG = (valueG >= 127) ? 255 : 0;
			valueB = (valueB >= 127) ? 255 : 0;
		}
		
		// debug info
		String debugInfo = "Mode: " + testMode + "\n\n";
		
		// do all channels at once
		switch (testMode) {
			case SINGLE_CHANNEL:
				dmx.setValue(startChannel, valueR);
				debugInfo += "Channel: " + startChannel + "\n";
				debugInfo += "Value: " + valueR;
				break;
			case RGB:
				dmx.setValue(startChannel + 0, valueR);
				dmx.setValue(startChannel + 1, valueG);
				dmx.setValue(startChannel + 2, valueB);
				debugInfo += "ChannelR: " + startChannel + "\n";
				debugInfo += "ValueR: " + valueR + "\n\n";
				debugInfo += "ChannelG: " + (startChannel + 1) + "\n";
				debugInfo += "ValueG: " + valueG + "\n\n";
				debugInfo += "ChannelB: " + (startChannel + 2) + "\n";
				debugInfo += "ValueB: " + valueB + "\n\n";
				break;
			case RGB_ALL:
				for (int i = 1; i < 512; i+=3) {
					dmx.setValue(i+0, valueR);
					dmx.setValue(i+1, valueG);
					dmx.setValue(i+2, valueB);
				}
				debugInfo += "ValueR: " + valueR + "\n\n";
				debugInfo += "ValueG: " + valueG + "\n\n";
				debugInfo += "ValueB: " + valueB + "\n\n";
				debugInfo += "Channels 1 - " + dmx.universeSize() + "\n";
				break;
			case ALL_PULSE:
				for (int i = 1; i < 512; i++) {
					dmx.setValue(i, valueR);
				}
				debugInfo += "Channels 1 - " + dmx.universeSize() + "\n";
				debugInfo += "Value: " + valueR + "\n";
				break;
			case CYCLE:
				freq = 0.03f;
				for (int i = 1; i < 512; i+=3) {
					valueR = P.round(127 + 127 * P.sin(i/10f + p.frameCount * freq));
					valueG = P.round(127 + 127 * P.sin(i/10f + p.frameCount * freq + P.HALF_PI));
					valueB = P.round(127 + 127 * P.sin(i/10f + p.frameCount * freq + P.PI));
					dmx.setValue(i+0, valueR);
					dmx.setValue(i+1, valueG);
					dmx.setValue(i+2, valueB);
				}
				debugInfo += "Channels 1 - " + dmx.universeSize() + "\n";
				break;
			case AUDIOREACTIVE:
				int eqSpread = 1;
				for (int i = 1; i < 512; i+=3) {
					valueR = P.round(AudioIn.audioFreq(i*eqSpread+0) * 255 * 5f);
					valueG = P.round(AudioIn.audioFreq(i*eqSpread+10) * 255 * 5f);
					valueB = P.round(AudioIn.audioFreq(i*eqSpread+20) * 255 * 5f);
					dmx.setValue(i+0, valueR);
					dmx.setValue(i+1, valueG);
					dmx.setValue(i+2, valueB);
				}
				debugInfo += "Channels 1 - " + dmx.universeSize() + "\n";
				break;
			case NONE:
				debugInfo += "NONE";
				break;
			default:
				break;
		}

		// show dmx channel
		p.fill(255f);
		p.text(debugInfo, 300, 40, p.width, p.height);
		
		// draw color at bottom for debug
		p.fill(valueR, valueG, valueB);
		p.rect(0, 0, 252, p.height);
		
		// draw debug grid
		dmxDebug.updateRGB(dmx.data());
//		dmxDebug.updateSingleChannel(dmx.data());
		p.image(dmxDebug.buffer(), p.width - dmxDebug.buffer().width, p.height - dmxDebug.buffer().height);
	}
	
	protected void resetAllChannels() {
		for (int i = 1; i < 512; i++) {
			dmx.setValue(i, 0);
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') resetAllChannels();
	}
}
