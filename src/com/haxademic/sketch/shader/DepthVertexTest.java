package com.haxademic.sketch.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class DepthVertexTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected float frames = 60 * 16;
	protected float progress = 0;
	
	PShader depthShader;
	PGraphics canvas;
	PImage backImage;

	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 600 );
		Config.setProperty( AppSettings.HEIGHT, 600 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false);
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(frames) );
		Config.setProperty( AppSettings.RETINA, false );
	}

	protected void firstFrame() {
		canvas = createGraphics(width, height, P3D);
		canvas.beginDraw();
		canvas.noStroke();
		canvas.endDraw();
		
		backImage = DemoAssets.justin();
		
		// load shader and set once - it'll persist between frames
		// https://forum.processing.org/two/discussion/3250/how-to-store-information-in-the-alpha-channel
		depthShader = new PShader(this, 
			FileUtil.getPath("haxademic/shaders/vertex/depth-vert.glsl"), 
			FileUtil.getPath("haxademic/shaders/vertex/depth-frag.glsl")
		);
		canvas.shader(depthShader);
		
	}
	
	protected void drawApp() {
		depthShader.set("screen", (float)width, (float)height, (float)300, (float)600);
	    canvas.beginDraw();
//	    canvas.blendMode(REPLACE);
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
