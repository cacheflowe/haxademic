package com.haxademic.sketch.hardware;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.PAppletHax;

public class MidiCCInputTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		appConfig.setProperty( AppSettings.WIDTH, "600" );
		appConfig.setProperty( AppSettings.HEIGHT, "400" );
		appConfig.setProperty( AppSettings.FILLS_SCREEN, "true" );
	}

	public void setup() {
		super.setup();
	}

	public void drawApp() {
		background(p.midi.midiCCPercent(0, 7) * 255, p.midi.midiCCPercent(1, 7) * 255, p.midi.midiCCPercent(2, 7) * 255 );
	}

}