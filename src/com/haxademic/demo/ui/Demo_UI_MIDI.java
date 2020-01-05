package com.haxademic.demo.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.ui.UI;
import com.haxademic.core.ui.UIButton;

public class Demo_UI_MIDI 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String R = "R";
	protected String G = "G";
	protected String B = "B";
	
	protected String VECTOR_3 = "VECTOR_3";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame () {
		// init midi input
		MidiDevice.init(0, 3);

		// build UI controls
		UI.addSlider(R, 255, 0, 255, 0.5f, false, LaunchControl.KNOB_01);
		UI.addSlider(G, 0, 0, 255, 0.5f, false, LaunchControl.KNOB_02);
		UI.addSlider(B, 0, 0, 255, 0.5f, false, LaunchControl.KNOB_03);
		UI.addSliderVector(VECTOR_3, 0, -1f, 1f, 0.001f, false, LaunchControl.KNOB_09, LaunchControl.KNOB_10, LaunchControl.KNOB_11);
		UI.addButton("Button", false, LaunchControl.PAD_01);
		UI.addButton("Button 2", true, LaunchControl.PAD_02);
		UI.addButtons(new String[] {"1", "2", "3", "4"}, true, new int[] {LaunchControl.PAD_03, LaunchControl.PAD_04, LaunchControl.PAD_05, LaunchControl.PAD_06});
	}
	
	protected void drawApp() {
//		p.debugView.active(true);

		// bg components
		p.background(UI.value(R), UI.value(G), UI.value(B));
		
		// 3d rotation
		p.lights();
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		p.rotateX(UI.valueXEased(VECTOR_3));
		p.rotateY(UI.valueYEased(VECTOR_3));
		p.rotateZ(UI.valueZEased(VECTOR_3));
		p.fill(255);
		p.stroke(0);
		if(UI.value("Button 2") == 0) {
			p.box(200);
		} else {
			p.box(200, 100, 200);
		}
	}
	
	public void uiButtonClicked(UIButton button) {
		P.out(button.id(), button.value());
	}
}
