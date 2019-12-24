package com.haxademic.core.hardware.midi;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.shared.InputTrigger.InputState;

import themidibus.SimpleMidiListener;

public class MidiState
implements SimpleMidiListener {

	protected HashMap<Integer, Integer> midiNoteVals = new HashMap<Integer, Integer>();
	protected HashMap<Integer, InputState> midiNoteStates = new HashMap<Integer, InputState>();
	protected HashMap<Integer, Integer> midiCCVals = new HashMap<Integer, Integer>();
	protected HashMap<Integer, InputState> midiCCState = new HashMap<Integer, InputState>();
	protected int lastUpdatedFrame = 0;

	// Singleton instance
	
	public static MidiState instance;
	
	public static MidiState instance() {
		if(instance != null) return instance;
		instance = new MidiState();
		return instance;
	}
	
	// Constructor

	public MidiState() {
		P.p.registerMethod(PRegisterableMethods.pre, this);
		P.p.registerMethod(PRegisterableMethods.post, this);
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
		
	public void pre() {
		// if the incoming MIDI signal was collected before post(), we want to clear it out here,
		// ... otherwise external objects checking TRIGGERED will get it on 2 consecutive frames.
		// `lastUpdatedFrame` helps us properly clear this out and not negate the state on a single frame
		updateTriggerState();
	}
	
	public void post() {
		logValues();
		updateTriggerState();	// do this after log so we can see TRIGGER state
	}
	
	protected void updateTriggerState() {
		if(P.p.frameCount == lastUpdatedFrame) return;
		for (Integer key : midiNoteStates.keySet()) {
			if(midiNoteStates.get(key) == InputState.TRIGGER) midiNoteStates.put(key, InputState.ON);
		}
		for (Integer key : midiCCState.keySet()) {
			if(midiCCState.get(key) == InputState.TRIGGER) midiCCState.put(key, InputState.ON);
		}
	}
	
	protected void logValues() {
		// debug print values if debug window is showing
		if(DebugView.active()) {
			// log buttons
			for (Integer key : midiNoteStates.keySet()) {
				DebugView.setValue("MIDI Note ["+key+"]", midiNoteVals.get(key) + " | " + midiNoteStates.get(key).name());
			}
			// log CC values
			for (Integer key : midiCCVals.keySet()) {
				DebugView.setValue("MIDI CC ["+key+"]", midiCCVals.get(key) + " | " + midiCCState.get(key).name());
			}
		}
	}
	
	///////////////////////////////
	// GETTERS
	///////////////////////////////
	
	public float midiNoteValue( int pitch ) {
		return (midiNoteVals.containsKey(pitch)) ? (float) midiNoteVals.get(pitch) / 127f : 0;
	}
	
	public float midiCCValue(int pitch) {
		return (midiCCVals.containsKey(pitch)) ? midiCCVals.get(pitch) : 0;
	}
	
	public float midiCCNormalized(int pitch) {
		return (midiCCVals.containsKey(pitch)) ? (float) midiCCVals.get(pitch) / 127f : 0;
	}
	
	public float midiCCNormalized(int channel, int pitch) {
		// P.println("TODO: add midi channel handling in MidiState");
		return midiCCNormalized(pitch);
	}
	
	///////////////////////////////
	// PUBLIC GETTERS
	///////////////////////////////
	
	public boolean isMidiNoteTriggered(int pitch) {
		return (midiNoteStates.containsKey(pitch) && midiNoteStates.get(pitch) == InputState.TRIGGER);
	}
	
	public boolean isMidiNoteOn(int pitch) {
		return (midiNoteStates.containsKey(pitch) && (midiNoteStates.get(pitch) == InputState.TRIGGER || midiNoteStates.get(pitch) == InputState.ON));
	}

	public boolean isMidiCCTriggered(int pitch) {
		return (midiCCState.containsKey(pitch) && midiCCState.get(pitch) == InputState.TRIGGER);
	}
	
	public boolean isMidiCCOn(int pitch) {
		return (midiCCState.containsKey(pitch) && (midiCCState.get(pitch) == InputState.TRIGGER || midiCCState.get(pitch) == InputState.ON));
	}

	
	///////////////////////////////
	// MIDI LISTENER
	///////////////////////////////

	@Override
	public void controllerChange(int channel, int  pitch, int velocity) {
		InputState newState = (velocity == 0) ? InputState.OFF : InputState.TRIGGER;
		midiCCVals.put(pitch, velocity);
		midiCCState.put(pitch, newState);
		lastUpdatedFrame = P.p.frameCount;
	}

	@Override
	public void noteOff(int channel, int  pitch, int velocity) {
		midiNoteVals.put(pitch, velocity);
		midiNoteStates.put(pitch, InputState.OFF);
		lastUpdatedFrame = P.p.frameCount;
	}

	@Override
	public void noteOn(int channel, int pitch, int velocity) {
		midiNoteVals.put(pitch, velocity);
		midiNoteStates.put(pitch, InputState.TRIGGER);
		lastUpdatedFrame = P.p.frameCount;
	}
}
