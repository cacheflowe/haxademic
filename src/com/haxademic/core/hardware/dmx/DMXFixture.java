 package com.haxademic.core.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingColor;

public class DMXFixture {

	protected int dmxChannel;
	protected boolean isSingleChannel = false;
	protected EasingColor color = new EasingColor("#ffffff", 0.3f); 
	
	public DMXFixture(int dmxStartChannel) {
		this(dmxStartChannel, false);
	}
		
	public DMXFixture(int dmxStartChannel, boolean isSingleChannel) {
		this.dmxChannel = dmxStartChannel;
		this.isSingleChannel = isSingleChannel;
		if(P.p.dmxFixtures != null) P.p.dmxFixtures.addFixture(this);
		else P.error("DMXFixtures not initialized");
	}
	
	public int dmxChannel() { return dmxChannel; }
	public void dmxChannel(int channel) { dmxChannel = channel; }
	public EasingColor color() { return color; }
	public DMXFixture setEaseFactor(float easeFactor) { color.setEaseFactor(easeFactor); return this; }
	public int colorR() { return P.round(color.r()); }
	public int colorG() { return P.round(color.g()); }
	public int colorB() { return P.round(color.b()); }
	public int colorA() { return P.round(color.a()); }
	public int colorLuma() { return (int) P.p.brightness(color.colorInt()); }
	
	public void update() {
		color.update();
		
		if(P.p.dmxFixtures == null) return;
		
		P.p.dmxFixtures.setValue(dmxChannel + 0, colorR());
		if(isSingleChannel == false) {
			P.p.dmxFixtures.setValue(dmxChannel + 1, colorG());
			P.p.dmxFixtures.setValue(dmxChannel + 2, colorB());
		}
	}
}
