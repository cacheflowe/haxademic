package com.haxademic.sketch.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class DepthVertexTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected float frames = 60 * 16;
	protected float progress = 0;
	
	PShader depthShader;
	PGraphics canvas;
	PImage backImage;

	// https://forum.processing.org/two/discussion/3250/how-to-store-information-in-the-alpha-channel
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 600 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(frames) );
		p.appConfig.setProperty( AppSettings.RETINA, false );
	}

	public void setup() {
		super.setup();	
		
		canvas = createGraphics(width, height, P3D);
		canvas.beginDraw();
		canvas.noStroke();
		canvas.endDraw();
		
		backImage = loadImage("http" + "://upload.wikimedia.org/wikipedia/commons/thumb/5/59/Processing_Logo_Clipped.svg/256px-Processing_Logo_Clipped.svg.png");
		depthShader = new PShader(this, FileUtil.getFile("haxademic/shaders/vertex/depth-vert.glsl"), FileUtil.getFile("haxademic/shaders/vertex/depth-frag.glsl"));
		canvas.shader(depthShader);
		
	}
	
	public void drawApp() {
		depthShader.set("screen", (float)width, (float)height, (float)300, (float)600);
	    canvas.beginDraw();
	    canvas.blendMode(REPLACE);
	 
	    canvas.background(0, 0);
//	    canvas.fill(100,200,100);
	    canvas.translate(width / 2, height / 2);
	    canvas.rotate(frameCount / 100.0f, 1.0f, 0.75f, 0.5f);
	    canvas.box(300);
	    canvas.endDraw();
	    

	 
	    image(backImage, -40, -40, width + 80, height + 80);
	    image(canvas, 0, 0);
	}
	
}
