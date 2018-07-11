package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_LinesDeform 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage texture;
	protected PerlinTexture perlin;
	protected PShader displacementShader;
	protected float shapeExtent = 100;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, 160);
	}
	
	protected void setupFirstFrame() {
		// load texture
		perlin = new PerlinTexture(p, 256, 256);
		texture = perlin.texture();
		p.debugView.setTexture(texture);
		
		// build sheet mesh
		shape = p.createShape(P.GROUP);
		int rows = 200;
		int cols = 500;
		for (int y = 0; y < rows; y++) {
			PShape line = P.p.createShape();
			line.beginShape();
			line.stroke(255);
			line.strokeWeight(1);
			line.noFill();
			for (int x = 0; x < cols; x++) {
				line.vertex(x * 10f, y * 10f, 0);
			}
			line.endShape();
			shape.addChild(line);
		}
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 2f);
		PShapeUtil.addTextureUVToShape(shape, texture);
		shapeExtent = PShapeUtil.getMaxExtent(shape);

		shape.setTexture(texture);
		p.debugView.setValue("shape.getVertexCount();", PShapeUtil.vertexCount(shape));
		
		// load shader
		displacementShader = loadShader(
			FileUtil.getFile("haxademic/shaders/vertex/line-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/vertex/line-vert.glsl")
		);
	}

	public void drawApp() {
		background(0);
		
		// update displacement texture
		perlin.update(0.05f, 0.05f, p.frameCount * 0.01f, 0);
		
		// rotate
		DrawUtil.setCenterScreen(p);
		DrawUtil.basicCameraFromMouse(p.g);

		// draw mesh
		shape.disableStyle();
		p.stroke(255);
		displacementShader.set("displacementMap", perlin.texture());
		displacementShader.set("colorMap", DemoAssets.textureNebula());
		displacementShader.set("displaceStrength", p.mousePercentY() * pg.height * 0.7f);
		displacementShader.set("weight", p.mousePercentX() * 20f);
		displacementShader.set("modelMaxExtent", shapeExtent * 2f);
		p.shader(displacementShader, P.LINES);  
		p.shape(shape);
		p.resetShader();
	}
		
}