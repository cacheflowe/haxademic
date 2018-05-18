package com.haxademic.app.musicvideos;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class CachePatterCloudControlTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PGraphics _cloudsGraphics;
	PShader _clouds;
	
	protected float _timeConstantInc = 0.5f;
	protected EasingFloat _cloudTimeEaser = new EasingFloat(0, 13);

	float _songLengthFrames = 120f; // really 4519, but we're letting Renderer shut this down at the end of the audio file

	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "720" );
		p.appConfig.setProperty( AppSettings.WIDTH, "480" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "270" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}
	
	public void setup() {
		super.setup();
		
		_cloudsGraphics = p.createGraphics(p.width, p.height, P.P3D);
		_cloudsGraphics.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_clouds = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/clouds-iq.glsl" ); 
	}
	
	public void drawApp() {
		p.background(0);
		
		drawClouds();
		
		// move clouds control in a big half-circle
		float percentComplete = (float) p.frameCount / _songLengthFrames;
		float cloudControlRadians = percentComplete * P.PI;
		float cloudControlX = (float)p.width/2f + 		   P.sin(cloudControlRadians - P.HALF_PI) * (float)p.width/2f;
		float cloudControlY = ((float)-p.height * 0.01f) + P.cos(cloudControlRadians - P.HALF_PI) * (float)p.width/2f * 1.f;

		// show cloud controls
		p.fill(0);
		p.ellipse(cloudControlX, cloudControlY, 10, 10);

		// show 3d rotation
		p.stroke(0);
		p.noFill();
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		p.rotateY(cloudControlRadians);
		p.rotateX(P.sin(cloudControlRadians * -2f)/2);
		p.box(10,10,50);
		p.popMatrix();
		
		P.println(p.frameCount+" / "+_songLengthFrames);
	}
	
	protected void drawClouds() {
		// move clouds control in a big half-circle
		float percentComplete = (float) p.frameCount / _songLengthFrames;
		float cloudControlRadians = percentComplete * P.PI;
		float cloudControlX = (float)p.width/4f + 		   P.sin(cloudControlRadians - P.HALF_PI) * (float)p.width/4f;
		float cloudControlY = ((float)-p.height * 0.3f) + P.cos(cloudControlRadians - P.HALF_PI) * (float)p.width/2f * 1.f;

		_cloudTimeEaser.update();
		_clouds = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/clouds-iq.glsl" ); 
		_clouds.set("resolution", 1f, (float)(p.width/p.height));
		_clouds.set("time", p.frameCount * _timeConstantInc + _cloudTimeEaser.value() );
//		_clouds.set("mouse", 0.5f + p.frameCount/4000f, 0.9f - p.frameCount/4000f);		
//		_clouds.set("mouse", (float)mouseX/p.width, (float)mouseY/p.height);		
		_clouds.set("mouse", cloudControlX/p.width, cloudControlY/p.height);		

		_cloudsGraphics.beginDraw();
		_cloudsGraphics.filter(_clouds);		
		_cloudsGraphics.endDraw();
		p.image( _cloudsGraphics, 0, 0);
	}

}