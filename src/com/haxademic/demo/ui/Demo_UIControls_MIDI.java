package com.haxademic.demo.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.ui.UIButton;

public class Demo_UIControls_MIDI 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String R = "R";
	protected String G = "G";
	protected String B = "B";
	
	protected String VECTOR_3 = "VECTOR_3";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_UI, true);
	}
	
	public void setupFirstFrame () {
		// init midi input
		MidiDevice.init(0, 3);

		// build UI controls
		p.ui.addSlider(R, 255, 0, 255, 0.5f, false, LaunchControl.KNOB_01);
		p.ui.addSlider(G, 0, 0, 255, 0.5f, false, LaunchControl.KNOB_02);
		p.ui.addSlider(B, 0, 0, 255, 0.5f, false, LaunchControl.KNOB_03);
		p.ui.addSliderVector(VECTOR_3, 0, -1f, 1f, 0.001f, false, LaunchControl.KNOB_09, LaunchControl.KNOB_10, LaunchControl.KNOB_11);
		p.ui.addButton("Button", false, LaunchControl.PAD_01);
		p.ui.addButton("Button 2", true, LaunchControl.PAD_02);
		p.ui.addButtons(new String[] {"1", "2", "3", "4"}, true, new int[] {LaunchControl.PAD_03, LaunchControl.PAD_04, LaunchControl.PAD_05, LaunchControl.PAD_06});
	}
	
	public void drawApp() {
//		p.debugView.active(true);

		// bg components
		p.background(p.ui.value(R), p.ui.value(G), p.ui.value(B));
		
		// 3d rotation
		p.lights();
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		p.rotateX(p.ui.valueXEased(VECTOR_3));
		p.rotateY(p.ui.valueYEased(VECTOR_3));
		p.rotateZ(p.ui.valueZEased(VECTOR_3));
		p.fill(255);
		p.stroke(0);
		if(p.ui.value("Button 2") == 0) {
			p.box(200);
		} else {
			p.box(200, 100, 200);
		}
	}
	
	public void uiButtonClicked(UIButton button) {
		P.out(button.id(), button.value());
	}
}
