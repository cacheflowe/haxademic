package com.haxademic.app.interphase;

import com.haxademic.app.interphase.sequencing.Interphase;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;

public class InterphaseStandalone
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 780 );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
		p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, false );
		p.appConfig.setProperty( AppSettings.APP_NAME, "INTERPHASE" );
//		p.appConfig.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
	}
	
	public void setupFirstFrame() {
		interphase = new Interphase();
	}
	
	public void keyPressed() {
		super.keyPressed();
		
		interphase.keyPressed();
	}
	
	public void drawApp() {
		p.background(0);
		p.noStroke();
		DrawUtil.setDrawCorner(p);

		interphase.update();
	}
	
}
