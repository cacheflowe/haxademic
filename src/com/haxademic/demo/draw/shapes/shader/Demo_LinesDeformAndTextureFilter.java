package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.LinesDeformAndTextureFilter;
import com.haxademic.core.draw.textures.PerlinTexture;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_LinesDeformAndTextureFilter 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage texture;
	protected PerlinTexture perlin;
	protected float shapeExtent = 100;

	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, 160);
	}
	
	protected void firstFrame() {
		// load texture
		perlin = new PerlinTexture(p, 256, 256);
		texture = perlin.texture();
		DebugView.setTexture("texture", texture);
		
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
		DebugView.setValue("shape.getVertexCount();", PShapeUtil.vertexCount(shape));
	}

	protected void drawApp() {
		background(0);
		
		// update displacement texture
		perlin.update(0.05f, 0.05f, p.frameCount * 0.01f, 0);
		
		// rotate
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);

		// draw shader-displaced mesh
		LinesDeformAndTextureFilter.instance().setDisplacementMap(perlin.texture());
		LinesDeformAndTextureFilter.instance().setColorMap(DemoAssets.textureNebula());
		LinesDeformAndTextureFilter.instance().setWeight(Mouse.xNorm * 20f);
		LinesDeformAndTextureFilter.instance().setModelMaxExtent(shapeExtent * 2f);
		LinesDeformAndTextureFilter.instance().setColorThicknessMode((Mouse.yNorm > 0.5f));
		if(Mouse.xNorm > 0.5f) {
			LinesDeformAndTextureFilter.instance().setSheetMode(true);
			LinesDeformAndTextureFilter.instance().setDisplaceAmp(Mouse.yNorm * pg.height * 0.7f);
		} else {
			LinesDeformAndTextureFilter.instance().setSheetMode(false);
			LinesDeformAndTextureFilter.instance().setDisplaceAmp(Mouse.yNorm * pg.height * 0.01f);
		}
		//		p.shader(displacementShader, P.LINES);
		LinesDeformAndTextureFilter.instance().setOnContext(p);

		p.stroke(255);
		p.shape(shape);
		p.resetShader();
	}
		
}