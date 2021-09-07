package com.haxademic.core.hardware.midi;

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
		instance = new MidiDevice(midiDeviceName, null);
		return instance;
	}
	
	public static MidiDevice init(String midiDeviceName, SimpleMidiListener delegate) {
		if(instance != null) return instance;
		instance = new MidiDevice(midiDeviceName, delegate);
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
	
	public MidiDevice(int midiDeviceInIndex, int midiDeviceOutIndex, SimpleMidiListener delegate) {
		printDevices();
		new Thread(new Runnable() { public void run() {
			midiBus = new MidiBus(this, midiDeviceInIndex, midiDeviceOutIndex);
			midiBus.addMidiListener((MidiListener) MidiState.instance());
			if(delegate != null) {
				midiBus.addMidiListener(delegate);
			}
		}}).start();
	}
	
	public MidiDevice(String midiDeviceName, SimpleMidiListener delegate) {
		printDevices();		
		new Thread(new Runnable() { public void run() {
			midiBus = new MidiBus(this, midiDeviceName, midiDeviceName);
			midiBus.addMidiListener((MidiListener) MidiState.instance());
			if(delegate != null) {
				midiBus.addMidiListener(delegate);
			}
		}}).start();
	}
	
	public void sendMidiOut(boolean isNoteOn, int channel, int note, int velocity) {
		if(isNoteOn) {
			midiBus.sendNoteOn(channel, note, velocity);
		} else {
			midiBus.sendNoteOff(channel, note, velocity);
		}
	}
	
}