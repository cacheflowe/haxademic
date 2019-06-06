package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.midi.devices.LaunchPad;
import com.haxademic.core.hardware.midi.devices.NovationColors;

import processing.core.PGraphics;
import themidibus.SimpleMidiListener;

public class Demo_LaunchPad_Grid
extends PAppletHax
implements SimpleMidiListener {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float[][] grid = new float[9][8];
	protected PGraphics pg8x8;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
		p.appConfig.setProperty(AppSettings.MIDI_DEVICE_OUT_INDEX, 3 );
	}
	
	public void setupFirstFrame() {
		setAll(1);
		setAll(0);	// flip onces to init
		
		pg8x8 = p.createGraphics(8, 8, P.P3D);
	}
	
	//////////////////////////
	// GET/SET GRID VALUE
	//////////////////////////
	
	protected void setAll(float val) {
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				setButton(x, y, val);
			}
		}
	}
	
	protected void setButton(int x, int y, float val) {
		// quantize normalized number for less color-changing via the following comparison 
		val = P.floor(val * (float) NovationColors.colors.length) / (float)NovationColors.colors.length;
//		P.out(val);
		if(grid[x][y] != val) {
			grid[x][y] = val;
			p.midiState.sendMidiOut(true, 0, LaunchPad.gridMidiNote(x, y), NovationColors.colorByPercent(val));
		}
	}
	
	protected float getButton(int x, int y) {
		return grid[x][y];
	}
	
	protected float toggleButton(int x, int y) {
		float newVal = (grid[x][y] == 0) ? 1 : 0; 
		setButton(x, y, newVal);
		return newVal;
	}
	
	//////////////////////////////
	// DEMO DRAW
	//////////////////////////////
	
	public void drawApp() {
		p.background(0);
		
		// draw test pattern
		pg.beginDraw();
		PG.push(pg);
		PG.setCenterScreen(pg);
		Gradients.radial(pg, pg.width * 2f, pg.height * 2f, p.color(127f + 127f * P.sin(-p.frameCount * 0.02f)), p.color(127f + 127f * P.sin(P.HALF_PI + -p.frameCount * 0.02f)), 50);
		PG.pop(pg);
		pg.noStroke();
//		pg.fill(180f + 75f * P.sin(p.frameCount * 0.1f));
		pg.fill(255);
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		pg.rotate(p.frameCount * 0.03f);
//		pg.rect(0, 0, pg.width, pg.height * 1f/4f);
		pg.endDraw();
		
		ImageUtil.copyImage(pg, pg8x8);
		p.debugView.setTexture(pg8x8);
		pg8x8.loadPixels();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				setButton(x, y, 1f/255f * ColorUtil.redFromColorInt(ImageUtil.getPixelColor(pg8x8, x, y)));
			}			
		}
		
		p.image(pg, 0, 0);
		
		// print debug
		p.midiState.printButtons();
		p.midiState.printCC();
	}

	//////////////////////////////
	// MIDIBUS LISTENERS
	//////////////////////////////
	
	public void controllerChange(int channel, int  pitch, int velocity) {
		
	}

	public void noteOff(int channel, int  pitch, int velocity) {
		
	}

	public void noteOn(int channel, int  pitch, int velocity) {
		if(channel == 0) {
			int inputGridX = LaunchPad.xFromNote(pitch);
			int inputGridY = LaunchPad.yFromNote(pitch);
			float buttonPressResult = toggleButton(inputGridX, inputGridY);
			P.out(buttonPressResult);
		}
	}
	
}