package com.haxademic.core.hardware.midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import com.haxademic.core.app.P;

public class MidiHandler {

	// from: http://stackoverflow.com/questions/6937760/java-getting-input-from-midi-keyboard

    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	public ArrayList<Transmitter> allTransmitters;
    
	public MidiHandler() {
		initDevices();
	}
	
    protected void initDevices(){
    	MidiDevice device;
    	MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
    	
    	allTransmitters = new ArrayList<Transmitter>();
    	
    	for (int i = 0; i < infos.length; i++) {
    		try {
    			device = MidiSystem.getMidiDevice(infos[i]);
    			// does the device have any transmitters?
    			// if it does, add it to the device list
    			System.out.println(infos[i]);
    			    			
    			//get all transmitters
    			List<Transmitter> transmitters = device.getTransmitters();
    			// and for each transmitter create a new receiver using our own MidiInputReceiver
    			for(int j = 0; j < transmitters.size(); j++ ) {
    				Receiver receiver = new MidiInputReceiver(device.getDeviceInfo().toString());
    				transmitters.get(j).setReceiver(receiver);
    			}
    			
    			// open each device
    			device.open();
    			
    			// store transmitter for each device
    			allTransmitters.add(device.getTransmitter());
    			
    			// if code gets this far without throwing an exception print a success message
    			System.out.println(device.getDeviceInfo()+" was opened");

    		} catch (MidiUnavailableException e) {}
    	}
    }
    
    public void sendMidiOut(boolean isNoteOn, int channel, int note, int velocity) {
    	try {
    		ShortMessage shortMessage = new ShortMessage();
    		int noteOnOff = (isNoteOn == true) ? ShortMessage.NOTE_ON : ShortMessage.NOTE_OFF;
			shortMessage.setMessage(noteOnOff, channel, note, velocity);
			
			// default midi system out
			try {
				Receiver rcvr = MidiSystem.getReceiver();
				rcvr.send(shortMessage, -1);
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
			
			// loop through all of our devices and send message out
//			for (int i = 0; i < allTransmitters.size(); i++) {
//				Transmitter transmitter = allTransmitters.get(i);
//				Receiver receiver = transmitter.getReceiver();
//				if(receiver != null) {
//					P.println("sending note");
//					receiver.send(shortMessage, -1);
//				}
//			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
    }

	public class MidiInputReceiver implements Receiver {
		public String name;
		public MidiInputReceiver(String name) {
			this.name = name;
		}

		public void send(MidiMessage message, long timeStamp) {
			P.println("midi received, "+message);
			if (message instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) message;
//				P.println("ShortMessage.getCommand() = "+sm.getCommand());
				if (sm.getCommand() == ShortMessage.NOTE_ON) {
					int key = sm.getData1();
					//P.println("key:",key);
					int velocity = sm.getData2();
					P.p.noteOn(1, key, velocity);
//					int note = key % 12;
//					String noteName = NOTE_NAMES[note];
//					int octave = (key / 12)-1;
//					P.println("Channel: " + sm.getChannel());
				} else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
					int key = sm.getData1();
					int velocity = sm.getData2();
					P.p.noteOff(1, key, velocity);
//					int note = key % 12;
//					String noteName = NOTE_NAMES[note];
//					int octave = (key / 12)-1;
//					P.println("Channel: " + sm.getChannel());
				} else if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
					int key = sm.getData1();
					int ccValue = sm.getData2();
					P.p.controllerChange(sm.getChannel(), key, ccValue);
//					P.println("CONTROL_CHANGE Channel: " + sm.getChannel() + " " + sm.getData1() + " " + sm.getData2());
				} 
			}
		}

		public void close() {}
	}
}
