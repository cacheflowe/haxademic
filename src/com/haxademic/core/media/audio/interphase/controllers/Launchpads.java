package com.haxademic.core.media.audio.interphase.controllers;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.hardware.midi.devices.LaunchPad;
import com.haxademic.core.hardware.midi.devices.LaunchPad.ILaunchpadCallback;
import com.haxademic.core.hardware.midi.devices.LaunchPadMini;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Sequencer;

import processing.core.PGraphics;
import processing.core.PImage;
import themidibus.MidiBus;

public class Launchpads
implements IAppStoreListener, ILaunchpadCallback {
  
  protected Interphase interphase;
  protected LaunchPadMini launchpad1;
  protected LaunchPadMini launchpad2;

  public Launchpads(Interphase interphase) {
    this.interphase = interphase;
    P.store.addListener(this);
    initLaunchpads("MIDIIN2 (LPMiniMK3 MIDI)", "MIDIOUT2 (LPMiniMK3 MIDI)", "MIDIIN4 (LPMiniMK3 MIDI)", "MIDIOUT4 (LPMiniMK3 MIDI)");
  }
	
	public void initLaunchpads(int midiIn1, int midiOut1, int midiIn2, int midiOut2) {
		MidiBus.list();
		launchpad1 = new LaunchPadMini(midiIn1, midiOut1);
		launchpad1.setDelegate(this);
		launchpad2 = new LaunchPadMini(midiIn2, midiOut2);
		launchpad2.setDelegate(this);
	}
	
	public void initLaunchpads(String device1In, String device1Out, String device2In, String device2Out) {
		MidiBus.list();
		launchpad1 = new LaunchPadMini(device1In, device1Out);
		launchpad1.setDelegate(this);
		launchpad2 = new LaunchPadMini(device2In, device2Out);
		launchpad2.setDelegate(this);
	}
	
	protected void updateLaunchpads() {
		if(launchpad1 == null) return;
		// split across launchpads
		int curStep = P.store.getInt(Interphase.CUR_STEP);
		int nextStep = (P.store.getInt(Interphase.CUR_STEP) + 1) % Interphase.NUM_STEPS;
		for (int i = 0; i < interphase.sequencers.length; i++) {
			for (int step = 0; step < Interphase.NUM_STEPS; step++) {
        Sequencer sequencer = interphase.sequencers[i];

        // choose button color based on sequencer state
        boolean stepActive = sequencer.stepActive(step);
				float value = stepActive ? 1 : 0; 

        // handle special cases for button color
				float adjustedVal = value;
				if(sequencer.manuallyTriggered() && step == nextStep) adjustedVal = 0.87f;
				if(sequencer.muted() && stepActive) adjustedVal = 0.2f;
				if(value == 0 && step % 4 == 0) adjustedVal = 0.3f;	// show grid divisor by 4
				if(value == 0 && step == curStep) adjustedVal = 0.65f;	// show playhead row

        // split 16 steps between 2 launchpads
				if(step <= 7) {
					launchpad1.setButton(i, step, adjustedVal);
				} else {
					launchpad2.setButton(i, step - 8, adjustedVal);
				}
			}
		}
	}

  //////////////////////////
  // ILaunchpadCallback updates
  //////////////////////////

  public void cellUpdated(LaunchPad launchpad, int x, int y, float value) {
    // apply toggle button press
    int launchpadNumber = (launchpad == launchpad1) ? 1 : 2;
    P.out("LaunchPads.cellUpdated()", launchpadNumber, x, y, value);
    int step = (launchpadNumber == 1) ? y : 8 + y;
    boolean isActive = (value == 1f);
    if (x < Interphase.NUM_CHANNELS)
      interphase.sequencers[x].stepActive(step, isActive);
  }

  public void noteOn(LaunchPad launchpad, int note, float value) {
    int launchpadNumber = (launchpad == launchpad1) ? 1 : 2;
    P.out("Interphase.noteOn", launchpadNumber, note, value);
    // special functions on original paunchpad.
    // launchpad mini doesn't send MIDI notes from 9th row/col of buttons
    if (launchpad1 instanceof LaunchPadMini == false) {
      if (launchpadNumber == 1) {
        for (int i = 0; i < Interphase.NUM_CHANNELS; i++) {
          if (note == LaunchPad.headerColMidiNote(i))
            interphase.sequencers[i].evolvePattern();
        }
      } else {
        // change sample
        for (int i = 0; i < Interphase.NUM_CHANNELS; i++) {
          if (note == LaunchPad.headerColMidiNote(i))
            interphase.sequencers[i].loadNextSound();
        }
        // bpm up/down
        int curBmpMIDI = P.store.getInt(Interphase.BPM);
        if (note == LaunchPad.groupRowMidiNote(1))
          P.store.setNumber(Interphase.BPM, curBmpMIDI - 1);
        if (note == LaunchPad.groupRowMidiNote(0))
          P.store.setNumber(Interphase.BPM, curBmpMIDI + 1);
      }
    }
  }

  //////////////////////////
  // IAppStoreListener updates
  //////////////////////////

  public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.BEAT)) updateLaunchpads(); // update launchpads with BEAT delay
  }
  public void updatedString(String key, String val) {}
  public void updatedBoolean(String key, Boolean val) {}
  public void updatedImage(String key, PImage val) {}
  public void updatedBuffer(String key, PGraphics val) {}

}
