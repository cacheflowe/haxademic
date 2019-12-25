package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_LightsNoiseTest_WIP 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PShape sheet;
	protected PShader shader;
	
	protected void config() {
		int FRAMES = 1000;
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}

	protected void firstFrame() {
		// build obj PShape and scale to window
		obj = DemoAssets.objSkullRealistic();
		sheet = Shapes.createSheet(40, 1000, 1000);
		
		// normalize shape
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, p.height * 0.60f);
		PShapeUtil.setRegistrationOffset(obj, 0, -0.04f, 0);
		
		// load shader
		shader = p.loadShader(
			FileUtil.getPath("haxademic/shaders/vertex/noise-light-frag.glsl"), 
			FileUtil.getPath("haxademic/shaders/vertex/noise-light-vert.glsl")
		);
		
		// Set UV coords & set texture on obj.
		PShapeUtil.addTextureUVSpherical(obj, null);
	}

	public void drawApp() {
		background(0);
		PG.setCenterScreen(p);

		// use shader
		shader.set("time", FrameLoop.progressRads());
		shader.set("lightDir", Mouse.xNorm, Mouse.yNorm, 0.9f);
		shader.set("lightsOn", 0);
		shader.set("lightAmbient", 0.1f, 0.1f * P.sin(FrameLoop.progressRads()), 0.5f);
		p.shader(shader);
		p.shape(sheet);
		p.rotateY(0.3f * P.sin(FrameLoop.progressRads()));
		shader.set("lightsOn", 1);
		p.shape(obj);
		p.resetShader();
	}
		
}