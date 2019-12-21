package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;

public class Demo_DmxFixture_FourLightsSelectOne
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXFixture[] fixtures;
	protected int selectedIndex = -1;
	
	public void firstFrame() {
		// use most basic singleton instance version of DMXUniverse
		DMXUniverse.instanceInit("COM3", 9600);
		
		// create lights
		float easeFactor = 0.04f;
		fixtures = new DMXFixture[] {
			(new DMXFixture(1)).setEaseFactor(easeFactor),	
			(new DMXFixture(4)).setEaseFactor(easeFactor),	
			(new DMXFixture(7)).setEaseFactor(easeFactor),	
			(new DMXFixture(10)).setEaseFactor(easeFactor),	
		};
	}

	public void drawApp() {
		p.background(0);
		p.noStroke();
		float rectW = (float) p.width / fixtures.length;
		for (int i = 0; i < fixtures.length; i++) {
			p.fill(fixtures[i].color().colorInt());
			p.rect(i * rectW, 0, rectW, p.height);
		}
	}
	
	protected void setLightActive(int index) {
		if(index >= 0 && index <= 3) {
			// dim all lights
			for (int i = 0; i < fixtures.length; i++) fixtures[i].color().setTargetInt(0xff111111);
			// but keep selected light active
			fixtures[index].color().setTargetInt(0xffffffff);
		} else {
			// set all lights bright
			for (int i = 0; i < fixtures.length; i++) fixtures[i].color().setTargetInt(0xffffffff);
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		
		// select light!
		if(p.key == '1') setLightActive(0);
		else if(p.key == '2') setLightActive(1);
		else if(p.key == '3') setLightActive(2);
		else if(p.key == '4') setLightActive(3);
		else setLightActive(-1);
	}
}
