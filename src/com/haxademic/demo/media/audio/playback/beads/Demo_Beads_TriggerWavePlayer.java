package com.haxademic.demo.media.audio.playback.beads;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;

import beads.AudioContext;
import beads.Buffer;
import beads.Envelope;
import beads.Gain;
import beads.KillTrigger;
import beads.WavePlayer;

public class Demo_Beads_TriggerWavePlayer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		// Send Beads audio player analyzer to PAppletHax
		// Connect to global AudioIn
		AudioUtil.setPrimaryMixer();
		AudioIn.instance(new AudioInputBeads(WavPlayer.sharedContext()));
		
		// from: https://github.com/moumar/beads/blob/master/src/beads_examples/net/beadsproject/beads/events/UsingTriggers.java
		// Create our audio context.
		AudioContext ac = WavPlayer.sharedContext();
		ac.start();
	}
	
	protected void playSawDown() {
		AudioContext ac = WavPlayer.sharedContext();
		/*
		 * Here's a common use of Triggers you've 
		 * probably seen elsewhere already.
		 * 
		 * Set some stuff up. An Envelope controlling
		 * the frequency of a WavePlayer, which is being
		 * attenuated by a Gain. See elsewhere if you
		 * don't know what this is all about yet.
		 */
		Envelope freqEnv = new Envelope(ac, MathUtil.randRangeDecimal(200, 500));
		WavePlayer wp = new WavePlayer(ac, freqEnv, Buffer.SAW);
		Gain g = new Gain(ac, 1, 0.1f);
		g.addInput(wp);
		ac.out.addInput(g);
		/*
		 * Here's the critical bit. Then Envelope triggers
		 * a KillTrigger when it completes the following 
		 * Segment. The KillTrigger is assigned to kill the
		 * Gain object g, which means that the sound stops.
		 * 
		 * Not that Envelope, Gain and KillTrigger are all 
		 * instances of Bead. Bead can be passed messages,
		 * can be passed as a message, can kill and
		 * can be killed. In Beads, everything is a Bead.
		 * It's a little bit sick, really.
		 */
		freqEnv.addSegment(50, 1000, new KillTrigger(g));
		
		
		/*
		 * Notice that it is only the Gain that has been killed.
		 * So AudioContext is still running. 
		 * 
		 * That is, until...
		 */
		/*
		DelayTrigger dt = new DelayTrigger(ac, 2000, new AudioContextStopTrigger(ac));
		ac.out.addDependent(dt);
		*/
	}


	protected void drawApp() {
		p.background(0);
		DebugView.setValue("AudioContext :: numinputs", WavPlayer.sharedContext().out.getConnectedInputs().size());
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') playSawDown();
	}
	
}
