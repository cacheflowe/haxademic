package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.LinesDeformAndTextureFilter;
import com.haxademic.core.file.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_LinesDeformAndTextureFilter 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage texture;
	protected PerlinTexture perlin;
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
		shape.disableStyle();

		shape.setTexture(texture);
		p.debugView.setValue("shape.getVertexCount();", PShapeUtil.vertexCount(shape));
	}

	public void drawApp() {
		background(0);
		
		// update displacement texture
		perlin.update(0.05f, 0.05f, p.frameCount * 0.01f, 0);
		
		// rotate
		DrawUtil.setCenterScreen(p);
		DrawUtil.basicCameraFromMouse(p.g);

		// draw shader-displaced mesh
		LinesDeformAndTextureFilter.instance(p).setDisplacementMap(perlin.texture());
		LinesDeformAndTextureFilter.instance(p).setColorMap(DemoAssets.textureNebula());
		LinesDeformAndTextureFilter.instance(p).setWeight(p.mousePercentX() * 20f);
		LinesDeformAndTextureFilter.instance(p).setModelMaxExtent(shapeExtent * 2f);
		LinesDeformAndTextureFilter.instance(p).setColorThicknessMode((p.mousePercentY() > 0.5f));
		if(p.mousePercentX() > 0.5f) {
			LinesDeformAndTextureFilter.instance(p).setSheetMode(true);
			LinesDeformAndTextureFilter.instance(p).setDisplaceAmp(p.mousePercentY() * pg.height * 0.7f);
		} else {
			LinesDeformAndTextureFilter.instance(p).setSheetMode(false);
			LinesDeformAndTextureFilter.instance(p).setDisplaceAmp(p.mousePercentY() * pg.height * 0.01f);
		}
		//		p.shader(displacementShader, P.LINES);
		LinesDeformAndTextureFilter.instance(p).applyTo(p);

		p.stroke(255);
		p.shape(shape);
		p.resetShader();
	}
		
}