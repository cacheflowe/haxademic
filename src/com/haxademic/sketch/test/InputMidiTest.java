package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class InputMidiTest 
extends PAppletHax {

	protected void overridePropsFile() {
		appConfig.setProperty( "width", "600" );
		appConfig.setProperty( "height", "400" );
		appConfig.setProperty( "fills_screen", "true" );
	}

	public void setup() {
		super.setup();
	}

	public void drawApp() {
		background(p.midi.midiCCPercent(0, 7) * 255, p.midi.midiCCPercent(1, 7) * 255, p.midi.midiCCPercent(2, 7) * 255 );
	}

}