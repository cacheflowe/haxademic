package com.haxademic.sketch.audio.beads;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.shared.InputTrigger;

import beads.AudioContext;
import beads.Gain;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;

public class BeadsSamplePlayer
extends PAppletHax { 
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	AudioContext ac;
	Sample sample01;
	SamplePlayer samplePlayer01;
	protected InputTrigger trigger1 = new InputTrigger(new char[]{' '}, null, new Integer[]{43}, null, null );

	public void firstFrame() {
		ac = new AudioContext();

		// load a sample		
		sample01 = SampleManager.sample(FileUtil.getFile("audio/drums/booty-house.wav"));

		ac.start();
	}

	public void drawApp() {
		background(0);

		if(trigger1.triggered()) {
			playSampleRecycle();
			// playSampleRecreate();
		}
	}
	
	protected void playSampleRecycle() {
		if(samplePlayer01 != null) {
			P.println(samplePlayer01.getPosition());
			samplePlayer01.setPosition(000);
			samplePlayer01.start();
		} else {
			samplePlayer01 = new SamplePlayer(ac, sample01);
			samplePlayer01.setKillOnEnd(false);
			Gain g = new Gain(ac, 2, 0.8f);
			g.addInput(samplePlayer01);
			ac.out.addInput(g);
			samplePlayer01.start();
		}
	}
	
	protected void playSampleRecreate() {
		if(samplePlayer01 != null) {
			P.println(samplePlayer01.getPosition());
			samplePlayer01.kill();
		} 
		samplePlayer01 = new SamplePlayer(ac, sample01);
		samplePlayer01.setKillOnEnd(true);
		Gain g = new Gain(ac, 2, 0.8f);
		g.addInput(samplePlayer01);
		ac.out.addInput(g);
		samplePlayer01.start();
	}
}
