package com.haxademic.demo.media.audio.interphase.viz;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class InterphaseVizConcentricAmps
implements IInterphaseViz {

  protected Sequencer[] sequencers;
  protected int numSequencers;

  public InterphaseVizConcentricAmps(Sequencer[] sequencers) {
    this.sequencers = sequencers;
    this.numSequencers = sequencers.length;
	}

  public void update(PGraphics pg) {
    // draw results
    pg.beginDraw();
    pg.background(0);
    pg.push();
    PG.setDrawCenter(pg);
    PG.setCenterScreen(pg);
    
    // draw circle per sequencer
    for (int i = 0; i < numSequencers; i++) {
      // draw square rings
      int iInv = numSequencers - i - 1;
      float ringSize = pg.width - i * (pg.width / numSequencers);
      
      // amp scale
      float amp = sequencers[iInv].ampSmoothed();
      amp = sequencers[iInv].triggerFalloff();
      int ringColor = ColorsHax.colorFromGroupAt(15, i);
      pg.fill(P.p.lerpColor(0xff000000, ringColor, amp));
      pg.noStroke();
      pg.rect(0, 0, ringSize, ringSize);
    }
    
    pg.pop();
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
