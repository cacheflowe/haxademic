package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchPadMini;
import com.haxademic.core.hardware.midi.devices.NovationColorsMK3;

import processing.core.PGraphics;
import themidibus.SimpleMidiListener;

public class Demo_LaunchPad_Grid
extends PAppletHax
implements SimpleMidiListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float[][] grid = new float[9][8];
	protected PGraphics pg8x8;
	
	protected void firstFrame() {
		MidiDevice.init(2, 5, this);
		
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
//		val = P.floor(val * (float) NovationColors.colors.length) / (float)NovationColors.colors.length;
//		P.out(val);
		if(grid[x][y] != val) {
			grid[x][y] = val;
			MidiDevice.instance().sendMidiOut(true, 0, LaunchPadMini.gridToMidiNote(x, y), NovationColorsMK3.colorByPercent(val));
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
	
	protected void drawApp() {
		p.background(0);
		
		if(p.frameCount == 10) {
			setAll(1);
			setAll(0);	// flip onces to init
		}
		
		// draw test pattern
		pg.beginDraw();
		PG.push(pg);
		PG.setCenterScreen(pg);
		Gradients.radial(pg, pg.width * 2f, pg.height * 2f, p.color(127f + 127f * P.sin(-p.frameCount * 0.05f)), p.color(127f + 127f * P.sin(P.HALF_PI + -p.frameCount * 0.05f)), 50);
		PG.pop(pg);
		pg.noStroke();
//		pg.fill(180f + 75f * P.sin(p.frameCount * 0.1f));
		pg.fill(255);
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		pg.rotate(p.frameCount * 0.03f);
//		pg.rect(0, 0, pg.width, pg.height * 1f/4f);
		pg.endDraw();
		
		// copy to texture & send to buttons
		ImageUtil.copyImage(pg, pg8x8);
		DebugView.setTexture("pg8x8", pg8x8);
		pg8x8.loadPixels();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
//				setButton(x, y, 1f/255f * ColorUtil.redFromColorInt(ImageUtil.getPixelColor(pg8x8, x, y)));
			}			
		}
		
		p.image(pg, 0, 0);
		
		// check to see if grid notes are calculated properly
		if(p.keyPressed && p.key == 'm') {
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid[x].length; y++) {
					P.out(x, y, LaunchPadMini.gridToMidiNote(x, y));
				}
			}

		}
	}

	//////////////////////////////
	// MIDIBUS LISTENERS
	//////////////////////////////
	
	public void controllerChange(int channel, int pitch, int velocity) {

	}

	public void noteOff(int channel, int pitch, int velocity) {
		
	}

	public void noteOn(int channel, int pitch, int velocity) {
		if(channel == 0) {
			int inputGridX = LaunchPadMini.xIndexFromNote(pitch);
			int inputGridY = LaunchPadMini.yIndexFromNote(pitch);
			float buttonPressResult = toggleButton(inputGridX, inputGridY);
			P.out(pitch, inputGridX, inputGridY, buttonPressResult);
		}
	}
	
}