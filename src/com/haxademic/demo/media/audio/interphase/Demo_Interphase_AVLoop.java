package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pgraphics.TexturePixelatedAudio;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.media.audio.interphase.Interphase;
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

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
	}
	
	protected void firstFrame() {
//		SequencerConfig.BASE_AUDIO_PATH = FileUtil.getHaxademicDataPath();
		interphase = new Interphase(SequencerConfig.interphaseChannels(), true);
		
		// config Interphase
		P.store.setNumber(Interphase.BPM, 120);
		
		// viz
		audioTexture = new TexturePixelatedAudio(p.width, p.height);
		
		// UI
		UI.addTitle("Interphase");
		UI.addSlider("BPM", 105, 60, 170, 1, false);
		for (int i = 0; i < interphase.sequencers().length; i++) {
			Sequencer seq = interphase.sequencers()[i];
			UI.addSlider("Sequencer "+(i+1), 0, 0, seq.numSamples() - 1, 1, false);
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

		// update music
		P.store.setNumber(Interphase.BPM, UI.value("BPM"));
		for (int i = 0; i < interphase.sequencers().length; i++) {
			Sequencer seq = interphase.sequencers()[i];
			seq.setSampleByIndex(UI.valueInt("Sequencer "+(i+1)));
		}
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
		if(key.equals(Interphase.SEQUENCER_TRIGGER)) {
			P.out(val.intValue());
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
