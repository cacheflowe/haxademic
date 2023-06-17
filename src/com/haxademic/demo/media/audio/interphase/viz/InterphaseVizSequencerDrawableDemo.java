package com.haxademic.demo.media.audio.interphase.viz;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.media.audio.interphase.draw.SequencerTexture;

import processing.core.PGraphics;

public class InterphaseVizSequencerDrawableDemo
implements IInterphaseViz {

  protected Sequencer[] sequencers;
  protected int numSequencers;

  public InterphaseVizSequencerDrawableDemo(Sequencer[] sequencers) {
    this.sequencers = sequencers;
    this.numSequencers = sequencers.length;
    for (int i = 0; i < numSequencers; i++) {
			sequencers[i].setDrawable(new SequencerTexture(i));
		}
	}

  public void update(PGraphics pg) {
    // draw results
    pg.beginDraw();
    pg.background(0);
    PG.setDrawCenter(pg);

		for (int i = 0; i < numSequencers; i++) {
			SequencerTexture drawable = (SequencerTexture) sequencers[i].getDrawable();
			int w = pg.width / numSequencers;
			int x = w * i;
			ImageUtil.cropFillCopyImage(drawable.buffer(), pg, x, 0, w, pg.height, true);
    }

    pg.endDraw();
  }

}
