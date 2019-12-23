package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ReflectFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.PerlinTexture;
import com.haxademic.core.draw.textures.pgraphics.TextureEQGrid;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesTextureMapDeform 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PerlinTexture perlin;
	protected BaseTexture audioTexture;

	protected PShape shape;
	protected PImage texture;
	protected PShader pointsTexturedShader;
	protected PGraphics buffer;
	float w = 1024;
	float h = 512;
	int FRAMES = 300;

	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 512);
		Config.setProperty(AppSettings.FULLSCREEN, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}

	protected void firstFrame() {
		// load & set texture
		// perlin = new PerlinTexture(p, (int) w, (int) h);
		audioTexture = new TextureEQGrid((int) w, (int) h);
		texture = audioTexture.texture();
		// texture = perlin.texture();
		// texture = DemoAssets.textureNebula();

		// build offsecreen buffer (things don't work the same on the main drawing surface)
		buffer = p.createGraphics(p.width, p.height, PRenderers.P3D);

		// count vertices for debugView
		int vertices = P.round(w * h); 
		DebugView.setValue("Vertices", vertices);
		
		// Build points vertices
		shape = PShapeUtil.pointsShapeForGPUData((int) w, (int) h);
		shape.setTexture(texture);
		
		// load shader
		pointsTexturedShader = loadShader(
			FileUtil.getFile("haxademic/shaders/point/points-default-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/point/points-interp-shapes-vert.glsl")
		);

		// clear the screen
		background(0);
	}

	public void drawApp() {
		// update displacement texture
		// perlin.update(0.15f, 0.05f, p.frameCount/ 10f, 0);
		audioTexture.update();
		ReflectFilter.instance(p).applyTo(audioTexture.texture());
		
		// fade background
		buffer.beginDraw();
		PG.fadeToBlack(buffer, 60);
		
		// move to screen center
		buffer.translate(p.width/2f, p.height/2f, 0);
		
		// draw vertex points. strokeWeight w/disableStyle works here for point size
		shape.disableStyle();
		buffer.strokeWeight(1.0f);
		pointsTexturedShader.set("displacementMap", texture);
		pointsTexturedShader.set("pointSize", 1f); // 2.5f + 1.5f * P.sin(P.TWO_PI * percentComplete));
		pointsTexturedShader.set("width", w);
		pointsTexturedShader.set("height", h);
		pointsTexturedShader.set("spread", 2.5f + 0.5f * P.sin(P.PI + 2f * FrameLoop.progressRads()));
		pointsTexturedShader.set("mixVal", 0.5f + 0.5f * P.sin(FrameLoop.progressRads()));
		pointsTexturedShader.set("displaceStrength", 130f);//130f + 130f * P.sin(P.PI + P.TWO_PI * percentComplete));
		buffer.shader(pointsTexturedShader);  
		buffer.shape(shape);
		buffer.resetShader();
		buffer.endDraw();
		
		// draw buffer to screen
		p.image(buffer, 0, 0);
	}
		
}