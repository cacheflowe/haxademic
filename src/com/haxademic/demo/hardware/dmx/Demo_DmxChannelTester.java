package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.dmx.DMXWrapper;

public class Demo_DmxChannelTester
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXWrapper dmx;

	protected int startChannel = 1;
	protected int value = 0;
	
	protected boolean manualBrightness = false;
	protected boolean brightnessCap = true;
	
	enum DMXTestmode {
		SINGLE_CHANNEL,
		RGB,
		ALL,
		NONE,
	}
	protected DMXTestmode testMode = DMXTestmode.SINGLE_CHANNEL;
	protected boolean allActive = false;
	protected boolean booleanMode = false;
	protected boolean audioActive = false;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
//		p.appConfig.setProperty(AppSettings.INIT_ESS_AUDIO, false );
	}

	public void setupFirstFrame() {
		// dmx = new DMXWrapper();
		dmx = new DMXWrapper("COM4", 9600);
		addHelpText();
	}
	
	protected void addHelpText() {
		p.debugView.setHelpLine("__ Key Commands", "__\n");
		p.debugView.setHelpLine("1 |", "SINGLE_CHANNEL");
		p.debugView.setHelpLine("2 |", "RGB");
		p.debugView.setHelpLine("3 |", "ALL");	
		p.debugView.setHelpLine("4 |", "NONE");
		p.debugView.setHelpLine("5 |", "MANUAL BRIGHTNESS");
		p.debugView.setHelpLine("6 |", "BRIGHTNESS CAP");
		p.debugView.setHelpLine("7 |", "AUDIO MODE");
		p.debugView.setHelpLine("8 |", "BOOLEAN MODE");
		p.debugView.setHelpLine("SPACE |", "Reset all");
		p.debugView.setHelpLine("LEFT |", "Channel down");
		p.debugView.setHelpLine("RIGHT |", "Channel up");
	}

	public void drawApp() {
		background(0);

		// choose channel with mouse
		if(p.mouseX != p.pmouseX) {
			startChannel = 1 + P.round(p.mousePercentX() * 512);
		}
		
		// oscillate
		float freq = 0.1f;
		int valueR = P.round(127 + 127 * P.sin(p.frameCount * freq));
		int valueG = P.round(127 + 127 * P.sin(p.frameCount * freq + P.HALF_PI));
		int valueB = P.round(127 + 127 * P.sin(p.frameCount * freq + P.PI));
		
		// manual control
		if (manualBrightness) {
			if(p.mouseY != p.pmouseY) {
				value = P.round(p.mousePercentY() * 255f);
			}
			valueR = valueG = valueB = value;
		}
		
		if (audioActive) {
			valueR = P.round(p.audioFreq(15) * 255);
			valueG = P.round(p.audioFreq(17) * 255);
			valueB = P.round(p.audioFreq(19) * 255);
		}
		
		// temp: brightness cap
		if(brightnessCap) {
			valueR = P.constrain(valueR, 0, 127);
			valueG = P.constrain(valueG, 0, 127);
			valueB = P.constrain(valueB, 0, 127);
		}
		
		if(booleanMode) {
			valueR = (valueR >= 127) ? 255 : 0;
			valueG = (valueG >= 127) ? 255 : 0;
			valueB = (valueB >= 127) ? 255 : 0;
		}
		
		// debug info
		String debugInfo = "";
		
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
				debugInfo += "ValueR: " + valueR + "\n";
				debugInfo += "ChannelG: " + (startChannel + 1) + "\n";
				debugInfo += "ValueG: " + valueG + "\n";
				debugInfo += "ChannelB: " + (startChannel + 2) + "\n";
				debugInfo += "ValueB: " + valueB + "\n";
				break;
			case ALL:
				for (int i = 1; i < 512; i++) {
					dmx.setValue(i, valueR);
				}
				debugInfo += "Channels 1 - " + dmx.universeSize() + "\n";
				debugInfo += "Value: " + valueR + "\n";
				break;
			case NONE:
				debugInfo += "NONE";
				break;
			default:
				break;
		}

		// show dmx channel
		p.fill(255f);
		p.textSize(50);
		p.textAlign(P.LEFT, P.TOP);
		p.text(debugInfo, p.width * 0.2f, p.height * 0.14f, p.width, p.height);
		
		// debug
		p.debugView.setValue("audioActive", audioActive);
		p.debugView.setValue("channel", startChannel);
		p.debugView.setValue("valueR", valueR);
		p.debugView.setValue("valueG", valueG);
		p.debugView.setValue("valueB", valueB);
		p.debugView.setValue("manualBrightness", manualBrightness);
		p.debugView.setValue("brightnessCap", brightnessCap);
	}
	
	protected void resetAllChannels() {
		for (int i = 1; i < 512; i++) {
			dmx.setValue(i, 0);
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') testMode = DMXTestmode.SINGLE_CHANNEL;
		if(p.key == '2') testMode = DMXTestmode.RGB;
		if(p.key == '3') testMode = DMXTestmode.ALL;
		if(p.key == '4') testMode = DMXTestmode.NONE;
		if(p.key == '5') manualBrightness = !manualBrightness;
		if(p.key == '6') brightnessCap = !brightnessCap;
		if(p.key == '7') audioActive = !audioActive;
		if(p.key == '8') booleanMode = !booleanMode;
		if(p.key == ' ') resetAllChannels();
		if(p.keyCode == P.LEFT && startChannel > 1) startChannel--; 
		if(p.keyCode == P.RIGHT && startChannel < 512) startChannel++; 
		if(p.keyCode == P.UP && value > 0) value -= 5; 
		if(p.keyCode == P.DOWN && value < 250) value += 5; 
	}
}
