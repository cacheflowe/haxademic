package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.SequencerConfig;

public class Demo_Interphase
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 780 );
		Config.setProperty( AppSettings.APP_NAME, "INTERPHASE" );
	}
	
	protected void firstFrame() {
//		SequencerConfig.BASE_AUDIO_PATH = FileUtil.getHaxademicDataPath();
		interphase = new Interphase(SequencerConfig.interphaseChannelsMinimal());
		interphase.initUI();
		interphase.initLaunchpads(1, 4, 2, 5);
		interphase.initGlobalControlsUI();
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
	
}
