package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.hardware.dmx.DMXWrapper;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class Demo_DmxWrapper
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXWrapper dmx;
	
	int numLights = 7;
	int numColors = 3;
	int numChannels = numLights * numColors;

	protected boolean audioActive = false;
	
	protected EasingColor[] colors;
	protected EasingColor targetColor;
	
	public void setupFirstFrame() {
		AudioIn.instance();
		// dmx = new DMXWrapper();
		dmx = new DMXWrapper("COM4", 9600);
		
		// init easing colors
		colors = new EasingColor[numLights];
		for (int i = 0; i < numLights; i++) {
			colors[i] = new EasingColor(0x000000, 0.15f);
		}
		targetColor = new EasingColor(0x00ff00, 0.5f);
	}

	public void drawApp() {
		p.debugView.setValue("audioActive", audioActive);
		background(0);
		if(audioActive) {
			// audio eq
			for (int i = 0; i < numChannels; i++) {
				dmx.setValue(i+1, P.constrain(P.round(255 * AudioIn.audioFreq(5 + 5 * i)), 0, 255));
			}
		} else {
			// easing color zone
			for (int i = 0; i < numLights; i++) {
				colors[i].update();
			}
			targetColor.update();
			
			// step through lights every x frames
			int frameInterval = P.round(p.mousePercentX() * 10 + 1);
			if(p.frameCount % frameInterval == 0) {
				int frameDivided = P.floor(p.frameCount / frameInterval);
				int curLightIndex = frameDivided % numLights;
//				if(curLightIndex == 0) targetColor.setCurrentHex(ColorUtil.randomHex());
				colors[curLightIndex].setCurrentInt(targetColor.colorInt());
				colors[curLightIndex].setTargetInt(0x000000);
			}
			
			// send light rgb colors
			for (int i = 0; i < numChannels; i+=3) {
				int curLightIndex = P.floor(i / 3);
				int channelR = curLightIndex * numColors + 1;
				int channelG = curLightIndex * numColors + 2;
				int channelB = curLightIndex * numColors + 3;
				dmx.setValue(channelR, round(colors[curLightIndex].r()));
				dmx.setValue(channelG, round(colors[curLightIndex].g()));
				dmx.setValue(channelB, round(colors[curLightIndex].b()));
			}

			// color cycle
			for (int i = 0; i < numChannels; i++) {
				float osc = (0.5f + 0.4f * P.sin(i)) * 0.15f;
				dmx.setValue(i+1, round(127 + 127 * P.sin(p.frameCount * osc)));
			}
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') audioActive = !audioActive;
	}
}





