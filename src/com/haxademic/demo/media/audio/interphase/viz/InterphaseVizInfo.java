package com.haxademic.demo.media.audio.interphase.viz;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Sequencer;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class InterphaseVizInfo 
implements IAppStoreListener, IInterphaseViz {

  protected Sequencer[] sequencers;
  protected int numSequencers;

  public InterphaseVizInfo(Sequencer[] sequencers) {
    this.sequencers = sequencers;
    this.numSequencers = sequencers.length;
    P.store.addListener(this);
  }

  public void update(PGraphics pg) {
    pg.beginDraw();
    pg.clear();
    pg.background(0);
		PG.setDrawCorner(pg);

		// set font
		FontCacher.setFontOnContext(pg, DemoAssets.font8px(), P.p.color(255), 1.5f, PTextAlign.LEFT, PTextAlign.TOP);

		// loop through channels
		int columnW = pg.width / Interphase.NUM_CHANNELS;
		for (int i = 0; i < Interphase.NUM_CHANNELS; i++) {
			int colX = columnW * i;
			pg.push();
			pg.translate(colX, 0);

			// draw waveform & playhead
			// float sampleLengthS = sequencers[i].sampleLength / 5f;
			// float maxWavW = columnW - 40;
			// float wavW = sampleLengthS;
			// wavW = P.constrain(wavW, 0, maxWavW);
			float wavW = columnW - 40;
			int wavX = 20;
			int wavY = 20;
			int wavH = 32;
			pg.image(sequencers[i].sampleWaveformPG(), wavX, wavY, wavW, wavH);
			// playhead
			float progress = sequencers[i].sampleProgress();
			if(progress > 0 && progress < 1) {
				pg.stroke(255, 0, 0);
				pg.rect(wavX + wavW * progress, wavY, 2, wavH);
			}

			// print text info
			pg.text(sequencers[i].info(), 20, 120);

			pg.pop();
		}
		
    pg.endDraw();
  }

  /////////////////////////////////////////////////////////////////
  // IAppStoreListener
  /////////////////////////////////////////////////////////////////
  
  public void updatedNumber(String key, Number val) {}
  public void updatedString(String key, String val) {}
  public void updatedBoolean(String key, Boolean val) {}
  public void updatedImage(String key, PImage val) {}
  public void updatedBuffer(String key, PGraphics val) {}

}
