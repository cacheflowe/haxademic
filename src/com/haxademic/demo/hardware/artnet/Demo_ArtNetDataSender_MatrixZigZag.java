package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.ui.UI;

public class Demo_ArtNetDataSender_MatrixZigZag
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
	protected int numRows = 12;
	protected int numCols = 48;
	protected int numPixels = numCols * numRows;

	protected String brightness = "brightness";
	protected String colorSpeed = "colorSpeed";
	protected String colorFreq = "colorFreq";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
		artNetDataSender = new ArtNetDataSender("192.168.1.101", 6, numPixels);
		UI.addSlider(brightness, 0.2f, 0, 1, 0.01f);
		UI.addSlider(colorSpeed, 0.1f, 0, 1, 0.01f);
		UI.addSlider(colorFreq, 0.4f, 0, 1, 0.01f);
	}

	protected void drawApp() {
		createColors();
		artNetDataSender.send();
		artNetDataSender.drawDebug(p.g);
	}
	
	protected void createColors() {
		// build entire LED data, to loop through afterwards
		for(int i=0; i < numPixels; i++) {
			// set rgb colors
			float r = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(0+((i % numCols)*UI.value(colorFreq)) + -frameCount * UI.value(colorSpeed)*1f));
			float g = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(1+((i % numCols)*UI.value(colorFreq)) + -frameCount * UI.value(colorSpeed)*0.66f));
			float b = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(2+((i % numCols)*UI.value(colorFreq)) + -frameCount * UI.value(colorSpeed)*0.33f));
			
			r *= UI.value(brightness);
			g *= UI.value(brightness);
			b *= UI.value(brightness);
			
			// pixel index, stepping through single-row sequential layout, 1 by 1
			int pixelIndex = i;
			
			// zigzag remap
			int rowStartI = P.floor(i / numCols) * numCols;
			int twoRowIndex = i % (numCols * 2); // lets us flip halfway through for the zag
			int zigZagRevIndex = numCols - 1 - (i % numCols);
			if(twoRowIndex < numCols) {	// flip even rows
				pixelIndex = (rowStartI + zigZagRevIndex);
			}
			
			// set data
			artNetDataSender.setColorAtIndex(pixelIndex, r, g, b);
		}
		
		// overwrite one single pixel in sequence
		artNetDataSender.setColorAtIndex(p.frameCount % numPixels, 255, 255, 255);
	}
}