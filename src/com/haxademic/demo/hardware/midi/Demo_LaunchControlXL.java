package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL.ILaunchControlXLCallback;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.render.FrameLoop;

public class Demo_LaunchControlXL
extends PAppletHax
implements ILaunchControlXLCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected LaunchControlXL launchControl1;
	protected LaunchControlXL launchControl2;

	protected void firstFrame() {
		AudioIn.instance();
		MidiDevice.printDevices();
		MidiState.instance();
		
//		launchControl1 = new LaunchControlXL(LaunchControlXL.deviceName, LaunchControlXL.deviceName);
		launchControl1 = new LaunchControlXL(1, 4);
		launchControl1.setDelegate(this);
//		launchControl2 = new LaunchControlXL(LaunchControlXL.deviceNameIn2, LaunchControlXL.deviceNameOut2);
		launchControl2 = new LaunchControlXL(3, 6);
		launchControl2.setDelegate(this);
	}
	
	protected void drawApp() {
		p.background(0);
		
		int curFrame = (int) FrameLoop.count(0.4f);
		int curFrame2 = (int) FrameLoop.count(0.8f);
		float frames = 8 * 16;

		launchControl1.setButtonRow1(curFrame % 8, (curFrame % frames) / frames);
		launchControl1.setButtonRow2(curFrame % 8, (curFrame % frames) / frames);
		launchControl2.setButtonRow1(curFrame % 8, (curFrame % frames) / frames);
		launchControl2.setButtonRow2(curFrame % 8, (curFrame % frames) / frames);
		launchControl1.setKnobLED(0, curFrame2 % 8, (curFrame2 % frames) / frames);
		launchControl1.setKnobLED(1, curFrame2 % 8, (curFrame2 % frames) / frames);
		launchControl1.setKnobLED(2, curFrame2 % 8, (curFrame2 % frames) / frames);
		launchControl2.setKnobLED(0, curFrame2 % 8, (curFrame2 % frames) / frames);
		launchControl2.setKnobLED(1, curFrame2 % 8, (curFrame2 % frames) / frames);
		launchControl2.setKnobLED(2, curFrame2 % 8, (curFrame2 % frames) / frames);


		// launchControl2.sendNoteOn(LaunchControlXL.BUTTON_SIDE_1, curFrame % 127);
		// launchControl2.sendCC(LaunchControlXL.BUTTON_UP, 0);
	}
	
	public void noteOnLaunchControl(LaunchControlXL launchControl, int note, float value) {
		int device = launchControl == launchControl1 ? '1' : '2';
		P.out("[LaunchControlXL] noteOn:", device, note, value);

		if(launchControl.isKnob(note)) {
			int noteVal = P.round(value) % launchControl.numColors();
		}
	}
	
	public void ccLaunchControl(LaunchControlXL launchControl, int note, float value) {
		int device = launchControl == launchControl1 ? '1' : '2';
		P.out("[LaunchControlXL] cc:", device, note, value);

		if(launchControl.isKnob(note)) {
			int noteVal = P.round(value) % launchControl.numColors();
		}
	}

}