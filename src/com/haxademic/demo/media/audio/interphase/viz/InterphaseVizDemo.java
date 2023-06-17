package com.haxademic.demo.media.audio.interphase.viz;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.media.audio.interphase.draw.SequencerTexture;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class InterphaseVizDemo
implements IInterphaseViz {

  protected Sequencer[] sequencers;
  protected int numSequencers;

  public InterphaseVizDemo(Sequencer[] sequencers) {
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

    // draw background square with overall progress as rotation
    DebugView.setValue("Metronome.loopProgress()", Metronome.loopProgress());
    pg.push();
    PG.setCenterScreen(pg);
    pg.fill(80);
    pg.rotate(Metronome.loopProgress() * P.TWO_PI);
    pg.rect(0, 0, pg.width * 0.3f, pg.width * 0.3f);
    pg.pop();

    // draw circle per sequencer
    for (int i = 0; i < numSequencers; i++) {
      ////////////////////////////////////
      // draw circles to screen
      float spacing = pg.width / 2f / numSequencers;
      float totalW = spacing * numSequencers;
      float x = pg.width / 2 - totalW / 2 + spacing * i;
      float y = pg.height / 2 - totalW / 2 + spacing * i;

      // sequence hits via LinearFloat objects
      float circleSize = pg.width * 0.05f;
      circleSize *= (1f + sequencers[i].triggerFalloff());
      pg.fill(ColorsHax.COLOR_GROUPS[6][i % 4]);
      pg.ellipse(x, y, circleSize, circleSize);

      // amp scale
      circleSize = pg.width * 0.05f;
      circleSize *= 1f + sequencers[i].ampSmoothed();
      pg.ellipse(x, y + 150, circleSize, circleSize);
    }

    pg.endDraw();

    // postprocessing
    BloomFilter.instance().setStrength(9f);
    BloomFilter.instance().setBlurIterations(12);
    BloomFilter.instance().setBlendMode(BloomFilter.BLEND_SCREEN);
    BloomFilter.instance().applyTo(pg);

    GrainFilter.instance().setTime(FrameLoop.count(0.01f));
    GrainFilter.instance().setCrossfade(0.11f);
    GrainFilter.instance().applyTo(pg);
  }

}
