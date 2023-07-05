package com.haxademic.demo.media.audio.playback.beads;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.system.Console;

import beads.AudioContext;
import beads.GranularSamplePlayer;
import beads.Sample;
import beads.SamplePlayer;
import beads.Static;

public class Demo_Beads_GranularSynth
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected GranularSamplePlayer gsp;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		// Send Beads audio player analyzer to PAppletHax
		// Connect to global AudioIn
		AudioUtil.setPrimaryMixer();
		AudioIn.instance(new AudioInputBeads(WavPlayer.sharedContext()));
		AudioContext ac = WavPlayer.sharedContext();
		
		
    // load the source sample from a file
    Sample sourceSample = null;
    try
    {
      sourceSample = new Sample("data/audio/communichords/bass/operator-detuned-lowpass-bass-lower.wav");
    }
    catch(Exception e)
    {
      /*
       * If the program exits with an error message,
       * then it most likely can't find the file
       * or can't open it. Make sure it is in the
       * root folder of your project in Eclipse.
       * Also make sure that it is a 16-bit,
       * 44.1kHz audio file. These can be created
       * using Audacity.
       */
      P.error(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    
    // instantiate a GranularSamplePlayer
    gsp = new GranularSamplePlayer(ac, sourceSample);
    
    // tell gsp to loop the file
    gsp.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
    
    // set the grain size to a fixed 10ms
    gsp.setGrainSize(new Static(ac, 50.0f));
    
    // tell gsp to behave somewhat randomly
    // gsp.setRandomness(new Static(ac, 100.0f));
    
    // connect gsp to ac
    ac.out.addInput(gsp);
		// gsp.setLoopStart(50);
		// gsp.setLoopEnd(250);
    
    // begin audio processing
    ac.start();
	}


	protected void drawApp() {
		p.background(0);
		DebugView.setValue("AudioContext :: numinputs", WavPlayer.sharedContext().out.getConnectedInputs().size());

		gsp.setLoopCrossFade(10);
	}

	public void keyPressed() {
		super.keyPressed();
		float randAmp = p.random(0, 1000);
		randAmp = 0;
		P.outColor(Console.BLUE_BACKGROUND, randAmp);
    gsp.setRandomness(new Static(WavPlayer.sharedContext(), randAmp));
		gsp.setPosition(0);
		gsp.setLoopPointsFraction(0, 0.2f);
		// gsp.setGrainSize(new Static(WavPlayer.sharedContext(), 80.0f));
	}
	
}
