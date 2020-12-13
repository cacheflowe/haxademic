package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pgraphics.TextureRadialGridPulse;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Scales;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.ui.UI;
import com.haxademic.core.ui.UIButton;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Interphase_AVLoop
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;
	protected BaseTexture audioTexture;
	
	protected String SAMPLE_ = "SAMPLE_";
	protected String UI_BPM = "UI_BPM";
	protected String UI_EVOLVES = "UI_EVOLVES";
	protected String UI_SCALE = "UI_SCALE";

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
	}
	
	protected void firstFrame() {
//		SequencerConfig.BASE_AUDIO_PATH = FileUtil.getHaxademicDataPath();
		interphase = new Interphase(SequencerConfig.interphaseChannels(), true);
		P.store.addListener(this);
		
		// viz
//		audioTexture = new TexturePixelatedAudio(p.width, p.height);
		audioTexture = new TextureRadialGridPulse(p.width, p.height);
		
		// Interphase UI
		UI.addTitle("Interphase");
		UI.addSlider(UI_BPM, 105, 60, 170, 1, false);
		UI.addToggle(UI_EVOLVES, true, false);
		UI.addSlider(UI_SCALE, 0, 0, Scales.SCALES.length-1, 1, false);
		for (int i = 0; i < interphase.sequencers().length; i++) {
			Sequencer seq = interphase.sequencers()[i];
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

		// update music playback
		// overall interphase props
		P.store.setNumber(Interphase.BPM, UI.value(UI_BPM));
		P.store.setBoolean(Interphase.PATTERNS_AUTO_MORPH, UI.valueToggle(UI_EVOLVES));
		P.store.setNumber(Interphase.CUR_SCALE_INDEX, UI.valueInt(UI_SCALE));
		// set current instruments
		for (int i = 0; i < interphase.sequencers().length; i++) {
			Sequencer seq = interphase.sequencers()[i];
			seq.setSampleByIndex(UI.valueInt(SAMPLE_+(i+1)));
		}
		// override sequences on some channels
		interphase.sequencers()[0].setPatternByInts(new int[] {1,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0});
		interphase.sequencers()[1].setPatternByInts(new int[] {0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0});
		interphase.sequencers()[2].setPatternByInts(new int[] {0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0});
		interphase.sequencers()[3].setPatternByInts(new int[] {0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0});
		// update Interphase object every frame
		interphase.update(null);
		
		// update viz
		audioTexture.update();
		ImageUtil.cropFillCopyImage(audioTexture.texture(), p.g, true);
	}
	
	/////////////////////////////////////////////////////////////////
	// UIControls listener
	/////////////////////////////////////////////////////////////////

	public void uiButtonClicked(UIButton button) {
		if(interphase != null) interphase.uiButtonClicked(button);
	}

	/////////////////////////////////////////////////////////////////
	// IAppStoreListener
	/////////////////////////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
//		if(key.equals(Interphase.BEAT)) {
		if(key.equals(Interphase.CUR_STEP)) {
			if(val.intValue() == 0) audioTexture.updateTimingSection();
		}
		if(key.equals(Interphase.SEQUENCER_TRIGGER)) {
			if(val.intValue() == 0) audioTexture.newLineMode();
			if(val.intValue() == 1) audioTexture.updateTiming();
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
