package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.midi.devices.LaunchPad;
import com.haxademic.core.hardware.midi.devices.LaunchPad.ILaunchpadCallback;

public class Demo_LaunchPad_Multiple
extends PAppletHax
implements ILaunchpadCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected LaunchPad launchpad1;
	protected LaunchPad launchpad2;
	
	public void setupFirstFrame() {
		launchpad1 = new LaunchPad(0, 3);
		launchpad1.setDelegate(this);
		launchpad2 = new LaunchPad(1, 4);
		launchpad2.setDelegate(this);
	}
	
	//////////////////////////////
	// DEMO DRAW
	//////////////////////////////
	
	public void drawApp() {
		p.background(0);
		
		// draw test pattern
		pg.beginDraw();
		pg.background(0);
		pg.fill(255);
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		pg.rotate(p.frameCount * 0.03f);
		pg.rect(0, 0, pg.width * 2f, pg.height * 1f/4f);
		pg.endDraw();
		
		// copy texture to 2 launchpads, inverting the 2nd
		launchpad1.setTextureFromTexture(pg);
		InvertFilter.instance(p).applyTo(pg);
		launchpad2.setTextureFromTexture(pg);
		
		p.image(pg, 0, 0);
		
		// print debug
		MidiState.instance().printButtons();
		MidiState.instance().printCC();
	}

	//////////////////////////////
	// LAUNCHPAD LISTENERS
	//////////////////////////////
	
	public void cellUpdated(LaunchPad launchpad, int x, int y, float value) {
		int launchpadNumber = (launchpad == launchpad1) ? 1 : 2;
		P.out(launchpadNumber, x, y, value);
	}

	public void noteOn(LaunchPad launchpad, int note, float value) {
		int launchpadNumber = (launchpad == launchpad1) ? 1 : 2;
		P.out(launchpadNumber, note, value);
	}
	
}