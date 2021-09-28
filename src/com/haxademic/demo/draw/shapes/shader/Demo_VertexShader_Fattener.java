package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_Fattener 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PImage texture;
	protected PShader fattenerVertShader;
	
	protected PGraphics buffer;
	PShader feedbackShader;

	protected void config() {
		int FRAMES = 300;
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
//		Config.setProperty(AppSettings.WIDTH, 1920);
//		Config.setProperty(AppSettings.HEIGHT, 1080);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false );
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 3);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 4);
	}
	
	protected void firstFrame() {
		buffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		
		// build obj PShape and scale to window
		// Note: Without getTesselation(), PShape.setTexture(PImage) is SUPER slow. 
		obj = DemoAssets.objSkullRealistic().getTessellation(); // p.loadShape(FileUtil.getPath("models/cacheflowe_2017-12-15_18-23-17.obj")).getTessellation();

		// normalize shape
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, p.height * 0.5f);
		
		// load shader
		fattenerVertShader = p.loadShader(
			FileUtil.getPath("haxademic/shaders/vertex/fattener-frag.glsl"), 
			FileUtil.getPath("haxademic/shaders/vertex/fattener-vert.glsl")
		);
		
		// Set UV coords & set texture on obj.
		// why is this necessary if it's not used??
		PShapeUtil.addTextureUVSpherical(obj, null);
		obj.setTexture(ImageUtil.imageToGraphics(DemoAssets.textureNebula()));

		// clear background
		background(0);
		feedbackShader = loadShader(FileUtil.getPath("haxademic/shaders/filters/feedback-radial-fattener-render.glsl"));
	}

	protected void drawApp() {
		
		// draw shape to buffer
		buffer.beginDraw();
		buffer.clear();
//		buffer.background(0);
		buffer.translate(p.width/2f, p.height/2f, 0);
		buffer.rotateY(0.3f * P.sin(P.QUARTER_PI + FrameLoop.progressRads()));
		
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		fattenerVertShader.set("time", FrameLoop.progressRads() * 2f);
		fattenerVertShader.set("amp", 20f + 20f * P.sin(FrameLoop.progressRads()));
//		fattenerVertShader.set("amp", p.height * 0.05f * P.map(P.sin(FrameLoop.progressRads()), -1, 1, 0, 1));
		buffer.noLights();
		buffer.shader(fattenerVertShader);  
		buffer.shape(obj);
		buffer.resetShader();
		buffer.endDraw();
		
		// do feedback & draw buffer to screen
		feedbackShader.set("amp", 0.00003f);
		feedbackShader.set("samplemult", 0.999f);
		p.filter(feedbackShader); p.filter(feedbackShader); p.filter(feedbackShader); 
		p.image(buffer, 0, 0);
	
		ContrastFilter.instance(p).setContrast(1.01f);
		ContrastFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).setDarkness(0.3f);
		VignetteFilter.instance(p).applyTo(p);
	}
		
}