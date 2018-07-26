package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class PanBigImage
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics _pg1;
	protected PGraphics _pg2;
	protected PImage bigImage;
	protected PImage bigImage2;
	protected PShader transitionShader;
	protected float frames = 200;

	protected int midiInChannel = 0;
	protected float controlX = 0.5f;
	protected float controlY = 0.5f;
	protected EasingFloat easedX = new EasingFloat(controlX, 4f);
	protected EasingFloat easedY = new EasingFloat(controlY, 4f);
	


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, true );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
		p.appConfig.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
		p.appConfig.setProperty( AppSettings.MIDI_DEBUG, true );
	}

	public void setup() {
		super.setup();
	}

	protected void buildCanvas() {
		_pg1 = p.createGraphics( p.width, p.height, P.P3D );
		_pg1.smooth(AppSettings.SMOOTH_NONE);
		_pg2 = p.createGraphics( p.width, p.height, P.P3D );
		_pg2.smooth(AppSettings.SMOOTH_NONE);
	}
	
	protected void initApp() {
		bigImage = p.requestImage(FileUtil.getFile("images/sun.jpg"));
		bigImage2 = p.requestImage(FileUtil.getFile("images/sun-nasa.jpg"));
		buildCanvas();
		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/warp-fade.glsl"));
	}

	public void drawApp() {
		if(p.frameCount == 1) initApp();
		p.background(0);
		
		updateInput();
		panImageToBuffers();
		setImageTransition();
	}
	
	protected void updateInput() {
		panWithMouse();
//		panWithMidi();
		easedX.setTarget(controlX);
		easedX.update();
		easedY.setTarget(controlY);
		easedY.update();
	}
	
	protected void panWithMouse() {
		controlX = P.map(p.mouseX, 0, p.width, 0, 1f);
		controlY = P.map(p.mouseY, 0, p.height, 0, 1f);
	}

	protected void panWithMidi() {
		controlX = p.midiState.midiCCPercent(21);
		controlY = p.midiState.midiCCPercent(22);
	}
	
	protected void panImageToBuffers() {
		float imageScrollW = bigImage.width - p.width; 
		float imageScrollH = bigImage.height - p.height;
		float imgX = P.map(easedX.value(), 0, 1f, 0, -imageScrollW);
		float imgY = P.map(easedY.value(), 0, 1f, 0, -imageScrollH);
		_pg1.beginDraw();
		_pg1.background(0);
		_pg1.image(bigImage, imgX, imgY);
		_pg1.endDraw();
		_pg2.beginDraw();
		_pg2.background(0);
		_pg2.image(bigImage2, imgX, imgY);
		_pg2.endDraw();
	}
	
	protected void setImageTransition() {
		float progress = (p.frameCount % frames*2) / frames; 
		if(progress < 1.0f) {
			transitionShader.set("from", _pg1);
			transitionShader.set("to", _pg2);
		} else {
			transitionShader.set("from", _pg2);
			transitionShader.set("to", _pg1);
		}
		float loopProgress = progress % 1f;
		float easedProgress = Penner.easeInOutCubic(loopProgress, 0, 1, 1);

		transitionShader.set("progress", easedProgress);
		p.filter(transitionShader);
	}

}
