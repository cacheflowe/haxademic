package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
import com.haxademic.core.hardware.midi.devices.NovationColors;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL.ILaunchControlXLCallback;
import com.haxademic.core.media.audio.analysis.AudioIn;

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
		launchControl1 = new LaunchControlXL(LaunchControlXL.deviceName2, LaunchControlXL.deviceName2);
		launchControl1.setDelegate(this);
		launchControl2 = new LaunchControlXL(LaunchControlXL.deviceName3, LaunchControlXL.deviceName3);
		launchControl2.setDelegate(this);
	}
	
	protected void drawApp() {
		p.background(0);
		
		// launchControl1.setButtonRow1(p.frameCount % 8, p.frameCount % 100 / 100f);
		// launchControl1.setButtonRow2(p.frameCount % 8, p.frameCount % 50 / 50f);
		launchControl2.setButtonRow1(p.frameCount % 8, p.frameCount % 100 / 100f);
		launchControl2.setButtonRow2(p.frameCount % 8, p.frameCount % 50 / 50f);
		// launchControl1.sendNoteOn(LaunchControlXL.KNOBS_ROW_1[p.frameCount % 8], p.frameCount % 127);
		// launchControl2.sendCC(p.frameCount % 110, p.frameCount % 127);
		launchControl2.sendCC(LaunchControlXL.KNOBS_ROW_1[5], p.frameCount % 127);
		launchControl2.sendNoteOn(LaunchControlXL.BUTTON_SIDE_1, p.frameCount % 127);
		launchControl2.sendCC(LaunchControlXL.BUTTON_UP, 0);
	}
	
	public void noteOnLaunchControl(LaunchControlXL launchControl, int note, float value) {
		int device = launchControl == launchControl1 ? '1' : '2';
		P.out("[LaunchControlXL] noteOn:", device, note, value);

		if(launchControl.isKnob(note)) {
			int noteVal = P.round(value) % launchControl.numColors(); //();
			P.out(note, noteVal);
			launchControl.sendCC(note, NovationColors.colorByPercent(noteVal / (float) launchControl.numColors()));
		}
	}

}