package com.haxademic.core.hardware.midi.devices;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ArrayUtil;
import com.haxademic.core.hardware.midi.MidiDevice;

import themidibus.MidiBus;
import themidibus.SimpleMidiListener;

public class LaunchControlXL
implements SimpleMidiListener {

	// Info:
	// - https://components.novationmusic.com/ - Device editor
	// - https://support.novationmusic.com/hc/en-gb/articles/4411807214226-Launch-Control-XL-Components-guide
	// - https://fael-downloads-prod.focusrite.com/customer/prod/s3fs-public/downloads/launch-control-xl-programmers-reference-guide.pdf
	// - https://scsynth.org/t/sending-sysex-messages-to-a-midi-controller/549/9

	// Static device props

	public static String deviceName = "Launch Control XL";
	public static String deviceName2 = "Launch Control XL";
	public static String deviceNameIn = "MIDIIN2 (Launch Control XL)";
	public static String deviceNameIn2 = "MIDIIN4 (Launch Control XL)";
	public static String deviceNameOut = "MIDIOUT2 (Launch Control XL)";
	public static String deviceNameOut2 = "MIDIOUT4 (Launch Control XL)";

	public static int[] KNOBS_ROW_1   = new int[] {13, 14, 15, 16, 17, 18, 19, 20};	// CC
	public static int[] KNOBS_ROW_2   = new int[] {29, 30, 31, 32, 33, 34, 35, 36};	// CC
	public static int[] KNOBS_ROW_3   = new int[] {49, 50, 51, 52, 53, 54, 55, 56};	// CC
	public static int[] SLIDERS       = new int[] {77, 78, 79, 80, 81, 82, 83, 84};	// CC
	public static int[] BUTTONS_1     = new int[] {41, 42, 43, 44, 57, 58, 59, 60};	// NOTE
	public static int[] BUTTONS_2     = new int[] {73, 74, 75, 76, 89, 90, 91, 92};	// NOTE
	public static int BUTTON_UP       = 104;	// CC
	public static int BUTTON_DOWN     = 105;	// CC
	public static int BUTTON_LEFT     = 106;	// CC
	public static int BUTTON_RIGHT    = 107;	// CC
	public static int BUTTON_SIDE_1   = 105;	// NOTE
	public static int BUTTON_SIDE_2   = 106;	// NOTE
	public static int BUTTON_SIDE_3   = 107;	// NOTE
	public static int BUTTON_SIDE_4   = 108;	// NOTE

	public static int CHANNEL         = 12; // hold Factory and press 5 top set this, which solves some funny midi issues, and standardizes across devices
	
	//////////////////////////////
	// STATIC HELPERS
	//////////////////////////////
	
	public int colorByPercent(float percent) {
		return NovationColors.colorByPercent(percent);
	}
	
	public int numColors() {
		return NovationColors.colors.length;
	}
	
	public int numColorsSysex() {
		return NovationColors.colorsSysex.length;
	}
	
	//////////////////////////////
	// CALLBACK INTERFACE
	//////////////////////////////
	
	public interface ILaunchControlXLCallback {
		public void noteOnLaunchControl(LaunchControlXL launchControl, int note, float value);
	}
	
	//////////////////////////////
	// INIT
	//////////////////////////////
	
	protected MidiBus midiBus;
	protected ILaunchControlXLCallback delegate;

	public LaunchControlXL(String launchControlDeviceNameIn, String launchControlDeviceNameOut) {
		MidiDevice device = new MidiDevice(launchControlDeviceNameIn, launchControlDeviceNameOut, this);
		// midiBus = new MidiBus(this, launchControlDeviceNameIn, launchControlDeviceNameOut);  // switched to MidiDevice, which also forwards incoming MIDI to MidiState
		midiBus = device.midiBus;
		init();
	}
	
	public LaunchControlXL(int midiIndexIn, int midiIndexOut) {
		MidiDevice device = new MidiDevice(midiIndexIn, midiIndexOut, this);
		// midiBus = new MidiBus(this, midiIndexIn, midiIndexOut); // switched to MidiDevice, which also forwards incoming MIDI to MidiState
		midiBus = device.midiBus;
		init();
	}
	
	protected void init() {
		initAll();
	}
	
	public void setDelegate(ILaunchControlXLCallback delegate ) {
		this.delegate = delegate;
	}
	
	//////////////////////////
	// HANDLE BUTTONS / KNOBS
	//////////////////////////

	protected void initAll() {
		for (int i = 0; i < BUTTONS_1.length; i++) setButtonRow1(i, 0);
		for (int i = 0; i < BUTTONS_2.length; i++) setButtonRow2(i, 0);
	}
	
	public void setButtonRow1(int i, float val) {
		val = P.floor(val * (float) numColors()) / (float) numColors();
		sendNoteOn(BUTTONS_1[i], colorByPercent(val));
	}
	
	public void setButtonRow2(int i, float val) {
		val = P.floor(val * (float) numColors()) / (float) numColors();
		sendNoteOn(BUTTONS_2[i], colorByPercent(val));
	}

	public void setKnobLED(int row, int btn, float val) {
		val = P.floor(val * (float) numColorsSysex()) / (float) numColorsSysex();
		// byte valByte = (byte) (val * 60); // doesn't go to 127! it goes to 60
		byte valByte = NovationColors.colorSysexByPercent(val);
		byte template5 = 12;
		byte btnIndex = (byte) (row * 8 + btn);
		sendSysEx(new byte[] { (byte) 240, 0, 32, 41, 2, 17, 120, template5, btnIndex, valByte, (byte) 247 });
	}

	public boolean isKnob(int note) {
		if (ArrayUtil.indexOfInt(KNOBS_ROW_1, note) != -1) return true; 
		if (ArrayUtil.indexOfInt(KNOBS_ROW_2, note) != -1) return true; 
		if (ArrayUtil.indexOfInt(KNOBS_ROW_3, note) != -1) return true; 
		return false;
	}

	//////////////////////////
	// MIDIBUS OUTPUT
	//////////////////////////

	public void sendNoteOn(int pitch, int velocity) {
		midiBus.sendNoteOn(CHANNEL, pitch, velocity);
	}
	
	public void sendCC(int pitch, int velocity) {
		midiBus.sendControllerChange(CHANNEL, pitch, velocity);
	}
	
	public void sendSysEx(byte[] data) {
		midiBus.sendMessage(data);
	}
	
	//////////////////////////
	// MIDIBUS LISTENERS
	//////////////////////////

	public void controllerChange(int channel, int pitch, int velocity) {
		if(delegate != null) {
			// TODO: change to ccLaunchControl
			delegate.noteOnLaunchControl(this, pitch, velocity);
		}
	}

	public void noteOff(int channel, int pitch, int velocity) {
		
	}

	public void noteOn(int channel, int pitch, int velocity) {
		if(delegate != null) {
			delegate.noteOnLaunchControl(this, pitch, velocity);
		}
	}

}