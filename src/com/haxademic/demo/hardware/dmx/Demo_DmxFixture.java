package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.dmx.DMXFixture;

public class Demo_DmxFixture
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXFixture fixture;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.DMX_PORT, "COM3" );
		p.appConfig.setProperty(AppSettings.DMX_BAUD_RATE, 9600 );
	}

	public void setupFirstFrame() {
		fixture = (new DMXFixture(32)).setEaseFactor(0.1f);
	}

	public void drawApp() {
		fixture.color().setTargetRGBANormalized(p.mousePercentX(), p.mousePercentY(), p.mousePercentX(), 1);
		background(fixture.color().colorInt());
	}
}
