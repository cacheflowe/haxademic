package com.haxademic.sketch.audio.beads;

import java.util.Arrays;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import beads.AudioContext;
import beads.Bead;
import beads.Buffer;
import beads.Clock;
import beads.Envelope;
import beads.Gain;
import beads.KillTrigger;
import beads.Noise;
import beads.Panner;
import beads.Pitch;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;
import beads.WavePlayer;

public class BeadsMusic
extends PAppletHax { 
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	AudioContext ac;
	Sample sample01;
	Sample sample02;
	Sample sample03;
	Sample sample04;
	Clock clock;
	int fore = color(255, 102, 204);
	int back = color(0,0,0);

	protected void overridePropsFile() {
		//	p.appConfig.setProperty( AppSettings.WIDTH, 800 );
	}

	public void setupFirstFrame() {
		ac = new AudioContext();

		// load a sample		
		sample01 = SampleManager.sample(FileUtil.getFile("audio/kit808/kick.wav"));
		sample02 = SampleManager.sample(FileUtil.getFile("audio/kit808/snare.wav"));
		sample03 = SampleManager.sample(FileUtil.getFile("audio/kit808/hi-hat.wav"));
		sample04 = SampleManager.sample(FileUtil.getFile("audio/kit808/hi-hat-open.wav"));

		/*
		 * In this example a Clock is used to trigger events. We do this
		 * by adding a listener to the Clock (which is of type Bead).
		 * 
		 * The Bead is made on-the-fly. All we have to do is to
		 * give the Bead a callback method to make notes.
		 * 
		 * This example is more sophisticated than the previous
		 * ones. It uses nested code.
		 */
		clock = new Clock(ac, 700);
		clock.addMessageListener(
				//this is the on-the-fly bead
				new Bead() {
					//this is the method that we override to make the Bead do something
					int pitch;
					public void messageReceived(Bead message) {
						Clock c = (Clock) message;
						p.debugView.setValue("c.getCount()", ((c.getCount() / 4) % 8) + 1);
						
						if(c.getCount() % 4 == 0 && MathUtil.randBooleanWeighted(0.65f)) {
							// hi hat
							SamplePlayer samplePlayer03 = (c.getCount() % 8 == 0) ? new SamplePlayer(ac, sample04) : new SamplePlayer(ac, sample03);
							Gain g = new Gain(ac, 2, 0.6f);
							g.addInput(samplePlayer03);
							ac.out.addInput(g);
							samplePlayer03.start();
						}
						if(c.isBeat()) {
							//choose some nice frequencies
							if(random(1) < 0.5) return;
							pitch = Pitch.forceToScale((int)random(12), Pitch.dorian);
							float freq = Pitch.mtof(pitch + (int)random(5) * 12 + 32);
							WavePlayer wp = new WavePlayer(ac, freq, Buffer.SINE);
							Gain g = new Gain(ac, 1, new Envelope(ac, 0));
							g.addInput(wp);
							ac.out.addInput(g);
							((Envelope)g.getGainEnvelope()).addSegment(0.1f, random(200));
							((Envelope)g.getGainEnvelope()).addSegment(0, random(7000), new KillTrigger(g));
						}
						if(c.getCount() % 4 == 0) {
							//choose some nice frequencies
							int pitchAlt = pitch;
							if(random(1) < 0.2) pitchAlt = Pitch.forceToScale((int)random(12), Pitch.dorian) + (int)random(2) * 12;
							float freq = Pitch.mtof(pitchAlt + 32);
							WavePlayer wp = new WavePlayer(ac, freq, Buffer.SQUARE);
							Gain g = new Gain(ac, 1, new Envelope(ac, 0));
							g.addInput(wp);
							Panner p = new Panner(ac, random(1));
							p.addInput(g);
							ac.out.addInput(p);
							((Envelope)g.getGainEnvelope()).addSegment(random(0.1f), random(50));
							((Envelope)g.getGainEnvelope()).addSegment(0, random(400), new KillTrigger(p));
						}
						if(c.getCount() % 16 == 0) {
							if(MathUtil.randBooleanWeighted(0.65f)) {
								// kick drum
								SamplePlayer samplePlayer01 = new SamplePlayer(ac, sample01);
								Gain g = new Gain(ac, 2, 0.8f);
								g.addInput(samplePlayer01);
								ac.out.addInput(g);
								samplePlayer01.start();
							} else {
								// snare
								SamplePlayer samplePlayer02 = new SamplePlayer(ac, sample02);
								Gain g = new Gain(ac, 2, 0.8f);
								g.addInput(samplePlayer02);
								ac.out.addInput(g);
								samplePlayer02.start();
							}
							{
								// percussive bitcrushed sounds
								Noise n = new Noise(ac);
								Gain g = new Gain(ac, 1, new Envelope(ac, 0.05f));
								g.addInput(n);
								Panner p = new Panner(ac, random(0.5f, 1));
								p.addInput(g);
								ac.out.addInput(p);
								((Envelope)g.getGainEnvelope()).addSegment(0, random(100), new KillTrigger(p));
							}
						}
					}
				}
			);
		ac.out.addDependent(clock);
		ac.start();
	}

	public void drawApp() {
		background(0);
		stroke(255, 102, 204);

		// change tempo
		//	clock.setValue();
//		int ticksPerBeat = 6 + 18 * P.round(p.mousePercentX());
//		clock.setTicksPerBeat(ticksPerBeat);
		clock.getIntervalEnvelope().setValue(1000f - 700 * p.mousePercentX());

		// visualize
		loadPixels();
		//set the background
		Arrays.fill(pixels, back);
		//scan across the pixels
//		for(int i = 0; i < width; i++) {
//			//for each pixel work out where in the current audio buffer we are
//			int buffIndex = i * ac.getBufferSize() / width;
//			//then work out the pixel height of the audio data at that point
//			int vOffset = (int)((1 + ac.out.getValue(0, buffIndex)) * height / 2);
//			//draw into Processing's convenient 1-D array of pixels
//			vOffset = min(vOffset, height);
//			pixels[vOffset * height + i] = fore;
//		}
		updatePixels();

	}
}
