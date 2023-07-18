package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.dmx.artnet.LedMatrix48x12;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.demo.media.audio.interphase.viz.IInterphaseViz;
import com.haxademic.demo.media.audio.interphase.viz.InterphaseVizAudioTexture;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Interphase_AVLoop_Example
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// interphase setup
	protected MidiDevice knobs;
	protected Interphase interphase;
	
	// visuals & lights
	protected IInterphaseViz interphaseViz;
	protected LedMatrix48x12 ledMatrix;
	protected IInterphaseViz interphaseDmxTriggers;

	protected void config() {
		Config.setAppSize(1024, 1024);
		// Config.setPgSize(2048, 2048);
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
	}
	
	protected void firstFrame() {
		initInterphase();
		initVisuals();
		P.store.addListener(this);
	}
	
	protected void initInterphase() {
		// init device for UI knobs MIDI input
		knobs = new MidiDevice(LaunchControlXL.deviceName, null);
		// init interphase + config 
		SequencerConfig.setAbsolutePath();
		interphase = new Interphase(SequencerConfig.interphaseChannelsAlt());
		interphase.initUI();
		interphase.initLaunchControls(LaunchControlXL.BUTTONS_1, LaunchControlXL.BUTTONS_2, LaunchControlXL.KNOBS_ROW_1, LaunchControlXL.SLIDERS, LaunchControlXL.KNOBS_ROW_2, LaunchControlXL.KNOBS_ROW_3);
		interphase.initLaunchpads(2, 5, 4, 7);
		interphase.initAudioAnalysisPerChannel();
		// for UI controls debugging
		// P.out("WebServer.DEBUG", WebServer.DEBUG);
		// HttpInputState.DEBUG = false;
		// numSequencers = interphase.numChannels();
	}
	
	protected void initVisuals() {
		// interphaseViz = new InterphaseVizDemo(interphase.sequencers());
		// interphaseViz = new InterphaseVizConcentricAmps(interphase.sequencers());
		interphaseViz = new InterphaseVizAudioTexture();
		// interphaseViz = new InterphaseVizBasicPolygons();
		// interphaseViz = new InterphaseVizSequencerDrawableDemo(interphase.sequencers());
		// interphaseViz = new InterphaseVizBasicPolygons();
		// interphaseDmxTriggers = new InterphaseVizDmxTriggers(interphase.sequencers());
		ledMatrix = new LedMatrix48x12();
	}

	protected void drawApp() {
		interphase.update();
		drawVisuals();
		if(interphaseDmxTriggers != null) interphaseDmxTriggers.update(pg);
		ledMatrix.setOrientation(LedMatrix48x12.Orientation.ROT_90_FILL);
		ImageUtil.rotate180(pg);
		ledMatrix.update(pg);
	}

	protected void drawVisuals() {
		// set draw context
		p.background(30);
		p.noStroke();
		PG.setDrawCorner(p);
		
		interphaseViz.update(pg);

		// draw to screen
		// PG.setCenterScreen(p.g);
		// PG.setDrawCenter(p.g);
		// p.image(pg, 0, 0);
		ImageUtil.cropFillCopyImage(pg, p.g, true);
	}
	
	
	/////////////////////////////////////////////////////////////////
	// IAppStoreListener
	/////////////////////////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
//		if(key.equals(Interphase.BEAT)) {
		if(key.equals(Interphase.CUR_STEP)) {}
		// if(key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) { // uses delay to visually appear on-beat
		// 	sequencerHits[val.intValue()].setCurrent(1).setTarget(0);
		// }
	}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED)) {
			if(val.equals("b")) SystemUtil.openWebPage("http://localhost:8080/ui");
			if(p.key == '6') AudioUtil.buildRecorder(Metronome.ac, 1500);
			if(p.key == '7') AudioUtil.finishRecording();
		}

	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
