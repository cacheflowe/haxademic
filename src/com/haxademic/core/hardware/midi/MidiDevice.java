package com.haxademic.core.hardware.midi;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.shared.InputState;

import themidibus.MidiListener;
import themidibus.SimpleMidiListener;

public class MidiDevice 
implements SimpleMidiListener {
	
	protected HashMap<Integer, Integer> midiButtonVal = new HashMap<Integer, Integer>();
	protected HashMap<Integer, InputState> midiButtons = new HashMap<Integer, InputState>();
	protected HashMap<Integer, Integer> midiCC = new HashMap<Integer, Integer>();
	protected HashMap<Integer, InputState> midiCCState = new HashMap<Integer, InputState>();
	protected int lastUpdatedFrame = 0;

	public MidiDevice() {
		if(P.p.midiBus != null) P.p.midiBus.addMidiListener((MidiListener)this);

//		new Thread(new Runnable() { public void run() {
//			_midiHandler = new MidiHandler();
//		}}).start();

//		Thread t = new Thread();
//		t.setPriority(Thread.MIN_PRIORITY);
//		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable ex) {
//                DebugUtil.printErr("UncaughtExceptionHandler on MidiHandler Thread");
//            }
//        });
//		t.start();
	}
	
	public void sendMidiOut(boolean isNoteOn, int channel, int note, int velocity) {
		if(isNoteOn) {
			P.p.midiBus.sendNoteOn(channel, note, velocity);
		} else {
			P.p.midiBus.sendNoteOff(channel, note, velocity);
		}
	}


	///////////////////////////////
	// GETTERS
	///////////////////////////////
	
	public float midiButtonValue( int pitch ) {
		return (midiButtonVal.containsKey(pitch)) ? (float) midiButtonVal.get(pitch) / 127f : 0;
	}
	
	public float midiCCPercent(int pitch) {
		return (midiCC.containsKey(pitch)) ? (float) midiCC.get(pitch) / 127f : 0;
	}
	
	public float midiCCPercent(int channel, int pitch) {
		// P.println("TODO: add midi channel handling in MidiState");
		return midiCCPercent(pitch);
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
	
	public void update() {
		if(P.p.frameCount == lastUpdatedFrame) return;
		for (Integer key : midiButtons.keySet()) {
			if(midiButtons.get(key) == InputState.TRIGGER) midiButtons.put(key, InputState.ON);
		}
		for (Integer key : midiCCState.keySet()) {
			if(midiCCState.get(key) == InputState.TRIGGER) midiCCState.put(key, InputState.ON);
		}
	}
	
	///////////////////////////////
	// PUBLIC GETTERS
	///////////////////////////////
	
	public boolean isMidiButtonTriggered(int pitch) {
		return (midiButtons.containsKey(pitch) && midiButtons.get(pitch) == InputState.TRIGGER);
	}
	
	public boolean isMidiButtonOn(int pitch) {
		return (midiButtons.containsKey(pitch) && (midiButtons.get(pitch) == InputState.TRIGGER || midiButtons.get(pitch) == InputState.ON));
	}

	public boolean isMidiCCTriggered(int pitch) {
		return (midiCCState.containsKey(pitch) && midiCCState.get(pitch) == InputState.TRIGGER);
	}

	
	///////////////////////////////
	// MIDI LISTENER
	///////////////////////////////

	@Override
	public void controllerChange(int channel, int  pitch, int velocity) {
		midiCC.put(pitch, velocity);
		InputState newState = (velocity == 0) ? InputState.OFF : InputState.TRIGGER;
		midiCCState.put(pitch, newState);
		if(P.p.debugView.active()) P.println("controllerChange", channel, pitch, velocity, newState);
		lastUpdatedFrame = P.p.frameCount;
	}

	@Override
	public void noteOff(int channel, int  pitch, int velocity) {
		if(P.p.debugView.active()) P.println("noteOff", channel, pitch, velocity);
		midiButtonVal.put(pitch, velocity);
		midiButtons.put(pitch, InputState.OFF);
		lastUpdatedFrame = P.p.frameCount;
	}

	@Override
	public void noteOn(int channel, int pitch, int velocity) {
		if(P.p.debugView.active()) P.println("noteOn", channel, pitch, velocity);
		midiButtonVal.put(pitch, velocity);
		midiButtons.put(pitch, InputState.TRIGGER);
		lastUpdatedFrame = P.p.frameCount;
	}
	
	///////////////////////////////
	// DEBUG
	///////////////////////////////

	public void printButtons() {
		P.p.noStroke();
		P.p.fill(255);
		String debugStr = "";
		for (Integer key : midiButtons.keySet()) {
			debugStr += key + ": " + midiButtons.get(key) + "\n";
		}
		P.p.text(debugStr, 220, 20, P.p.width - 40, P.p.height - 40);
	}

	public void printCC() {
		P.p.noStroke();
		P.p.fill(255);
		String debugStr = "";
		for (Integer key : midiCC.keySet()) {
			if(midiCCState.get(key) == InputState.TRIGGER) {
				debugStr += key + ": TRIGGER \n";
			} else {
				debugStr += key + ": " + midiCC.get(key) + "\n";
			}
		}
		P.p.text(debugStr, 320, 20, P.p.width - 40, P.p.height - 40);
	}
	
}
