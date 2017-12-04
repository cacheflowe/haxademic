package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.MirrorFilter;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_PointsPShapeAndTextureMapDeform 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PerlinTexture perlin;
	protected BaseTexture audioTexture;
	protected float _frames = 360;

	protected PShape shape;
	protected PImage texture;
	protected PShader pointsTexturedShader;
	protected PGraphics buffer;
	float w = 1024;
	float h = 512;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 512);
		p.appConfig.setProperty(AppSettings.FULLSCREEN, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, (int) _frames);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, (int) (_frames * 2));
		p.appConfig.setProperty(AppSettings.APP_VIEWER_WINDOW, true);
	}

	protected void setupFirstFrame() {
		// load & set texture
		// perlin = new PerlinTexture(p, (int) w, (int) h);
		audioTexture = new TextureEQGrid((int) w, (int) h);
		texture = audioTexture.texture();
		// texture = perlin.texture();
		// texture = DemoAssets.textureNebula();

		// build offsecreen buffer (thing don't work the same on the main drawing surface)
		buffer = p.createGraphics(p.width, p.height, PRenderers.P3D);

		// count vertices for debugView
		int vertices = P.round( w * h); 
		p.debugView.setValue("Vertices", vertices);
		
		// Build points vertices
		shape = P.p.createShape();
		shape.beginShape(PConstants.POINTS);
		shape.stroke(255);
		shape.strokeWeight(1);
		shape.noFill();
		for (int i = 0; i < vertices; i++) {
			float x = i % w;
			float y = P.floor(i / w);
			if(y % 2 == 1) x = w - x - 1;
			shape.vertex(x/w, y/h, 0); // x/y coords are used as UV coords (0-1), and multplied by `spread` uniform
		}
		shape.endShape();
		shape.setTexture(texture);
		
		// load shader
		pointsTexturedShader = loadShader(
			FileUtil.getFile("shaders/point/point-frag.glsl"), 
			FileUtil.getFile("shaders/point/point-vert.glsl")
		);

		// clear the screen
		background(0);
	}

	public void drawApp() {
		// calculate loop
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		// update displacement texture
		// perlin.update(0.15f, 0.05f, p.frameCount/ 10f, 0);
		audioTexture.update();
		MirrorFilter.instance(p).applyTo(audioTexture.texture());
		
		// fade background
		buffer.beginDraw();
		DrawUtil.fadeToBlack(buffer, 60);
		
		// move to screen center
		buffer.translate(p.width/2f, p.height/2f, 0);
		
		// draw vertex points. strokeWeight w/disableStyle works here for point size
		shape.disableStyle();
		buffer.strokeWeight(1.0f);
		pointsTexturedShader.set("displacementMap", texture);
		pointsTexturedShader.set("pointSize", 1f); // 2.5f + 1.5f * P.sin(P.TWO_PI * percentComplete));
		pointsTexturedShader.set("width", w);
		pointsTexturedShader.set("height", h);
		pointsTexturedShader.set("spread", 2.5f + 0.5f * P.sin(P.PI + 2f * P.TWO_PI * percentComplete));
		pointsTexturedShader.set("mixVal", 0.5f + 0.5f * P.sin(P.TWO_PI * percentComplete));
		pointsTexturedShader.set("displaceStrength", 130f);//130f + 130f * P.sin(P.PI + P.TWO_PI * percentComplete));
		buffer.shader(pointsTexturedShader);  
		buffer.shape(shape);
		buffer.resetShader();
		buffer.endDraw();
		
		// draw buffer to screen
		p.image(buffer, 0, 0);
	}
		
}