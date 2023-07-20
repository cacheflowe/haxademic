package com.haxademic.core.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.system.Console;

import themidibus.MidiBus;
import themidibus.MidiListener;
import themidibus.SimpleMidiListener;

public class MidiDevice {
	
	public MidiBus midiBus;
	
	public static MidiDevice instance;
	public static boolean listedDevices = false;
	
	public static MidiDevice init(int midiDeviceInIndex, int midiDeviceOutIndex) {
		if(instance != null) return instance;
		instance = new MidiDevice(midiDeviceInIndex, midiDeviceOutIndex, null);
		return instance;
	}
	
	public static MidiDevice init(int midiDeviceInIndex, int midiDeviceOutIndex, SimpleMidiListener delegate) {
		if(instance != null) return instance;
		instance = new MidiDevice(midiDeviceInIndex, midiDeviceOutIndex, delegate);
		return instance;
	}

	public static MidiDevice init(String midiDeviceName) {
		if(instance != null) return instance;
		instance = new MidiDevice(midiDeviceName, midiDeviceName, null);
		return instance;
	}
	
	public static MidiDevice init(String midiDeviceName, SimpleMidiListener delegate) {
		if(instance != null) return instance;
		instance = new MidiDevice(midiDeviceName, midiDeviceName, delegate);
		return instance;
	}
	
	public static MidiDevice init(String midiDeviceInName, String midiDeviceOutName, SimpleMidiListener delegate) {
		if(instance != null) return instance;
		instance = new MidiDevice(midiDeviceInName, midiDeviceOutName, delegate);
		return instance;
	}
	
	public static MidiDevice instance() {
		return instance;
	}
	
	public static void printDevices() {
		if(!listedDevices) {
			MidiBus.list();
			listedDevices = true;
		}
	}
	
	public MidiDevice(int midiDeviceInIndex, SimpleMidiListener delegate) {
		this(midiDeviceInIndex, midiDeviceInIndex, delegate);
	}

	public MidiDevice(int midiDeviceInIndex, int midiDeviceOutIndex, SimpleMidiListener delegate) {
		printDevices();
		new Thread(new Runnable() { public void run() {
			midiBus = new MidiBus(this, midiDeviceInIndex, midiDeviceOutIndex);
			midiBus.addMidiListener((MidiListener) MidiState.instance());
			if(delegate != null) {
				midiBus.addMidiListener(delegate);
			}
			P.outColor(Console.GREEN_BACKGROUND, "MidiBus init by index:", midiDeviceInIndex, midiDeviceOutIndex);
		}}).start();
	}
	
	public MidiDevice(String midiDeviceInName, SimpleMidiListener delegate) {
		this(midiDeviceInName, midiDeviceInName, delegate);
	}

	public MidiDevice(String midiDeviceInName, String midiDeviceOutName, SimpleMidiListener delegate) {
		printDevices();		
		new Thread(new Runnable() { public void run() {
			midiBus = new MidiBus(this, midiDeviceInName, midiDeviceOutName);
			midiBus.addMidiListener((MidiListener) MidiState.instance());
			if(delegate != null) {
				midiBus.addMidiListener(delegate);
			}
			P.outColor(Console.GREEN_BACKGROUND, "MidiBus init by name:", midiDeviceInName, midiDeviceOutName);
		}}).start();
	}
	
	public void sendMidiOut(boolean isNoteOn, int channel, int note, int velocity) {
		if(midiBus == null) {
			DebugView.setValue("MidiDevice.midiBus not ready", P.p.frameCount);
			return;
		}
		if(isNoteOn) {
			midiBus.sendNoteOn(channel, note, velocity);
		} else {
			midiBus.sendNoteOff(channel, note, velocity);
		}
	}
	
}