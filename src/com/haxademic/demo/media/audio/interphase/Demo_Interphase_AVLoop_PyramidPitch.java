package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.AudioUtil;
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

public class Demo_Interphase_AVLoop_PyramidPitch
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
	
	protected PImage mockup;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
	}
	
	protected void firstFrame() {
		AudioUtil.DEFAULT_AUDIO_MIXER_INDEX = 4;

		mockup = P.getImage("images/_sketch/sheraton-street-view.png");
		
//		SequencerConfig.BASE_AUDIO_PATH = FileUtil.getHaxademicDataPath();
		boolean hasUI = true;
		interphase = new Interphase(SequencerConfig.interphaseChannels(), hasUI);
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
	
	public void keyPressed() {
		super.keyPressed();
		interphase.keyPressed();
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
		pg.ortho();
		
		// draw textures for "projection
		pg.beginShape(P.QUAD);
		pg.textureMode(P.NORMAL);
		pg.texture(((SequencerTexture) interphase.sequencerAt(0).getDrawable()).buffer());
		pg.vertex(221, 168, 0, 0, 0);
		pg.vertex(248, 168, 0, 1, 0);
		pg.vertex(244, 340, 0, 1, 1);
		pg.vertex(213, 341, 0, 0, 1);
		pg.endShape();

		pg.beginShape(P.QUAD);
		pg.textureMode(P.NORMAL);
		pg.texture(((SequencerTexture) interphase.sequencerAt(1).getDrawable()).buffer());
		pg.vertex(250, 168, 0, 0, 0);
		pg.vertex(280, 168, 0, 1, 0);
		pg.vertex(279, 340, 0, 1, 1);
		pg.vertex(244, 341, 0, 0, 1);
		pg.endShape();
		
		pg.beginShape(P.QUAD);
		pg.textureMode(P.NORMAL);
		pg.texture(((SequencerTexture) interphase.sequencerAt(2).getDrawable()).buffer());
		pg.vertex(282, 168, 0, 0, 0);
		pg.vertex(313, 168, 0, 1, 0);
		pg.vertex(313, 340, 0, 1, 1);
		pg.vertex(278, 341, 0, 0, 1);
		pg.endShape();

		pg.beginShape(P.QUAD);
		pg.textureMode(P.NORMAL);
		pg.texture(((SequencerTexture) interphase.sequencerAt(3).getDrawable()).buffer());
		pg.vertex(316, 168, 0, 0, 0);
		pg.vertex(344, 168, 0, 1, 0);
		pg.vertex(346, 340, 0, 1, 1);
		pg.vertex(316, 341, 0, 0, 1);
		pg.endShape();

		// color panels
		int beat = P.floor(P.store.getInt(Interphase.BEAT) / 4f);
		pg.beginShape(P.QUADS);
		pg.fill(ColorsHax.COLOR_GROUPS[1][(0+beat)%4]);
		pg.vertex(216, 333);
		pg.vertex(244, 333);
		pg.vertex(244, 395);
		pg.vertex(216, 395);
		pg.fill(ColorsHax.COLOR_GROUPS[1][(1+beat)%4]);
		pg.vertex(247, 333);
		pg.vertex(280, 333);
		pg.vertex(280, 395);
		pg.vertex(246, 395);
		pg.fill(ColorsHax.COLOR_GROUPS[1][(2+beat)%4]);
		pg.vertex(282, 333);
		pg.vertex(313, 333);
		pg.vertex(313, 395);
		pg.vertex(282, 395);
		pg.fill(ColorsHax.COLOR_GROUPS[1][(3+beat)%4]);
		pg.vertex(316, 333);
		pg.vertex(346, 333);
		pg.vertex(346, 395);
		pg.vertex(316, 395);
		pg.endShape();

		
		// draw mockup
		float mockupScale = MathUtil.scaleToTarget(mockup.height, pg.height);
		pg.image(mockup, 0, 0, mockup.width * mockupScale, mockup.height * mockupScale);
		
		// lights
//		PG.setBetterLights(pg);
		
		// draw pyramid right
		pg.push();
		pg.translate(p.width * 0.72f, p.height * 0.5f);
//		PG.basicCameraFromMouse(pg);
		pg.rotateX(-0.5f);
		pg.rotateY(Metronome.loopProgress() * P.TWO_PI);
		drawPyramid(8, 100, 200, 200);
		pg.pop();


		PG.setDrawFlat2d(pg, true);
		
		// draw pyramid left
		pg.push();
		pg.translate(p.width * 0.22f, p.height * 0.8f);
//		PG.basicCameraFromMouse(pg);
		pg.rotateX(-0.25f);
		pg.rotateY(P.PI);
		drawPyramid(4, 50, 100, 150);
		pg.pop();
		
		
		
		// end draw
		PG.setDrawFlat2d(pg, false);
		pg.endDraw();
		
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
			UI.setValue("SAMPLE_1", 34);
			UI.setValue("SAMPLE_2", 31);
			UI.setValue("SAMPLE_3", 58);
			UI.setValue("SAMPLE_4", 22);
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
		interphase.update(null);
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
		if(key.equals(Interphase.SEQUENCER_TRIGGER)) {
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
