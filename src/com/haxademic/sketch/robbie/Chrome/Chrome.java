package com.haxademic.sketch.robbie.Chrome;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;

import processing.core.PGraphics;

public class Chrome 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int chromeColor;
	protected float chromeColorR;
	protected float chromeColorG;
	protected float chromeColorB;
	
	protected float chromeSpeed = 0.01f;
	
	protected float chromePos;
	
	
	
	/////////////////////////////////
	// INIT
	/////////////////////////////////
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 600 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
//		p.appConfig.setProperty( AppSettings.PG_WIDTH, 600 );
//		p.appConfig.setProperty( AppSettings.PG_HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
//		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
		p.appConfig.setProperty( AppSettings.APP_NAME, "Basic" );
		p.appConfig.setProperty( AppSettings.APP_ICON, "images/app-icon.png" );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, false);
//		p.appConfig.setProperty(AppSettings.RENDERING_IMAGE_SEQUENCE, false );
//		p.appConfig.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 1);
//		p.appConfig.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, 1800);
		int FRAMES = 300;
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 1);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	public void setupFirstFrame() {
		p.background(0);
		p.noStroke();
//		p.size(600, 600);
	}

	public void drawApp() {
		chromeSpeed = 0.01f;
//		float mouse = p.mouseX  / (float)p.width * P.TWO_PI;
//		chromeSpeed = mouse;
//		chromeSetter(p.frameCount * chromeSpeed);
		
		
//		p.background(chromeColor);
		p.background(0);
		
		p.pushStyle();
		
		int maxGradials = 27;
		float gap = 60;
		float radiusGrow = 20f;
		float radius = (int)P.sqrt(P.sq(p.width) + P.sq(p.height))+radiusGrow;
		float radiusFreq = 3f;
//		float radiusSpeed = 0.04f;
		int degrees = 33;
//		float rotateSpeed = 0.002f * p.loop.progressRads();
//		float rotateSpeed = 0.1f;

//		if(P.sin(p.loop.progress()) == 0) P.out("looped");
//		P.out(P.sin(p.loop.progressRads()));
		
		p.translate(width/2, height/2);
//		p.rotate(p.frameCount*rotateSpeed);
		p.rotate(p.loop.progressRads());
		
		for (int i=0; i < maxGradials; i++) {			
//			gradial(sin(p.frameCount * radiusSpeed + i/radiusFreq) * radiusGrow + radius, sin(p.frameCount * radiusSpeed + i/radiusFreq) * radiusGrow + radius , P.radians(i * degrees));
			gradial(sin(p.loop.progressRads()+ i/radiusFreq) * radiusGrow + radius, sin(p.loop.progressRads()+ i/radiusFreq) * radiusGrow + radius , P.radians(i * degrees));
			
			// color correction
			p.pushStyle();
			p.blendMode(MULTIPLY);
//			gradial(sin(p.frameCount * radiusSpeed + i/radiusFreq) * radiusGrow + radius, sin(p.frameCount * radiusSpeed + i/radiusFreq) * radiusGrow + radius , P.radians(i * degrees));
			gradial(sin(p.loop.progressRads()+ i/radiusFreq) * radiusGrow + radius, sin(p.loop.progressRads()+ i/radiusFreq) * radiusGrow + radius , P.radians(i * degrees));
			p.popStyle();
			
			radius -= gap;
			gap -= 2.2;
		}

		SaturationFilter.instance(p).setSaturation(2f);
		SaturationFilter.instance(p).applyTo(p.g);

	}
	
	public void chromeSetter(float _num) {
		chromeColorR = (float)(sin(_num)) * 127 + 127;
		chromeColorG = (float)(sin(_num + (1f/3f))) * 127 + 127;
		chromeColorB = (float)(sin(_num + (2f/3f))) * 127 + 127;
		chromeColor = p.color(chromeColorR, chromeColorG, chromeColorB);
	}
	
	public void gradial(float width, float height, float _rotation) {
		p.pushMatrix();
		
		p.rotate(_rotation);
		
		float halfW = width/2;
		float halfH = height/2;
		float numSegments = 500;

		float segmentRadians = P.TWO_PI / numSegments;
		p.noStroke();
		for(float r=0; r < P.TWO_PI; r += segmentRadians) {
			float r2 = r + segmentRadians;
			p.beginShape();
			chromeSetter(r);
			p.fill(chromeColor);
			p.vertex(0,0);
			p.vertex(P.sin(r) * halfW, P.cos(r) * halfH);
			p.vertex(P.sin(r2) * halfW, P.cos(r2) * halfH);
			p.endShape(P.CLOSE);
		}
		
		p.popMatrix();
	}
	
	
}