package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

public class Demo_IAudioInput
extends PAppletHax { public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false);
		p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, false);
		p.appConfig.setProperty( AppSettings.INIT_BEADS_AUDIO, true);
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true);
	}

	public void drawApp() {
		background(0);
		if(p.audioInputDebugBuffer != null) {
			p.image(p.audioInputDebugBuffer, 360, 0);
		}
	}

}

