package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pgraphics.TextureGridInnerShapes;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Scales;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Interphase_AVLoop_Grid
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;
	protected BaseTexture audioTexture;
	
	protected String SAMPLE_ = "SAMPLE_";
	protected String GLOBAL_BPM = "GLOBAL_BPM";
	protected String GLOBAL_EVOLVES = "GLOBAL_EVOLVES";
	protected String CUR_SCALE = "CUR_SCALE";

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 1280 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
	}
	
	protected void firstFrame() {
		// viz
		audioTexture = new TextureGridInnerShapes(p.width, p.height);
		
//		SequencerConfig.BASE_AUDIO_PATH = FileUtil.getHaxademicDataPath();
		SequencerConfig.setAbsolutePath();
		interphase = new Interphase(SequencerConfig.interphaseChannelsTones());
		interphase.initUI();
		interphase.autoPlay();
		P.store.addListener(this);
		
		P.out("WebServer.DEBUG", WebServer.DEBUG);
		HttpInputState.DEBUG = false;
		
		// Interphase UI
		UI.addTitle("Interphase");
		UI.addSlider(GLOBAL_BPM, 60, 60, 170, 1, false);
		UI.addToggle(GLOBAL_EVOLVES, true, false);
		UI.addSlider(CUR_SCALE, 0, 0, Scales.SCALES.length-1, 1, false);
		for (int i = 0; i < interphase.sequencers().length; i++) {
			Sequencer seq = interphase.sequencers()[i];
			UI.addSlider(SAMPLE_+(i+1), 0, 0, seq.numSamples() - 1, 1, false);
		}
	}
	
	protected void drawApp() {
		// set draw context
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		// update music playback
		// overall interphase props
		P.store.setNumber(Interphase.BPM, UI.value(GLOBAL_BPM));
		P.store.setBoolean(Interphase.GLOBAL_PATTERNS_EVLOVE, UI.valueToggle(GLOBAL_EVOLVES));
		P.store.setNumber(Interphase.CUR_SCALE_INDEX, UI.valueInt(CUR_SCALE));
		
		// set current instruments
		for (int i = 0; i < interphase.sequencers().length; i++) {
			Sequencer seq = interphase.sequencers()[i];
			seq.setSampleByIndex(UI.valueInt(SAMPLE_+(i+1)));
		}
		
		// override sequences on some channels
//		interphase.sequencers()[0].setPatternByInts(new int[] {1,0,0,0,0,0,1,0,1,0,0,1,0,0,0,0});
//		interphase.sequencers()[1].setPatternByInts(new int[] {0,0,1,0,0,0,1,0,0,0,1,0,1,0,0,1});
//		interphase.sequencers()[2].setPatternByInts(new int[] {0,0,1,0,1,0,0,1,0,1,0,0,0,0,0,0});
//		interphase.sequencers()[3].setPatternByInts(new int[] {0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0});
//		interphase.sequencers()[0].setSampleByIndex( 3); 	// 3, 26, 30, 36, 38, 48, 52
//		interphase.sequencers()[1].setSampleByIndex(19);	// 2, 5, 9, 18, 19, 27, 29
//		interphase.sequencers()[2].setSampleByIndex(23);	// 0, 6, 15, 20, 23, 25, 26, 28, 
//		interphase.sequencers()[3].setSampleByIndex( 5);	// 2, 5, 9, 18, 19, 27, 29
		
		// update Interphase object every frame
		interphase.update(null);
		
		// update viz (disabled for the moment
		audioTexture.update();
		audioTexture.setActive(true);
		ImageUtil.cropFillCopyImage(audioTexture.texture(), p.g, true);
		
		// do something custom
		if(p.frameCount % 1000 == 0) {
//			audioTexture = new TextureConcentricDashedCubes(p.width, p.height);
//			audioTexture = new TextureNoiseLines(p.width, p.height);
//			audioTexture = new TexturePixelatedAudio(p.width, p.height);
//			audioTexture = new TextureOuterSphere(p.width, p.height);
//			audioTexture = new TextureConcentricDashedCubes(p.width, p.height);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	// IAppStoreListener
	/////////////////////////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
//		if(key.equals(Interphase.BEAT)) {
		if(key.equals(Interphase.CUR_STEP)) {
			if(val.intValue() == 0) audioTexture.newMode();
			if(val.intValue() == 0) audioTexture.updateTimingSection();
			if(val.intValue() == 2) audioTexture.newLineMode();
			if(val.intValue() == 5) audioTexture.updateTiming();
		}
		if(key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) {
			if(P.store.getInt(Interphase.CUR_STEP) % 2 == 0) audioTexture.updateTiming();
			
			if(val.intValue() == 0) audioTexture.newLineMode();
			if(val.intValue() == 1) audioTexture.newMode();
//			if(val.intValue() == 1) audioTexture.updateTiming();
			if(val.intValue() == 2) audioTexture.newLineMode();
			if(val.intValue() == 2) audioTexture.newRotation();

			
//			if(val.intValue() == 0) P.store.setNumber("beat1", 1f);
//			if(val.intValue() == 1) audioTexture.newMode();
//			if(val.intValue() == 2) audioTexture.updateTiming();
//			if(val.intValue() == 2) audioTexture.newLineMode();
		}
	}
	public void updatedString(String key, String val) {
		if(key == PEvents.KEY_PRESSED && val.equals("b")) {
//			SystemUtil.openWebPage(WebServer.getServerAddress() + "ui");
			SystemUtil.openWebPage("http://localhost:8080/ui");
		}
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
