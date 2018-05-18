package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.filters.shaders.ContrastFilter;
import com.haxademic.core.draw.filters.shaders.VignetteAltFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_Fattener 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PImage texture;
	protected PShader fattenerVertShader;
	
	protected PGraphics buffer;
	PShader feedbackShader;

	protected void overridePropsFile() {
		int FRAMES = 340;
		p.appConfig.setProperty(AppSettings.WIDTH, 600);
		p.appConfig.setProperty(AppSettings.HEIGHT, 700);
//		p.appConfig.setProperty(AppSettings.WIDTH, 1920);
//		p.appConfig.setProperty(AppSettings.HEIGHT, 1080);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 3);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 4);
	}
	
	protected void setupFirstFrame() {
		buffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		
		// build obj PShape and scale to window
		// Note: Without getTesselation(), PShape.setTexture(PImage) is SUPER slow. 
		obj = p.loadShape(FileUtil.getFile("models/cacheflowe_2017-12-15_18-23-17.obj")).getTessellation();

		// normalize shape
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, p.height * 0.30f);
		
		// load shader
		fattenerVertShader = p.loadShader(
			FileUtil.getFile("haxademic/shaders/vertex/fattener-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/vertex/fattener-vert.glsl")
		);
		
		// Set UV coords & set texture on obj.
		// why is this necessary if it's not used??
		PShapeUtil.addTextureUVSpherical(obj, null);
		obj.setTexture(ImageUtil.imageToGraphics(DemoAssets.textureNebula()));

		// clear background
		background(0);
		feedbackShader = loadShader(FileUtil.getFile("haxademic/shaders/filters/feedback-radial-fattener-render.glsl"));
	}

	public void drawApp() {
		
		// draw shape to buffer
		buffer.beginDraw();
		buffer.clear();
		buffer.translate(p.width/2f, p.height/2f, 0);
		buffer.rotateY(0.5f * P.sin(loop.progressRads()));
		
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		fattenerVertShader.set("time", loop.progressRads() * 2f);
//		fattenerVertShader.set("amp", 20f + 20f * P.sin(loop.progressRads()));
		fattenerVertShader.set("amp", p.height * 0.05f);
		buffer.noLights();
		buffer.shader(fattenerVertShader);  
		buffer.shape(obj);
		buffer.resetShader();
		buffer.endDraw();
		
		// do feedback & draw buffer to screen
		feedbackShader.set("amp", 0.00003f);
		feedbackShader.set("samplemult", 0.998f);
		p.filter(feedbackShader); p.filter(feedbackShader); p.filter(feedbackShader); 
		p.image(buffer, 0, 0);
	
		ContrastFilter.instance(p).setContrast(1.01f);
		ContrastFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).setDarkness(0.3f);
		VignetteFilter.instance(p).applyTo(p);
	}
		
}