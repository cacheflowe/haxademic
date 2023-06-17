package com.haxademic.demo.media.audio.interphase.viz;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.media.audio.interphase.Sequencer;

import processing.core.PGraphics;

public class InterphaseVizDmxTriggers
implements IInterphaseViz {

  protected Sequencer[] sequencers;
  protected int numSequencers;
  protected ArrayList<DMXFixture> fixture;

  public InterphaseVizDmxTriggers(Sequencer[] sequencers) {
    this.sequencers = sequencers;
    this.numSequencers = sequencers.length;
    initDMX();
  }

	protected void initDMX() {
		DMXUniverse.instanceInit("COM8", 9600);
		fixture = new ArrayList<DMXFixture>();
		for (int i = 0; i < numSequencers; i++) {
			fixture.add((new DMXFixture(1 + i * 3)).setEaseFactor(0.25f));
		}
	}

	public void update(PGraphics pg) {
		for (int i = 0; i < numSequencers; i++) {
			// dmx colors from amp scale
			// use the oldest value in the buffer, because the FFT values are a little ahead of the sound
			// this would likely need adjustment on different machines
			int lightColor = P.p.color(
				sequencers[i].ampSmoothed() * (127 + 127f * P.sin(i+0)),
				sequencers[i].ampSmoothed() * (127 + 127f * P.sin(i+1)),
				sequencers[i].ampSmoothed() * (127 + 127f * P.sin(i+2))
			);
			fixture.get(i)
				.color().setTargetInt(lightColor)
				.setEaseFactor(0.75f);
		}
	}
}
