package com.haxademic.core.hardware.dmx;

import java.util.ArrayList;

public class DMXFixtures
extends DMXWrapper {

	protected ArrayList<DMXFixture> fixtures = new ArrayList<DMXFixture>();

	public DMXFixtures(String port, int baudRate) {
		super(port, baudRate);
	}
	
	public void addFixture(DMXFixture fixture) {
		fixtures.add(fixture);
	}
		
	public void update() {
		for (int i = 0; i < fixtures.size(); i++) {
			fixtures.get(i).update();
		}
	}	

}
