package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.camera.CameraUtil;
import com.haxademic.core.constants.AppSettings;

import controlP5.ControlP5;

public class CameraUtilTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public float cameraNear = 0;
	public float cameraDist = 0;
	public boolean orthoCamera = false;
	protected ControlP5 _cp5;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "600" );
	}

	public void setup() {
		super.setup();	

		_cp5 = new ControlP5(this);
		_cp5.addSlider("cameraNear").setPosition(20,60).setWidth(100).setRange(1, 1000).setValue(1);
		_cp5.addSlider("cameraDist").setPosition(20,80).setWidth(100).setRange(0, 30000).setValue(30000);
		_cp5.addToggle("orthoCamera").setPosition(20,100).setWidth(100).setValue(orthoCamera);
	}

	public void drawApp() {
		p.background(0);
		p.pushMatrix();
		  if(orthoCamera == true) {
			  p.ortho();
		  } else {			  
			  float fov = mouseX/(float)width * PI/2f;
			  float cameraY = (float)height/2.0f;
			  float cameraZ = cameraY / tan(fov / 2.0f);
			  float aspect = (float)width/(float)height;
			  if (mousePressed) {
				  aspect = aspect / 2.0f;
			  }
//			  perspective(fov, aspect, cameraZ/10.0, cameraZ*10.0);
			  p.perspective(fov, aspect, cameraNear, cameraDist);
		  }
		  
//		  CameraUtil.setCameraDistance(p.g, cameraNear, cameraDist);

		p.fill(255);
		
		
		p.pushMatrix();
		
		translate(p.width/2, p.height/2);
		p.fill(255, 0, 255);
		p.stroke(255);
		
		for(int z = 0; z > -20000; z -= 100) {
			p.translate(40, -40, -100);
			p.box(100 + -z/100);
		}
		p.popMatrix();

		p.popMatrix();
		CameraUtil.resetCamera(p.g);
	}
}
