package com.haxademic.core.hardware.midi.devices;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import themidibus.MidiBus;
import themidibus.SimpleMidiListener;

public class LaunchPad
implements SimpleMidiListener {
	
	//////////////////////////////
	// STATIC GRID HELPERS
	//////////////////////////////
	
	public static int gridMidiNote(int x, int y) {
		return y * 16 + x;
	}
	
	public static int headerColMidiNote(int x) {
		return 104 + x;
	}
	
	public static int groupRowMidiNote(int y) {
		return y * 16 + 8;
	}
	
	public static int xFromNote(int note) {
		return note % 16;
	}

	public static int yFromNote(int note) {
		return P.floor(note / 16);
	}
	
	//////////////////////////////
	// CALLBACK INTERFACE
	//////////////////////////////
	
	public interface ILaunchpadCallback {
		public void cellUpdated(LaunchPad launchpad, int x, int y, float value);
		public void noteOn(LaunchPad launchpad, int note, float value);
	}
	
	//////////////////////////////
	// GRID CLASS
	//////////////////////////////
	
	protected MidiBus midiBus;
	protected float[][] grid = new float[9][8];
	protected PGraphics pg8x8;
	protected ILaunchpadCallback delegate;

	public LaunchPad(int midiIndexIn, int midiIndexOut) {
		midiBus = new MidiBus(this, midiIndexIn, midiIndexOut);
		pg8x8 = PG.newPG(8, 8);
		setAll(1);
		setAll(0);	// flip once to init
	}
	
	public void setDelegate(ILaunchpadCallback delegate ) {
		this.delegate = delegate;
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
	
	public void setButton(int x, int y, float val) {
		// quantize normalized number for less color-changing via the following comparison 
		val = P.floor(val * (float) NovationColors.colors.length) / (float)NovationColors.colors.length;
//		P.out(val);
		// update on launchpad hardware
		if(grid[x][y] != val) {
			grid[x][y] = val;
			midiBus.sendNoteOn(0, LaunchPad.gridMidiNote(x, y), NovationColors.colorByPercent(val));
		}
	}
	
	protected float getButton(int x, int y) {
		return grid[x][y];
	}
	
	protected float toggleButton(int x, int y) {
		float newVal = (grid[x][y] != 1) ? 1 : 0; 
		setButton(x, y, newVal);
		return newVal;
	}
	
	//////////////////////////
	// TEXTURE
	//////////////////////////

	public PGraphics texture() {
		return pg8x8;
	}
	
	public void setTextureFromTexture(PImage src) {
		// P.p.debugView.setTexture(pg8x8);
		ImageUtil.copyImage(src, pg8x8);
		pg8x8.loadPixels();
		for (int x = 0; x < pg8x8.width; x++) {
			for (int y = 0; y < pg8x8.height; y++) {
				setButton(x, y, 1f/255f * ColorUtil.redFromColorInt(ImageUtil.getPixelColor(pg8x8, x, y)));
			}			
		}
	}

	//////////////////////////
	// MIDIBUS LISTENERS
	//////////////////////////

	public void controllerChange(int channel, int pitch, int velocity) {
		// top row of Launchpad comes in as CC
		if(delegate != null) {
			if(velocity == 127f) {
				delegate.noteOn(this, pitch, velocity);
			}
		}
	}

	public void noteOff(int channel, int pitch, int velocity) {
		
	}

	public void noteOn(int channel, int pitch, int velocity) {
		int inputGridX = LaunchPad.xFromNote(pitch);
		int inputGridY = LaunchPad.yFromNote(pitch);
		float buttonPressResult = toggleButton(inputGridX, inputGridY);
		P.out(buttonPressResult);
		if(delegate != null) {
			delegate.cellUpdated(this, inputGridX, inputGridY, buttonPressResult);
			delegate.noteOn(this, pitch, velocity);
		}
	}

}
