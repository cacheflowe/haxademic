package com.haxademic.core.media.audio.interphase.controllers;

import java.awt.event.ActionListener;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ArrayUtil;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL.ILaunchControlXLCallback;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import themidibus.MidiBus;

public class LaunchControls
implements IAppStoreListener, ILaunchControlXLCallback {
  
  protected Interphase interphase;
  protected LaunchControlXL launchControl1;
  protected LaunchControlXL launchControl2;

  public LaunchControls(Interphase interphase) {
    this.interphase = interphase;
    P.store.addListener(this);
    // initLaunchControls("MIDIIN2 (LPMiniMK3 MIDI)", "MIDIOUT2 (LPMiniMK3 MIDI)", "MIDIIN4 (LPMiniMK3 MIDI)", "MIDIOUT4 (LPMiniMK3 MIDI)");
    initLaunchControls(3, 6, 9, 12);
  }
  
  public void initLaunchControls(int midiIn1, int midiOut1, int midiIn2, int midiOut2) {
    MidiBus.list();
    launchControl1 = new LaunchControlXL(midiIn1, midiOut1);
    launchControl1.setDelegate(this);
    launchControl2 = new LaunchControlXL(midiIn2, midiOut2);
    launchControl2.setDelegate(this);
    
  }
  
  public void initLaunchControls(String device1In, String device1Out, String device2In, String device2Out) {
    MidiBus.list();
    launchControl1 = new LaunchControlXL(device1In, device1Out);
    launchControl1.setDelegate(this);
    launchControl2 = new LaunchControlXL(device2In, device2Out);
    launchControl2.setDelegate(this);
  }

  // MIDI lights
  
  protected void updateLaunchControls() {
    for (int i = 0; i < interphase.sequencers.length; i++) {
      // set mute button LED
      boolean muted = interphase.sequencers[i].muted();
      if(muted) {
        launchControl1.setButtonRow2(i, 0.2f);
      } else {
        launchControl1.setButtonRow2(i, 0.8f);
      }
    }
  }

  protected void flashSampleManualTrigger(String key) {
      String sequencerNum = key.substring(Interphase.UI_TRIGGER_.length(), key.length() - 0);	// used to break after 9 channels, should work for higher numbers 
      int sequencerIndex = ConvertUtil.stringToInt(sequencerNum) - 1;	// use key to grab sample index
      launchControl1.setButtonRow1(sequencerIndex, 0.8f); // flash button green
      SystemUtil.setTimeout(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          launchControl1.setButtonRow1(sequencerIndex, 0); // set back to black
        }
      }, 100);
  }

  protected void flashSampleTrigger(int sequencerIndex) {
      launchControl1.setButtonRow1(sequencerIndex, 0.95f); // flash button green
      SystemUtil.setTimeout(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          launchControl1.setButtonRow1(sequencerIndex, 0); // set back to black
        }
      }, 100);
  }

  //////////////////////////
  // ILaunchpadCallback updates
  //////////////////////////

  public void noteOnLaunchControl(LaunchControlXL launchControl, int note, float value) {
    int launchControlNumber = (launchControl == launchControl1) ? 1 : 2;
    P.out("noteOn :: launchControlNumber", launchControlNumber, note, value);
    if (launchControl.isKnob(note)) {
      int noteVal = P.round(value) % launchControl.numColors();
    }

    // set UI values from local midi
    if(launchControlNumber == 1) {
      // sample selection
      if (ArrayUtil.indexOfInt(LaunchControlXL.KNOBS_ROW_1, note) != -1) {
        int sequencerIndex = ArrayUtil.indexOfInt(LaunchControlXL.KNOBS_ROW_1, note);
        String sampleKey = Interphase.UI_SAMPLE_+(sequencerIndex+1);
        int numSamples = interphase.sequencerAt(sequencerIndex).numSamples();
        UI.setValue(sampleKey, value / 127f * (numSamples - 1));
      }
      // manual trigger
      if (ArrayUtil.indexOfInt(LaunchControlXL.BUTTONS_1, note) != -1) {
        int sequencerIndex = ArrayUtil.indexOfInt(LaunchControlXL.BUTTONS_1, note);
        String sampleKey = Interphase.UI_TRIGGER_+(sequencerIndex+1);
        UI.setValue(sampleKey, 1);
      }
      // global tempo 
      if (note == LaunchControlXL.BUTTON_UP && value > 0) interphase.bpmDown(); // button sends noteOn with 0.0f value on release
      if (note == LaunchControlXL.BUTTON_DOWN && value > 0) interphase.bpmUp();
      // load json configs
      if (note == LaunchControlXL.BUTTON_LEFT && value > 0) interphase.prevConfig();
      if (note == LaunchControlXL.BUTTON_RIGHT && value > 0) interphase.nextConfig();
      // global play/pause
      if (note == LaunchControlXL.BUTTON_SIDE_4 && value > 0) interphase.togglePlay();
    }

    if(launchControlNumber == 2) {
      // manual evolve
      if (ArrayUtil.indexOfInt(LaunchControlXL.BUTTONS_1, note) != -1) {
        int sequencerIndex = ArrayUtil.indexOfInt(LaunchControlXL.BUTTONS_1, note);
        String evolveKey = Interphase.UI_EVOLVE_ + (sequencerIndex + 1);
        UI.setValue(evolveKey, 1);
      }
    }
  }

  //////////////////////////
  // IAppStoreListener updates
  //////////////////////////

  public void updatedNumber(String key, Number val) {
    // update launchpads with BEAT delay
    if(key.equals(Interphase.BEAT)) updateLaunchControls();
    if(key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) flashSampleTrigger(val.intValue());
    if(key.indexOf(Interphase.UI_TRIGGER_) == 0) flashSampleManualTrigger(key);
    
  }
  public void updatedString(String key, String val) {}
  public void updatedBoolean(String key, Boolean val) {}
  public void updatedImage(String key, PImage val) {}
  public void updatedBuffer(String key, PGraphics val) {}

}
