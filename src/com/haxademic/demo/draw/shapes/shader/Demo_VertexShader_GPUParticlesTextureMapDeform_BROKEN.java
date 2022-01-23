package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.ReflectFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.PerlinTexture;
import com.haxademic.core.draw.textures.pgraphics.TextureEQGrid;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_VertexShader_GPUParticlesTextureMapDeform_BROKEN 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PerlinTexture perlin;
	protected BaseTexture audioTexture;

	protected PShape shape;
	protected PImage texture;
	protected PShaderHotSwap pointsTexturedShader;
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
		AudioIn.instance();
		
		// load & set texture
		// perlin = new PerlinTexture(p, (int) w, (int) h);
		audioTexture = new TextureEQGrid((int) w, (int) h);
		texture = audioTexture.texture();
		// texture = perlin.texture();
		texture = DemoAssets.textureNebula();
//		DebugView.setTexture("Vertices", audioTexture.texture());

		// build offsecreen buffer (things don't work the same on the main drawing surface)
		buffer = PG.newPG(p.width, p.height);

		// count vertices for debugView
		int vertices = P.round(w * h); 
		DebugView.setValue("Vertices", vertices);
		
		// Build points vertices
		shape = PShapeUtil.pointsShapeForGPUData((int) w, (int) h);
		PShapeUtil.centerShape(shape);
		shape.setTexture(texture);
		
		// load shader
		pointsTexturedShader = new PShaderHotSwap(
			FileUtil.getPath("haxademic/shaders/point/points-default-frag.glsl"), 
			FileUtil.getPath("haxademic/shaders/point/points-interp-shapes-vert.glsl")
		);

		// clear the screen
		background(0);
	}

	protected void drawApp() {
		// check shader hot swap
		pointsTexturedShader.update();
		
		// update displacement texture
		// perlin.update(0.15f, 0.05f, p.frameCount/ 10f, 0);
		audioTexture.update();
		ReflectFilter.instance(p).applyTo(audioTexture.texture());

		shape.disableStyle();
//		shape.enableStyle();
		
		// fade background
		buffer.beginDraw();
		buffer.background(0,255,0);
		PG.setCenterScreen(buffer);
		PG.basicCameraFromMouse(buffer);
		PG.fadeToBlack(buffer, 60);
		buffer.fill(255);
		
		// draw vertex points. strokeWeight w/disableStyle works here for point size
		buffer.strokeWeight(5.0f);
		pointsTexturedShader.shader().set("displacementMap", texture);
		pointsTexturedShader.shader().set("pointSize", 4f); // 2.5f + 1.5f * P.sin(P.TWO_PI * percentComplete));
		pointsTexturedShader.shader().set("width", w);
		pointsTexturedShader.shader().set("height", h);
		pointsTexturedShader.shader().set("spread", 2.5f + 0.5f * P.sin(P.PI + 2f * FrameLoop.progressRads()));
		pointsTexturedShader.shader().set("mixVal", 0.5f + 0.5f * P.sin(FrameLoop.progressRads()));
		pointsTexturedShader.shader().set("displaceStrength", 130f + 130f * P.sin(P.PI + FrameLoop.progressRads()));//);
		
		buffer.shader(pointsTexturedShader.shader());  
		buffer.shape(shape);
		buffer.resetShader();
		buffer.endDraw();
		
		// draw buffer to screen
		p.image(buffer, 0, 0);
	}
		
}