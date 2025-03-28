package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.hardware.dmx.artnet.LedMatrix48x12;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;
import com.haxademic.demo.media.audio.interphase.viz.IInterphaseViz;
import com.haxademic.demo.media.audio.interphase.viz.InterphaseVizBasicLines;
import com.haxademic.demo.media.audio.interphase.viz.InterphaseVizGrid;
import com.haxademic.demo.media.audio.interphase.viz.InterphaseVizInfo;
import com.haxademic.demo.media.audio.interphase.viz.InterphaseVizTextLog;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Interphase_AVLoop_Example
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// interphase setup
	protected MidiDevice knobs;
	protected Interphase interphase;

	// visuals
	protected PGraphics viz;
	protected PGraphics viz2;
	protected PGraphics pgWavGrid;

	// visuals & lights
	protected IInterphaseViz interphaseViz;
	protected IInterphaseViz interphaseViz2;
	protected IInterphaseViz interphaseVizInfo;
	protected IInterphaseViz interphaseVizGrid;
	protected LedMatrix48x12 ledMatrix;
	protected IInterphaseViz interphaseDmxTriggers;

	protected void config() {
		Config.setAppSize(1920, 1080);
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
		// init interphase + config 
		SequencerConfig.setAbsolutePath();
		interphase = new Interphase(SequencerConfig.interphaseChannelsAlt());
		interphase.initUI();
		interphase.initAudioAnalysisPerChannel();

		// visual buffers
		viz = PG.newPG(p.width, p.height);
		viz2 = PG.newPG(p.width, p.height);
		pgWavGrid = PG.newPG(800, 400);
		DebugView.setTexture("viz", viz);
		DebugView.setTexture("viz2", viz2);
		DebugView.setTexture("pgWavGrid", pgWavGrid);
		DebugView.setTexture("pg", pg);
	}
	
	protected void initVisuals() {
		// interphaseViz = new InterphaseVizDemo(interphase.sequencers());
		// interphaseViz = new InterphaseVizConcentricAmps(interphase.sequencers());
		interphaseViz = new InterphaseVizBasicLines();
		// interphaseViz = new InterphaseVizAudioTexture();
		// interphaseViz2 = new InterphaseVizBasicPolygons();
		interphaseViz2 = new InterphaseVizTextLog();
		interphaseVizInfo = new InterphaseVizInfo(interphase.sequencers());
		interphaseVizGrid = new InterphaseVizGrid(interphase.sequencers());
		// interphaseViz = new InterphaseVizSequencerDrawableDemo(interphase.sequencers());
		// interphaseDmxTriggers = new InterphaseVizDmxTriggers(interphase.sequencers());
		// ledMatrix = new LedMatrix48x12();
	}

	protected void drawApp() {
		interphase.update();
		drawVisuals();
		if(interphaseDmxTriggers != null) interphaseDmxTriggers.update(pg);
		if(ledMatrix != null) updateLedMatrix();
	}

	protected void drawVisuals() {
		
		// set draw context
		p.background(30);
		p.noStroke();
		PG.setDrawCorner(p);
		
		// update viz buffers
		
		// draw layers to screen
			// draw ------------------------
			// interphaseViz.update(viz);
			interphaseViz2.update(viz2);
			interphaseVizGrid.update(viz);
			interphaseVizInfo.update(viz2);
			// draw viz 1 & 2------------------------
			ImageUtil.drawImageCropFill(viz, p.g, true);
			p.blendMode(PBlendModes.SCREEN);
			ImageUtil.drawImageCropFill(viz2, p.g, true);
			p.blendMode(PBlendModes.BLEND);
	}

	protected void updateLedMatrix() {
		ArtNetDataSender.DEBUG = false;
		ledMatrix.setOrientation(LedMatrix48x12.Orientation.ROT_0_COPY);
		// ledMatrix.setOrientation(LedMatrix48x12.Orientation.ROT_90_FILL);
		// ImageUtil.rotate180(pg);
		ledMatrix.update(viz);
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
		}

	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
