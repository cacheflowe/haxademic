package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.ui.UIButton;

public class Demo_Interphase
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 780 );
		Config.setProperty( AppSettings.APP_NAME, "INTERPHASE" );
//		Config.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
	}
	
	protected void firstFrame() {
//		interphase = new Interphase(SequencerConfig.interphaseChannels, true);
//		SequencerConfig.BASE_AUDIO_PATH = FileUtil.getHaxademicDataPath();
		interphase = new Interphase(SequencerConfig.interphaseChannels(), true);
//		interphase = new Interphase(SequencerConfig.interphaseChannelsMinimal(), true);
	}
	
	public void keyPressed() {
		super.keyPressed();
		
		interphase.keyPressed();
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		interphase.update(p.g);
	}
	
	/////////////////////////////////////////////////////////////////
	// UIControls listener
	/////////////////////////////////////////////////////////////////

	public void uiButtonClicked(UIButton button) {
		if(interphase != null) interphase.uiButtonClicked(button);
	}

}
