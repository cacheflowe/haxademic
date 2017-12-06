package com.haxademic.core.hardware.midi;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.shared.ButtonState;

import themidibus.MidiListener;
import themidibus.SimpleMidiListener;

public class MidiState implements SimpleMidiListener {
	
	protected HashMap<Integer, Integer> midiButtonVal = new HashMap<Integer, Integer>();
	protected HashMap<Integer, ButtonState> midiButtons = new HashMap<Integer, ButtonState>();
	protected HashMap<Integer, Integer> midiCC = new HashMap<Integer, Integer>();
	protected HashMap<Integer, ButtonState> midiCCState = new HashMap<Integer, ButtonState>();
	
	public MidiState() {
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
	
	public float midiButtonValue( int pitch ) {
		return (midiButtonVal.containsKey(pitch)) ? (float) midiButtonVal.get(pitch) / 127f : 0;
	}
	
	public float midiCCPercent( int channel, int pitch ) {
		return (midiCC.containsKey(pitch)) ? (float) midiCC.get(pitch) / 127f : 0;
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
	
	public void update() {
		for (Integer key : midiButtons.keySet()) {
			if(midiButtons.get(key) == ButtonState.TRIGGER) midiButtons.put(key, ButtonState.ON);
		}
		for (Integer key : midiCCState.keySet()) {
			if(midiCCState.get(key) == ButtonState.TRIGGER) midiCCState.put(key, ButtonState.ON);
		}
	}
	
	///////////////////////////////
	// PUBLIC GETTERS
	///////////////////////////////
	
	public boolean isMidiButtonTriggered(int pitch) {
		return (midiButtons.containsKey(pitch) && midiButtons.get(pitch) == ButtonState.TRIGGER);
	}
	
	public boolean isMidiButtonOn(int pitch) {
		return (midiButtons.containsKey(pitch) && (midiButtons.get(pitch) == ButtonState.TRIGGER || midiButtons.get(pitch) == ButtonState.ON));
	}

	public boolean isMidiCCTriggered(int pitch) {
		return (midiCCState.containsKey(pitch) && midiCCState.get(pitch) == ButtonState.TRIGGER);
	}

	
	///////////////////////////////
	// MIDI LISTENER
	///////////////////////////////

	@Override
	public void controllerChange(int channel, int  pitch, int velocity) {
		// TODO Auto-generated method stub
		if(P.p.showDebug) P.println("controllerChange", channel, pitch, velocity);
		midiCC.put(pitch, velocity);
		ButtonState newState = (velocity == 0) ? ButtonState.OFF : ButtonState.TRIGGER;
		midiCCState.put(pitch, newState);
	}

	@Override
	public void noteOff(int channel, int  pitch, int velocity) {
		// TODO Auto-generated method stub
		if(P.p.showDebug) P.println("noteOff", channel, pitch, velocity);
		midiButtonVal.put(pitch, velocity);
		midiButtons.put(pitch, ButtonState.OFF);
	}

	@Override
	public void noteOn(int channel, int pitch, int velocity) {
		// TODO Auto-generated method stub
		if(P.p.showDebug) P.println("noteOn", channel, pitch, velocity);
		midiButtonVal.put(pitch, velocity);
		midiButtons.put(pitch, ButtonState.TRIGGER);

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
			if(midiCCState.get(key) == ButtonState.TRIGGER) {
				debugStr += key + ": TRIGGER \n";
			} else {
				debugStr += key + ": " + midiCC.get(key) + "\n";
			}
		}
		P.p.text(debugStr, 420, 20, P.p.width - 40, P.p.height - 40);
	}
	

	
//	public void sendMidiOut(boolean isNoteOn, int channel, int note, int velocity) {
//		_midiHandler.sendMidiOut(isNoteOn, channel, note, velocity);
//	}

}
