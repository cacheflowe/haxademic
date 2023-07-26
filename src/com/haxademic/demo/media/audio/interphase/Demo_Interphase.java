package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
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
		new MidiDevice(LaunchControlXL.deviceName, null);
		SequencerConfig.setAbsolutePath();
		interphase = new Interphase(SequencerConfig.interphaseChannelsAlt());
		interphase.initUI();
		interphase.initLaunchControls(LaunchControlXL.BUTTONS_1, LaunchControlXL.BUTTONS_2, LaunchControlXL.KNOBS_ROW_1, LaunchControlXL.SLIDERS, LaunchControlXL.KNOBS_ROW_2, LaunchControlXL.KNOBS_ROW_3);
		interphase.initLaunchpads("MIDIIN2 (LPMiniMK3 MIDI)", "MIDIOUT2 (LPMiniMK3 MIDI)", "MIDIIN4 (LPMiniMK3 MIDI)", "MIDIOUT4 (LPMiniMK3 MIDI)");
		interphase.initAudioAnalysisPerChannel();

//		interphase = new Interphase(SequencerConfig.interphaseChannelsMinimal(), true);
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		interphase.update();
		interphase.drawAudioGrid(p.g, false);
	}
	
}
