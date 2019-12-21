package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class Demo_VertexShader_TracerstarTexturedShape 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape icosa;
	protected PImage texture;
	protected PShader tracerstarShader;

	protected void config() {
		int FRAMES = 340;
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false );
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 3);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 4);
	}
	
	protected void firstFrame() {
		// create icosa
		int detail = 9;
		icosa = Icosahedron.createIcosahedron(p.g, detail, null);// DemoAssets.textureJupiter());
		PShapeUtil.scaleShapeToHeight(icosa, p.height * 0.65f);
		
		// load shader
		tracerstarShader = p.loadShader(
//			FileUtil.getFile("haxademic/shaders/vertex/tracerstar-texture-frag.glsl"), 
//			FileUtil.getFile("haxademic/shaders/vertex/tracerstar-texture-vert.glsl")
			FileUtil.getFile("haxademic/shaders/vertex/tracerstar-vertcolor-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/vertex/tracerstar-vertcolor-vert.glsl")
		);
	}

	public void drawApp() {
		
		// set context
		pg.beginDraw();
		pg.background(0);
		pg.noLights();
		PG.setCenterScreen(pg);
		PG.basicCameraFromMouse(pg);
//		pg.rotateY(0.5f * P.sin(loop.progressRads()));
		
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		tracerstarShader.set("time", loop.progressRads());
		tracerstarShader.set("displaceAmp", 0.2f);
		tracerstarShader.set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);

		// apply shader, draw shape
		pg.shader(tracerstarShader);  
		pg.shape(icosa);
		pg.resetShader();
		pg.endDraw();
		
		// draw pg to screen
		p.image(pg, 0, 0);
	}
		
}