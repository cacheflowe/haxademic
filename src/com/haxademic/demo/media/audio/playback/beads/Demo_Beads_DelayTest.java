package com.haxademic.demo.media.audio.playback.beads;

import java.io.IOException;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;

import beads.AudioContext;
import beads.Bead;
import beads.Buffer;
import beads.Clock;
import beads.Envelope;
import beads.Gain;
import beads.Glide;
import beads.Panner;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;
import beads.TapIn;
import beads.TapOut;
import beads.WavePlayer;

public class Demo_Beads_DelayTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected WavePlayer wp;
	protected Gain g;
	protected Glide gainGlide;
	protected Glide frequencyGlide;

	protected SamplePlayer sp1;

	protected TapIn delayIn;
	protected TapOut delayOut;
	protected Gain delayGain;

	protected TapIn delayIn2;
	protected TapOut delayOut2;
	protected Gain delayGain2;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		// Send Beads audio player analyzer to PAppletHax
		// Connect to global AudioIn
		AudioUtil.setPrimaryMixer();
		AudioIn.instance(new AudioInputBeads(WavPlayer.sharedContext()));
		AudioContext ac = WavPlayer.sharedContext();
		
		// from:
		// https://forum.processing.org/one/topic/beads-volume-issue.html

		// build audio generator chain
		gainGlide = new Glide(ac, 1.0f, 50);
		frequencyGlide = new Glide(ac, 20, 50);
		wp = new WavePlayer(ac, frequencyGlide, Buffer.SINE);
		g = new Gain(ac, 1, gainGlide);
		// g.addInput(wp);

		// build delay chain
		delayIn = new TapIn(ac, 3000);
		delayIn.addInput(g);
		delayOut = new TapOut(ac, delayIn, 500.0f);
		delayGain = new Gain(ac, 1, 0.30f);
		delayGain.addInput(delayOut);

		delayIn2 = new TapIn(ac, 3000);
		delayIn2.addInput(g);
		delayOut2 = new TapOut(ac, delayIn2, 500.0f);
		delayGain2 = new Gain(ac, 1, 0.15f);
		delayGain2.addInput(delayOut2);

		// send both original signal & delay to output
		ac.out.addInput(g);
		ac.out.addInput(delayGain);
		ac.out.addInput(delayGain2);

		ac.start();
	}

	protected void drawApp() {
		p.background(0);
		// gainGlide.setValue(mouseX / (float) width);
		// gainGlide.setGlideTime(100);
		frequencyGlide.setValue(mouseY);
		// frequencyGlide.setGlideTime(100);
		delayOut.setDelay(250 * mouseX / (float) width);
		delayOut2.setDelay(500 * mouseX / (float) width);
		DebugView.setValue("AudioContext :: numinputs", WavPlayer.sharedContext().out.getConnectedInputs().size());
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			sp1 = new SamplePlayer(WavPlayer.sharedContext(), SampleManager.sample(("data/audio/kit808/snare.wav")));
			sp1.setKillOnEnd(true);
			g.addInput(sp1);

			sp1.start();
		}
	}
	
}
