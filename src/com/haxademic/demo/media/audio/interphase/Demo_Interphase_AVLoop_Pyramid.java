package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.interphase.Scales;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.media.audio.interphase.SequencerTexture;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Interphase_AVLoop_Pyramid
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;
	protected int numSequencers;
	protected BaseTexture audioTexture;
	
	protected String SAMPLE_ = "SAMPLE_";
	protected String GLOBAL_BPM = "GLOBAL_BPM";
	protected String GLOBAL_EVOLVES = "GLOBAL_EVOLVES";
	protected String CUR_SCALE = "CUR_SCALE";
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 1024 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
	}
	
	protected void firstFrame() {
//		SequencerConfig.BASE_AUDIO_PATH = FileUtil.getHaxademicDataPath();
		interphase = new Interphase(SequencerConfig.interphaseChannels());
		interphase.initUI();
//		interphase = new Interphase(SequencerConfig.interphaseChannelsMinimal(), hasUI, hasMidi);
		numSequencers = interphase.sequencers().length;

		// add drawable sequencers
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			seq.setDrawable(new SequencerTexture(i));
		}
		
		P.store.addListener(this);
		
		P.out("WebServer.DEBUG", WebServer.DEBUG);
		HttpInputState.DEBUG = false;
		
		// Interphase UI
		UI.addTitle("Interphase");
		UI.addSlider(GLOBAL_BPM, 105, 60, 170, 1, false);
		UI.addToggle(GLOBAL_EVOLVES, false, false);
		UI.addSlider(CUR_SCALE, 0, 0, Scales.SCALES.length-1, 1, false);
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			UI.addSlider(SAMPLE_+(i+1), 0, 0, seq.numSamples() - 1, 1, false);
		}
	}
	
	protected void drawApp() {
		// set draw context
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		updateMusic();
		
		// draw results
		pg.beginDraw();
		pg.background(0);
//		pg.ortho();
		
		// lights
		PG.setBetterLights(pg);
		
		// draw pyramid right
		pg.push();
		pg.translate(p.width * 0.5f, p.height * 0.5f);
//		PG.basicCameraFromMouse(pg);
		pg.rotateX(-0.5f);
		pg.rotateY(Metronome.loopProgress() * P.TWO_PI);
		drawPyramid(8, 100, 300, 300);
		pg.pop();

		// end draw
		pg.endDraw();
		
		// postprocessing
//		RadialFlareFilter.instance(p).setImageBrightness(0f + Mouse.yNorm * 10f);
//		RadialFlareFilter.instance(p).setFlareBrightness(0f + Mouse.yNorm * 10f);
//		RadialFlareFilter.instance(p).setRadialLength(0.5f + Mouse.xNorm * 0.5f);
//		RadialFlareFilter.instance(p).setIters(100f + Mouse.xNorm * 3000f);
//		RadialFlareFilter.instance(p).applyTo(pg);
		
		BloomFilter.instance(p).setStrength(9f);
		BloomFilter.instance(p).setBlurIterations(12);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.5f);
		VignetteFilter.instance(p).applyTo(pg);
		
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.11f);
		GrainFilter.instance(p).applyTo(pg);

		
		// draw to screen
		p.image(pg, 0, 0);
	}
	
	protected void drawPyramid(float sides, float radiusTop, float radiusBot, float height) {
		// draw pyramid
		float segmentRads = P.TWO_PI / sides;
		float yTop = -height/2;
		float yBot = height/2;
		for (int i = 0; i < sides; i++) {
			// get texture for panel
			PGraphics texture = ((SequencerTexture) interphase.sequencerAt(i).getDrawable()).buffer();
//			if(i == 0) DebugView.setTexture("TEST", texture);
			pg.beginShape(P.QUAD);
			pg.textureMode(P.NORMAL);
			pg.texture(texture);
			
			// get positions
			float curRads = segmentRads * i;
			float nextRads = segmentRads * (i+1);

			pg.vertex(P.cos(curRads) * radiusTop, yTop, P.sin(curRads) * radiusTop, 0, 0);
			pg.vertex(P.cos(nextRads) * radiusTop, yTop, P.sin(nextRads) * radiusTop, 1, 0);
			pg.vertex(P.cos(nextRads) * radiusBot, yBot, P.sin(nextRads) * radiusBot, 1, 1);
			pg.vertex(P.cos(curRads) * radiusBot, yBot, P.sin(curRads) * radiusBot, 0, 1);
			
			pg.endShape();
		}
	}
	
	protected void updateMusic() {
		// update music playback
		// overall interphase props
		P.store.setNumber(Interphase.BPM, UI.value(GLOBAL_BPM));
		P.store.setBoolean(Interphase.GLOBAL_PATTERNS_EVLOVE, UI.valueToggle(GLOBAL_EVOLVES));
		P.store.setNumber(Interphase.CUR_SCALE_INDEX, UI.valueInt(CUR_SCALE));
		
		// override current instruments
		UI.setValue("SAMPLE_1", 33);
		UI.setValue("SAMPLE_2", 30);
		UI.setValue("SAMPLE_3", 59);
		UI.setValue("SAMPLE_4", 23);
		UI.setValue("SAMPLE_5", 34);
		UI.setValue("SAMPLE_6", 11);
		UI.setValue("SAMPLE_7", 28);
		UI.setValue("SAMPLE_8", 17);
		if(P.store.getInt(Interphase.BEAT) % 64 >= 48) {
//			UI.setValue("SAMPLE_1", 34);
//			UI.setValue("SAMPLE_2", 31);
//			UI.setValue("SAMPLE_3", 58);
//			UI.setValue("SAMPLE_4", 22);
			UI.setValue("SAMPLE_5", 33);
			UI.setValue("SAMPLE_6", 12);
			UI.setValue("SAMPLE_7", 25);
			UI.setValue("SAMPLE_8", 13);
		}
		interphase.sequencerAt(1).setMute(false);
		interphase.sequencerAt(2).setMute(false);
		interphase.sequencerAt(3).setMute(false);
		interphase.sequencerAt(4).setMute(false);
		// more chill
		if(P.store.getInt(Interphase.BEAT) % 128 >= 64) {
			UI.setValue("SAMPLE_1", 37);
			UI.setValue("SAMPLE_2", 30);
			UI.setValue("SAMPLE_3", 59);
			UI.setValue("SAMPLE_4", 23);
			UI.setValue("SAMPLE_5", 34);
			UI.setValue("SAMPLE_6", 27);
			UI.setValue("SAMPLE_7", 29);
			UI.setValue("SAMPLE_8", 28);
			interphase.sequencerAt(1).setMute(true);
			interphase.sequencerAt(2).setMute(true);
			interphase.sequencerAt(3).setMute(true);
			interphase.sequencerAt(4).setMute(true);
		}

		// set current instruments
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencers()[i];
			seq.setSampleByIndex(UI.valueInt(SAMPLE_+(i+1)));
		}
		
		// override sequences on some channels
		if(P.store.getInt(Interphase.BEAT) % 64 < 48) {
			interphase.sequencers()[0].setPatternByInts(new int[] {1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0});
			interphase.sequencers()[1].setPatternByInts(new int[] {0,0,0,0,1,0,0,0,0,1,0,0,1,0,0,0});
			interphase.sequencers()[2].setPatternByInts(new int[] {0,1,0,0,0,1,0,1,0,1,0,0,0,0,1,1});
			interphase.sequencers()[3].setPatternByInts(new int[] {0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0});
			interphase.sequencers()[4].setPatternByInts(new int[] {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1});
			interphase.sequencers()[5].setPatternByInts(new int[] {0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0});
			interphase.sequencers()[6].setPatternByInts(new int[] {0,0,0,0,0,1,0,0,1,0,1,0,0,0,1,0});
			interphase.sequencers()[7].setPatternByInts(new int[] {0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,1});
		} else {
			interphase.sequencers()[0].setPatternByInts(new int[] {1,0,0,0,1,0,1,0,1,0,0,0,1,0,1,1});
			interphase.sequencers()[1].setPatternByInts(new int[] {0,0,0,0,1,0,1,0,0,1,0,0,1,0,0,0});
			interphase.sequencers()[2].setPatternByInts(new int[] {0,1,0,0,0,1,0,1,0,1,0,0,0,0,1,1});
			interphase.sequencers()[3].setPatternByInts(new int[] {0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0});
			interphase.sequencers()[4].setPatternByInts(new int[] {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1});
			interphase.sequencers()[5].setPatternByInts(new int[] {1,1,0,1,0,0,0,0,1,0,1,0,0,0,0,0});
			interphase.sequencers()[6].setPatternByInts(new int[] {0,0,0,0,0,1,0,0,1,0,1,0,0,0,1,0});
			interphase.sequencers()[7].setPatternByInts(new int[] {0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,1});
		}
				
		// update Interphase object every frame
		interphase.update();
	}
	
	/////////////////////////////////////////////////////////////////
	// IAppStoreListener
	/////////////////////////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
//		if(key.equals(Interphase.BEAT)) {
		if(key.equals(Interphase.CUR_STEP)) {
//			if(val.intValue() == 0) audioTexture.newMode();
//			if(val.intValue() == 2) audioTexture.newLineMode();
		}
		if(key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) {
//			if(val.intValue() == 0) audioTexture.newLineMode();
//			if(val.intValue() == 1) audioTexture.newMode();
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
