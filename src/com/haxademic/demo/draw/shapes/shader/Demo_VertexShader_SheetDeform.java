package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_SheetDeform 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage texture;
	protected PerlinTexture perlin;
	protected PShader displacementShader;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, 160);
	}
	
	protected void setupFirstFrame() {
		// load texture
		perlin = new PerlinTexture(p, 128, 64);
		texture = perlin.texture();
		
		// build sheet mesh
		shape = Shapes.createSheet(60, texture);
		shape.setTexture(DemoAssets.textureNebula());
		p.debugView.setValue("shape.getVertexCount();", shape.getVertexCount());
		
		// load shader
		displacementShader = loadShader(
			FileUtil.getFile("haxademic/shaders/vertex/brightness-displace-frag-texture.glsl"), 
			FileUtil.getFile("haxademic/shaders/vertex/brightness-displace-sheet-vert.glsl")
		);
	}

	public void drawApp() {
		background(0);
		
		// update displacement texture
		perlin.update(0.05f, 0.05f, p.frameCount * 0.01f, 0);
		
		// rotate
		DrawUtil.setCenterScreen(p.g);
		DrawUtil.basicCameraFromMouse(p.g);

		// draw mesh with texture or without
		displacementShader.set("displacementMap", perlin.texture());
		displacementShader.set("displaceStrength", 100f);
		p.shader(displacementShader);
		p.scale(4f);
		p.shape(shape);
		p.resetShader();
	}
		
}