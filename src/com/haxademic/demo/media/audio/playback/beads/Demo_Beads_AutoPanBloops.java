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

public class Demo_Beads_AutoPanBloops
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
		// We'll use a nice, simple sine tone.
		WavePlayer sine = new WavePlayer(ac, 220, Buffer.SINE);

		// We'll make it bloop-y by multiplying it (using a Gain) by an
		// envelope that is triggered by a Clock.
		final Envelope envelope = new Envelope(ac, 0);
		Gain bloops = new Gain(ac, 1, envelope);
		bloops.addInput(sine);

		Clock clock = new Clock(ac, 300);
		clock.setTicksPerBeat(1);
		ac.out.addDependent(clock);

		Bead blooper = new Bead() {
			public void messageReceived(Bead message) {
				envelope.addSegment(1, 30);
				envelope.addSegment(.1f, 30);
				envelope.addSegment(0, 60);
			}
		};

		clock.addMessageListener(blooper);

		// Now we'll make the pan position move back and forth with a slow
		// sine wave.
		WavePlayer panPos = new WavePlayer(ac, 1, Buffer.SINE);


		// Create our Panner.
		// Use our slow sine wave as the position.
		Panner panner = new Panner(ac);
		panner.setPos(panPos);

		// Now add the bloops to the Panner's input.
		panner.addInput(bloops);

		// And send the output to the audio out.
		ac.out.addInput(panner);

		// Don't forget to start processing audio!
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
