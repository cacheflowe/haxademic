package com.haxademic.demo.media.audio.interphase.viz;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Sequencer;

import processing.core.PGraphics;
import processing.core.PImage;

public class InterphaseVizGrid 
implements IAppStoreListener, IInterphaseViz {

  protected Sequencer[] sequencers;
  protected int numSequencers;

  public InterphaseVizGrid(Sequencer[] sequencers) {
    this.sequencers = sequencers;
    this.numSequencers = sequencers.length;
    P.store.addListener(this);
  }

  public void update(PGraphics pg) {
		float boxSize = pg.width / Interphase.NUM_STEPS;
		float drawW = (boxSize * sequencers.length);
		float startY = drawW / -2f;
		float startX = (boxSize * Interphase.NUM_STEPS) / -2f;

		pg.beginDraw();
		pg.clear();
		// pg.background(0);

		PG.setCenterScreen(pg);
		PG.setDrawCorner(pg);

		pg.translate(startX, startY);

		// draw grid
		pg.blendMode(PBlendModes.LIGHTEST);
		for (int y = 0; y < Interphase.NUM_CHANNELS; y++) {
			for (int x = 0; x < Interphase.NUM_STEPS; x++) {
				boolean isOn = (sequencers[y].stepActive(x));
				pg.push();
				pg.translate(x * boxSize, y * boxSize);
				int cellColor = 10;
				if (x % 4 == 0) cellColor = P.p.color(0, 87, 167);
				if (isOn) cellColor = P.p.color(0, 127, 0);
				pg.fill(cellColor);
				pg.stroke(100);
				pg.rect(0, 0, boxSize, boxSize);
				if (isOn) pg.image(sequencers[y].sampleWaveformPG(), 0, 0, sequencers[y].sampleWaveformPG().width, boxSize);
				pg.pop();
			}
		}
		pg.blendMode(PBlendModes.BLEND);

		// track current beat
		int curBeat = P.store.getInt(Interphase.BEAT) % Interphase.NUM_STEPS;
		pg.push();
		pg.stroke(255);
		pg.stroke(0, 255, 0);
		pg.fill(255, 50);
		pg.rect(curBeat * boxSize, 0, boxSize, boxSize * Interphase.NUM_CHANNELS);
		pg.popMatrix();

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
