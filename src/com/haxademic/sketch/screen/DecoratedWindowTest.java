package com.haxademic.sketch.screen;

import java.awt.Frame;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;

public class DecoratedWindowTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	Frame _fullScreenFrame;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "60" );
	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		_fullScreenFrame = new Frame();
		_fullScreenFrame.setUndecorated(true);//prepare an undecorated fullscreen frame since java won't allow you to 'undecorate' a frame after it's been set visible 
		_fullScreenFrame.setBounds(0,0,displayWidth,displayHeight);
		// _fullScreenFrame.addKeyListener(getKeyListeners()[0]);//pass key events from this applet to the fullScreen Frame
	}

	public void drawApp() {
		background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
	}

	public void keyReleased(){
		if(key == 'f') {
//			setBounds(0,0,displayWidth,displayHeight);//resize the skech
//			fullScreenFrame.   ).add(this);
			_fullScreenFrame.add(p.frame.getComponent(0));//add the applet to the fullscreen frame from Processing's frame
			_fullScreenFrame.setVisible(true);//make our fullscreen frame visible
			p.frame.setVisible(false );//and hide Processing's frame
		}
	} 

}
