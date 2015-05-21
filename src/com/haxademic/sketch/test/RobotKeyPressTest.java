package com.haxademic.sketch.test;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class RobotKeyPressTest
extends PAppletHax {
	
	protected Robot _robot;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
	}

	public void setup() {
		super.setup();	
		try { _robot = new Robot(); } catch( Exception error ) { println("couldn't init Robot"); }
	}

	public void drawApp() {
		background(0);

		if( p.frameCount % 2 == 0 ) {
			_robot.keyPress(KeyEvent.VK_A);
		}
		if( p.frameCount % 2 == 1 ) {
			_robot.keyRelease(KeyEvent.VK_A);
		}
	}
}
