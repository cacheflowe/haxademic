package com.haxademic.demo.media.audio.playback.beads;

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
import beads.Panner;
import beads.WavePlayer;

public class Demo_Beads_SineWave
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
		
		// Moving bloops.
		// from: https://github.com/moumar/beads/blob/master/src/beads_examples/net/beadsproject/beads/ugens/UsingPanner.java
		// Create our audio context.
		AudioContext ac = WavPlayer.sharedContext();
		
		
		// set the number of WavePlayers 
		int count = 3;

		// initialize the arrays
		WavePlayer[] wp = new WavePlayer[count];
		Gain[] g = new Gain[count];

		// use a loop to set up each WavePlayer
		for( int i = 0; i < count; i++ )
		{
			// create the WavePlayer and the Gain - use math to set frequence / volume
			// try changing Buffer.SINE to Buffer.SQUARE
			wp[i] = new WavePlayer(ac, 220.0f * (i+1), Buffer.SINE);
			g[i] = new Gain(ac, 1, 1.0f / (i+1));
			
			// connect the WavePlayer to the Gain, and the Gain to ac.out
			g[i].addInput(wp[i]);
			ac.out.addInput(g[i]);
		}
		ac.start();

	}


	protected void drawApp() {
		p.background(0);
		DebugView.setValue("AudioContext :: numinputs", WavPlayer.sharedContext().out.getConnectedInputs().size());
	}

	public void keyPressed() {
		super.keyPressed();
//		if(p.key == '5') ambientLoop.soundForceStop();
	}
	
}
