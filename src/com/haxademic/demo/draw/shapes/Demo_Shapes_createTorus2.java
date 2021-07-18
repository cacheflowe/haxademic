package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.pshader.DitherFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.RadialFlareFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class Demo_Shapes_createTorus2
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	int FRAMES = 60 * 10;

	protected PShape shape;
	protected float torusRadius;
	
	protected PShader matCapShader;
	protected ArrayList<PImage> matCapImages;
	protected String MATCAP_IMG_INDEX = "MATCAP_IMG_INDEX";
	
	protected PGraphics scaleDown;

	
	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, true);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		// build texture 
		scaleDown = PG.newPG(p.width / 6, p.height / 6, false, false);
		((PGraphicsOpenGL)scaleDown).textureSampling(2);
		
		// create torus w/quads
		torusRadius = p.width * 2;
		shape = Shapes.createTorus(torusRadius, torusRadius/20, 60, 18, null);
		// get triangulaed version if desired
		shape = shape.getTessellation();
		
		// rotate up front for ease of later fancy rotation
		PShapeUtil.meshRotateOnAxis(shape, P.HALF_PI, P.X);

		// matcap shader & textures
		matCapImages = FileUtil.loadImagesFromDir(FileUtil.getPath("haxademic/images/matcap/"), "png,jpg");
		matCapShader = p.loadShader(
				FileUtil.getPath("haxademic/shaders/lights/matcap/matcap-frag.glsl"), 
				FileUtil.getPath("haxademic/shaders/lights/matcap/matcap-vert.glsl")
		);
		UI.addSlider(MATCAP_IMG_INDEX, 25, 0, matCapImages.size() - 1, 1, false);
	}

	protected void drawApp() {
		background(0);
		PG.setCenterScreen(p.g);
		
		// fancier rotating rotation
		float curRads = -1f * FrameLoop.progressRads();
		p.translate(torusRadius * P.cos(curRads), torusRadius * P.sin(curRads), torusRadius/2);
//		p.rotateX(-curRads);
		p.rotateZ(curRads);
		p.rotateY(-curRads);

		// draw torus
	
		// matcap shader
		p.noLights();
		matCapShader.set("range", 0.97f);
		matCapShader.set("matcap", matCapImages.get(UI.valueInt(MATCAP_IMG_INDEX)));
		p.shader(matCapShader);

		// draw mesh w/matcap
		p.shape(shape);
		p.resetShader();

		// draw extra mesh wireframe
		p.noFill();
		p.stroke(255, 20);
		p.strokeWeight(2);
//		PShapeUtil.drawTriangles(p.g, shape, null, 1);
		
		// copy to small 
		ImageUtil.copyImage(p.get(), scaleDown);
		DebugView.setTexture("scaleDown", scaleDown);
		
		// postprocessing
		VignetteFilter.instance(p).setDarkness(0.5f);
		VignetteFilter.instance(p).setSpread(0.2f);
//		VignetteFilter.instance(p).applyTo(p.g);
		
		CubicLensDistortionFilter.instance(p).setAmplitude(-1f);
//		CubicLensDistortionFilter.instance(p).applyTo(p.g);
		
		SaturationFilter.instance(p).setSaturation(1.4f);
		SaturationFilter.instance(p).applyTo(scaleDown);
		
		GrainFilter.instance(p).setTime(p.frameCount * 0.02f);
		GrainFilter.instance(p).setCrossfade(0.05f);
//		GrainFilter.instance(p).applyTo(scaleDown);
		
		RadialFlareFilter.instance(p).setImageBrightness(12f);
		RadialFlareFilter.instance(p).setFlareBrightness(2f);
		RadialFlareFilter.instance(p).applyTo(scaleDown);
		
		DitherFilter.instance(P.p).setDitherMode8x8();
		DitherFilter.instance(P.p).applyTo(scaleDown);
		
		// draw scaled back up
		ImageUtil.copyImage(scaleDown, p.g);
	}

}
