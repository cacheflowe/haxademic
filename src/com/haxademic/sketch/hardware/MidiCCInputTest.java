package com.haxademic.sketch.hardware;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;

public class MidiCCInputTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		appConfig.setProperty( AppSettings.WIDTH, "600" );
		appConfig.setProperty( AppSettings.HEIGHT, "400" );
		appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
	}

	public void setup() {
		super.setup();
	}

	public void drawApp() {
		background(p.midiState.midiCCPercent(0, 7) * 255, p.midiState.midiCCPercent(1, 7) * 255, p.midiState.midiCCPercent(2, 7) * 255 );
	}

}