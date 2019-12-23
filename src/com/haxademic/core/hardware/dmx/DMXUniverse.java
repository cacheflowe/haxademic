package com.haxademic.core.hardware.dmx;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;

public class DMXUniverse
extends DMXWrapper {
	
	// DMXUniverse provides a collection of lights that auto-update.
	// This allows for a more comfortable communication via the parent DMXWrapper

	protected ArrayList<DMXFixture> fixtures = new ArrayList<DMXFixture>();

	/////////////////////////////
	// static instance & initializer for quick & easy access
	/////////////////////////////
	
	public static DMXUniverse instance;
	
	public static DMXUniverse instance() {
		if(instance != null) return instance;
		instance = new DMXUniverse();
		return instance;
	}
	
	public static DMXUniverse instanceInit(String port, int baudRate) {
		if(instance != null) return instance;
		instance = new DMXUniverse(port, baudRate);
		return instance;
	}
	
	/////////////////////////////
	// regular constructor
	/////////////////////////////

	public DMXUniverse() {
		super();
		P.p.registerMethod(PRegisterableMethods.post, this);
	}
	
	public DMXUniverse(String port, int baudRate) {
		super(port, baudRate);
		P.p.registerMethod(PRegisterableMethods.post, this);
	}
	
	/////////////////////////////
	// public methods & auto update
	/////////////////////////////
	
	public void addFixture(DMXFixture fixture) {
		fixtures.add(fixture);
	}
		
	public void post() {
		for (int i = 0; i < fixtures.size(); i++) {
			fixtures.get(i).update();
		}
	}	

}
