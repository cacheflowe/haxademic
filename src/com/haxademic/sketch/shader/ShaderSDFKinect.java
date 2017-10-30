package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.kinect.KinectRegionGrid;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class ShaderSDFKinect
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PGraphics _bg;

	protected PShader sdfShader;
	
	protected float _timeEaseInc = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 5);
	protected EasingFloat _userX = new EasingFloat(0, 12);
	protected EasingFloat _userZ = new EasingFloat(0, 12);
	protected float _autoTime = 0;
	
	protected KinectRegionGrid _kinectGrid;

	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "false" );
		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "600" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );
		
		_bg = p.createGraphics(p.width, p.height, P.P2D);
		_bg.smooth( OpenGLUtil.SMOOTH_HIGH );
				
		_kinectGrid = new KinectRegionGrid(1, 1, 500, 2000, 0, 0, 480, 20, 10, false);

		sdfShader = loadShader( FileUtil.getFile("shaders/textures/sdf-01.glsl") ); 
	}

	public void drawApp() {
		background(0);
		
		DrawUtil.setColorForPImage( p );
		DrawUtil.resetPImageAlpha( p );
		DrawUtil.setPImageAlpha(p, 1f);		
		
		_kinectGrid.update();
		updateTime();
		generateTexture();
	}
	
	protected void updateTime() {
		_timeEaser.update();
		_timeEaseInc = _timeEaser.value();
		_autoTime = millis() / 1000.0f;
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') _timeEaser.setTarget(_timeEaser.value() + 10);
	}
	
	protected void generateTexture() {
		_bg.resetShader();

//		sdfShader.set("time", _timeEaseInc);
//		float userX = 50f * (-0.5f + (float)p.mouseX / (float)p.width);
		float userX = 50f * -_kinectGrid.getRegion(0).controlX();
		_userX.setTarget(userX);
		_userX.update();
//		float userY = 300f * (-0.5f + (float)p.mouseY / (float)p.height);
		float userY = 300f * -_kinectGrid.getRegion(0).controlZ();
		_userZ.setTarget(userY);
		_userZ.update();
		
		sdfShader.set("userX", _userX.value());
		sdfShader.set("userZ", _userZ.value());
		sdfShader.set("time", (float) p.frameCount/3f);
		_bg.filter(sdfShader);
		p.image( _bg,  0,  0 );
	}

}
