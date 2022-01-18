package com.haxademic.demo.media.audio.playback.beads;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;

import beads.AudioContext;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;
import beads.SamplePlayer.LoopType;

public class Demo_Beads_WavLoopPoints
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Sample sample;
	protected SamplePlayer sp;
	protected float sampleQuarter;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		// Send Beads audio player analyzer to PAppletHax
		// Connect to global AudioIn
		AudioUtil.setPrimaryMixer();
		AudioContext ac = WavPlayer.sharedContext();
		AudioIn.instance(new AudioInputBeads(ac));
		
		// Moving bloops.
		// from: https://github.com/moumar/beads/blob/master/src/beads_examples/net/beadsproject/beads/ugens/UsingSamplePlayer.java
		// and: https://github.com/moumar/beads/blob/master/src/beads_examples/net/beadsproject/beads/data/UsingSample.java
		// Create our audio context.

		// load sample		
		sample = SampleManager.sample(FileUtil.getPath("audio/breakbeats/break02.wav"));
		P.out("Loaded sample from file");
		P.out("Name " + sample.getFileName());
		P.out("Known to friends as " + sample.getSimpleName());
		P.out("Length " + sample.getLength() + "ms");
		P.out("Channels " + sample.getNumChannels());
		P.out("----------------");
		sampleQuarter = (float) sample.getLength() / 4f;
		
		/*
		 * Choose a loop type.
		 */
		sp = new SamplePlayer(ac, sample);
		sp.setLoopType(LoopType.LOOP_FORWARDS);
		/*
		 * Set the loop points.
		 */
//		sp.setLoopCrossFade(100);
		sp.getLoopStartUGen().setValue(sampleQuarter * 1);
		sp.getLoopEndUGen().setValue(sampleQuarter * 3);
		//
		ac.out.addInput(sp);
		ac.start();
	}


	protected void drawApp() {
		p.background(0);
		DebugView.setValue("AudioContext :: numinputs", WavPlayer.sharedContext().out.getConnectedInputs().size());
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '5') {
			sp.getLoopStartUGen().setValue(sampleQuarter * 2);
			sp.getLoopEndUGen().setValue(sampleQuarter * 3);
		}
		if(p.key == '6') {
			sp.getLoopStartUGen().setValue(sampleQuarter * 3);
			sp.getLoopEndUGen().setValue(sampleQuarter * 4);
		}
		if(p.key == '7') {
			sp.getLoopStartUGen().setValue(sampleQuarter * 0);
			sp.getLoopEndUGen().setValue(sampleQuarter * 1);
		}
		if(p.key == '8') {
			sp.getLoopStartUGen().setValue(sampleQuarter * 0);
			sp.getLoopEndUGen().setValue(sampleQuarter * 2);
		}
		
	}
	
}
