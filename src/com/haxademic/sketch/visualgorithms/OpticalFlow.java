package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;
import processing.video.Movie;

public class OpticalFlow
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie testMovie;
	protected PGraphics lastFrame;
	protected PGraphics curFrame;
	protected PShader opFlowShader;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 480 );
	}

	public void setupFirstFrame() {
		// load movie
		testMovie = new Movie(p, FileUtil.getFile("haxademic/video/kinect-silhouette-test.mp4"));
		testMovie.jump(0);
		testMovie.loop();
		
		// create buffers
		curFrame = p.createGraphics(p.width, p.height, PRenderers.P3D);
		lastFrame = p.createGraphics(p.width, p.height, PRenderers.P3D);
		p.debugView.setTexture(curFrame);
		p.debugView.setTexture(lastFrame);
		
		// load shader
		opFlowShader = p.loadShader(FileUtil.getFile("shaders/filters/optical-flow.glsl"));
	}

	public void drawApp() {
		p.background(0);
		
		if(testMovie.width > 10) {
			// copy movie frames
			ImageUtil.cropFillCopyImage(curFrame, lastFrame, true);
			ImageUtil.cropFillCopyImage(testMovie.get(), curFrame, true);
			
			// update/draw shader
			opFlowShader.set("tex0", curFrame);
			opFlowShader.set("tex1", lastFrame);
			p.filter(opFlowShader);
		}
	}

}








