package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderCompiler;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class Demo_VertexShader_PShaderCompiler_Rippleskin 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape icosa;
	protected PImage texture;
	protected PShader shader;

	protected void config() {
		int FRAMES = 340;
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false );
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 3);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 4);
	}
	
	protected void firstFrame() {
		buildShape();
		rebuildShader();
	}
	
	protected void buildShape() {
		int detail = 9;
		icosa = Icosahedron.createIcosahedron(p.g, detail, null);// DemoAssets.textureJupiter());
		PShapeUtil.scaleShapeToHeight(icosa, p.height * 0.5f);
	}

	protected void rebuildShader() {
		PShaderCompiler newShader = new PShaderCompiler(p, vertSource(), fragSource());
		if(newShader.isValid()) {
			shader = newShader;
		}
	}
	
	protected String[] vertSource() {
		return FileUtil.readTextFromFile(FileUtil.getPath("haxademic/shaders/vertex/inline-vertcolor-150-vert.glsl"));
	}
	
	protected String[] fragSource() {
		return FileUtil.readTextFromFile(FileUtil.getPath("haxademic/shaders/vertex/inline-vertcolor-150-frag.glsl"));
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') rebuildShader();
	}
	
	protected void drawApp() {
		// set context
		pg.beginDraw();
		pg.background(0);
		pg.noLights();
		PG.setCenterScreen(pg);
//		PG.basicCameraFromMouse(pg);
		pg.rotateY(FrameLoop.progressRads());
		
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		shader.set("time", FrameLoop.progressRads());
		shader.set("displaceAmp", 0.4f);
		shader.set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);

		// apply shader, draw shape
		pg.shader(shader);  
		pg.shape(icosa);
		pg.resetShader();
		pg.endDraw();
		
		// draw pg to screen
		p.image(pg, 0, 0);
	}
		
}