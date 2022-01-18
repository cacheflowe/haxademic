package com.haxademic.demo.media.audio.playback.beads;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.playback.WavPlayer;

import beads.AudioContext;
import beads.Glide;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;
import beads.SamplePlayer.LoopType;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Beads_WavLoopPointsMetronome
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Metronome metronome;
	protected Sample sample;
	protected SamplePlayer sp;
	protected float sampleQuarter;
	protected Glide glide;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		// Send Beads audio player analyzer to PAppletHax
		// Connect to global AudioIn
		AudioUtil.setPrimaryMixer();

		// build metronome
		metronome = new Metronome();
		metronome.togglePlay();
		P.store.setNumber(Interphase.BPM, 96);
		Interphase.TEMPO_MOUSE_CONTROL = true;
		
		
		// from: https://github.com/moumar/beads/blob/master/src/beads_examples/net/beadsproject/beads/ugens/UsingSamplePlayer.java
		// and: https://github.com/moumar/beads/blob/master/src/beads_examples/net/beadsproject/beads/data/UsingSample.java
		// Create our audio context.
		AudioContext ac = Metronome.ac;
		AudioIn.instance(new AudioInputBeads(ac));

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
//		sp.setLoopCrossFade(100);
		sp.getLoopStartUGen().setValue(sampleQuarter * 1);
		sp.getLoopEndUGen().setValue(sampleQuarter * 4);
		
		
		// set pitch if needed
		int glideTime = 0;
		float pitchOffset = WavPlayer.pitchRatioFromIndex(0);
		glide = new Glide(ac, pitchOffset);
		glide.setKillListener(sp);
		glide.setGlideTime(glideTime);
		
		// set pitch
		sp.setRate(glide);
		
		// // get sample output going
		ac.out.addInput(sp);
		sp.start();

		P.store.addListener(this);
	}


	protected void drawApp() {
		p.background(0);
		DebugView.setValue("AudioContext :: numinputs", Metronome.ac.out.getConnectedInputs().size());
		
		// repitch based on current bpm
		float bpm = P.store.getNumber(Interphase.BPM).floatValue();
		Metronome.shiftPitchToMatchBpm(sp, glide, bpm * 2, 8);

	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '4') {
			sp.start();
		}
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


	////////////////////////////////////////////
	// IAppStoreListeners
	////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.BEAT)) {
			if(val.intValue() % 4 == 0) {
				// chop it up on the beat!
				sampleQuarter = (float) sample.getLength() / 4f;
				float startTime = sampleQuarter * MathUtil.randRange(0, 2);
				sp.getLoopStartUGen().setValue(startTime);
				sp.getLoopEndUGen().setValue(startTime + sampleQuarter);
				sp.setPosition(startTime);
			}
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
