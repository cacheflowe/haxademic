package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.midi.devices.LaunchPad;
import com.haxademic.core.hardware.midi.devices.LaunchPad.ILaunchpadCallback;

import themidibus.MidiBus;

import com.haxademic.core.hardware.midi.devices.LaunchPadMini;

public class Demo_LaunchPad_Multiple
extends PAppletHax
implements ILaunchpadCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected LaunchPadMini launchpad1;
	protected LaunchPadMini launchpad2;
	
	protected void config() {
		Config.setProperty(AppSettings.PG_WIDTH, 128 );
		Config.setProperty(AppSettings.PG_HEIGHT, 128 );
	}
	
	protected void firstFrame() {
		MidiBus.list();
		launchpad1 = new LaunchPadMini(6, 9);
//		launchpad1 = new LaunchPadMini("4- Launchpad");
		launchpad1.setDelegate(this);
		launchpad2 = new LaunchPadMini(8, 11);
//		launchpad2 = new LaunchPadMini("5- Launchpad");
		launchpad2.setDelegate(this);
	}
	
	//////////////////////////////
	// DEMO DRAW
	//////////////////////////////
	
	protected void drawApp() {
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

		// draw to screen
		ImageUtil.cropFillCopyImage(pg, p.g, true);
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