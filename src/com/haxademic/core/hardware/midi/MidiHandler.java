package com.haxademic.core.hardware.midi;

import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import com.haxademic.core.app.P;

public class MidiHandler
implements Runnable {

	// from: http://stackoverflow.com/questions/6937760/java-getting-input-from-midi-keyboard

    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	
	public MidiHandler() {
	}
	
    public void run(){
    	MidiDevice device;
    	MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
    	
    	for (int i = 0; i < infos.length; i++) {
    		try {
    			device = MidiSystem.getMidiDevice(infos[i]);
    			//does the device have any transmitters?
    			//if it does, add it to the device list
    			System.out.println(infos[i]);
    			
    			//get all transmitters
    			List<Transmitter> transmitters = device.getTransmitters();
    			//and for each transmitter
    			
    			for(int j = 0; j < transmitters.size(); j++ ) {
    				//create a new receiver
    				transmitters.get(j).setReceiver(
    						//using my own MidiInputReceiver
    						new MidiInputReceiver(device.getDeviceInfo().toString())
    						);
    			}
    			
    			Transmitter trans = device.getTransmitter();
    			trans.setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString()));
    			
    			//open each device
    			device.open();
    			//if code gets this far without throwing an exception
    			//print a success message
    			System.out.println(device.getDeviceInfo()+" was opened");
    			
    		} catch (MidiUnavailableException e) {}
    	}
    }


	// tried to write my own class. I thought the send method handles an MidiEvents sent to it
	public class MidiInputReceiver implements Receiver {
		public String name;
		public MidiInputReceiver(String name) {
			this.name = name;
		}

		public void send(MidiMessage message, long timeStamp) {
//			System.out.println("midi received");
			if (message instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) message;
//				P.print("|| "+sm.getCommand()+" ||");
				if (sm.getCommand() == NOTE_ON) {
					int key = sm.getData1();
					int velocity = sm.getData2();
					P.p.noteOn(1, key, velocity);
//					int note = key % 12;
//					String noteName = NOTE_NAMES[note];
//					int octave = (key / 12)-1;
//					P.println("Channel: " + sm.getChannel());
				}
			}
		}

		public void close() {}
	}
}
