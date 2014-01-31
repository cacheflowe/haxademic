package com.haxademic.core.hardware.midi;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Hashtable;

import processing.core.PApplet;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.system.SystemUtil;

public class MidiWrapper
{
	PApplet p;
//	MidiBus myBus;
	
	MidiHandler _midiHandler;
	
	public int[] _notesOn;
	Hashtable<String, Integer> padMap;
	
	public int sliderValue = 0;
	
	public static String PAD_01 = "PAD_01";
	public static String PAD_02 = "PAD_02";
	public static String PAD_03 = "PAD_03";
	public static String PAD_04 = "PAD_04";
	public static String PAD_05 = "PAD_05";
	public static String PAD_06 = "PAD_06";
	public static String PAD_07 = "PAD_07";
	public static String PAD_08 = "PAD_08";
	public static String PAD_09 = "PAD_09";
	public static String PAD_10 = "PAD_10";
	public static String PAD_11 = "PAD_11";
	public static String PAD_12 = "PAD_12";
	public static String PAD_13 = "PAD_13";
	public static String PAD_14 = "PAD_14";
	public static String PAD_15 = "PAD_15";
	public static String PAD_16 = "PAD_16";
	
	public static String NOTE_01 = "NOTE_01";
	public static String NOTE_02 = "NOTE_02";
	public static String NOTE_03 = "NOTE_03";
	public static String NOTE_04 = "NOTE_04";
	public static String NOTE_05 = "NOTE_05";
	public static String NOTE_06 = "NOTE_06";
	public static String NOTE_07 = "NOTE_07";
	public static String NOTE_08 = "NOTE_08";
	public static String NOTE_09 = "NOTE_09";
	public static String NOTE_10 = "NOTE_10";
	public static String NOTE_11 = "NOTE_11";
	public static String NOTE_12 = "NOTE_12";
	public static String NOTE_13 = "NOTE_13";
	public static String NOTE_14 = "NOTE_14";
	public static String NOTE_15 = "NOTE_15";
	public static String NOTE_16 = "NOTE_16";
	
	public static String PROGRAM_01 = "PROGRAM_01";
	public static String PROGRAM_02 = "PROGRAM_02";
	public static String PROGRAM_03 = "PROGRAM_03";
	public static String PROGRAM_04 = "PROGRAM_04";
	public static String PROGRAM_05 = "PROGRAM_05";
	public static String PROGRAM_06 = "PROGRAM_06";
	public static String PROGRAM_07 = "PROGRAM_07";
	public static String PROGRAM_08 = "PROGRAM_08";
	public static String PROGRAM_09 = "PROGRAM_09";
	public static String PROGRAM_10 = "PROGRAM_10";
	public static String PROGRAM_11 = "PROGRAM_11";
	public static String PROGRAM_12 = "PROGRAM_12";
	public static String PROGRAM_13 = "PROGRAM_13";
	public static String PROGRAM_14 = "PROGRAM_14";
	public static String PROGRAM_15 = "PROGRAM_15";
	public static String PROGRAM_16 = "PROGRAM_16";

	public MidiWrapper( PApplet p5, String in_device_name, String out_device_name )
	{
		p = p5;
		
//		_midiHandler = new MidiHandler();
		Thread t = new Thread(new MidiHandler());
		t.setPriority(Thread.MIN_PRIORITY);
		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                DebugUtil.printErr("UncaughtExceptionHandler on MidiHandler Thread");
            }
        });
		t.start();
		
//		MidiBus.list();
		if(in_device_name != "") {
			P.println("MIDI connection:");
			if( in_device_name != "" || out_device_name != "" ) {
//				myBus = new MidiBus( p, in_device_name, out_device_name);
			} else {
//				myBus = new MidiBus( p );
			}
		} else {
			P.println("No MIDI connected");
		}
		
		// init notes on array
		_notesOn = new int[128];
		for( int i = 0; i < 128; i++ ) _notesOn[i] = 0;
		
		initPads();
	}
	
	private void initPads()
	{
		padMap = new Hashtable<String, Integer>();
		// Special mapping for AKAI MPD16 - move this out and build an adapter system for different instruments
		padMap.put(PAD_01, 72);
		padMap.put(PAD_02, 73);
		padMap.put(PAD_03, 74);
		padMap.put(PAD_04, 75);
		padMap.put(PAD_05, 68);
		padMap.put(PAD_06, 69);
		padMap.put(PAD_07, 70);
		padMap.put(PAD_08, 71);
		padMap.put(PAD_09, 64);
		padMap.put(PAD_10, 65);
		padMap.put(PAD_11, 66);
		padMap.put(PAD_12, 67);
		padMap.put(PAD_13, 60);
		padMap.put(PAD_14, 61);
		padMap.put(PAD_15, 62);
		padMap.put(PAD_16, 63);

		// C2
		padMap.put(NOTE_01, 48); 
		padMap.put(NOTE_02, 49); 
		padMap.put(NOTE_03, 50); 
		padMap.put(NOTE_04, 51); 
		padMap.put(NOTE_05, 52); 
		padMap.put(NOTE_06, 53); 
		padMap.put(NOTE_07, 54); 
		padMap.put(NOTE_08, 55); 
		padMap.put(NOTE_09, 56); 
		padMap.put(NOTE_10, 57); 
		padMap.put(NOTE_11, 58); 
		padMap.put(NOTE_12, 59); 
		padMap.put(NOTE_13, 60); 
		padMap.put(NOTE_14, 61); 
		padMap.put(NOTE_15, 62); 
		padMap.put(NOTE_16, 63); 

		// C0
		padMap.put(PROGRAM_01, 24);
		padMap.put(PROGRAM_02, 25);
		padMap.put(PROGRAM_03, 26);
		padMap.put(PROGRAM_04, 27);
		padMap.put(PROGRAM_05, 28);
		padMap.put(PROGRAM_06, 29);
		padMap.put(PROGRAM_07, 30);
		padMap.put(PROGRAM_08, 31);
		padMap.put(PROGRAM_09, 32);
		padMap.put(PROGRAM_10, 33);
		padMap.put(PROGRAM_11, 34);
		padMap.put(PROGRAM_12, 35);
		padMap.put(PROGRAM_13, 36);
		padMap.put(PROGRAM_14, 37);
		padMap.put(PROGRAM_15, 38);
		padMap.put(PROGRAM_16, 39);
	}
	
	public int getMidiPad( String padId )
	{
//		p.println(  padId  );
		if( padMap.containsKey( padId ) )
		{
			return (int)padMap.get( padId );
		}
		return -1;
	}
	
	public int midiPadIsOn( String padId )
	{
		int isOn = midiNoteIsOn( getMidiPad( padId ) );
		if( isOn == -1 )
			return 0;
		else
			return isOn;
	}
	
	public int midiNoteIsOn( int pitch )
	{
		if( _notesOn[ pitch ] == 1 ) {
			_notesOn[ pitch ] = 0;
			return 1;
		} else { 
			return 0;
		}
	}
	
	public void noteOn(int channel, int pitch, int velocity) {
		// Receive a noteOn
//		P.println("ON: pitch = "+pitch);
		_notesOn[ pitch ] = 1;
	}
	public void noteOff(int channel, int pitch, int velocity) {
		// Receive a noteOff
//		P.println("OFF: pitch = "+pitch);
		_notesOn[ pitch ] = 0;
	}
	public void allOff() {
		for( int i = 0; i < 128; i++ ) _notesOn[i] = 0;
	}
	public void controllerChange(int channel, int number, int value) {
		// Receive a controllerChange
//		p.println("Note CC:  Channel:"+channel+" | Number:"+number+" | Value:"+value);
		sliderValue = value;
	}
}
