package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_MeshDeformAndTextureFilter_SimpleSheetWireframe 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float cols = 5;
	protected float rows = 5;

	protected PShape shape;
	protected PGraphics texture;
	protected SimplexNoiseTexture displaceTexture;

	protected void setupFirstFrame() {
		int sheetW = P.round(cols * 100);
		int sheetH = P.round(cols * 100);
		
		// displace texture
		displaceTexture = new SimplexNoiseTexture(sheetW, sheetH);
		
		// create wireframe texture
		texture = p.createGraphics(sheetW, sheetH, P.P2D);
		
		// build sheet mesh
		shape = Shapes.createSheet((int) cols, (int) rows, texture);
		
		// debug view
		p.debugView.setValue("shape.getVertexCount();", shape.getVertexCount());
		p.debugView.setTexture("texture", texture);
		p.debugView.setTexture("displaceTexture", displaceTexture.texture());
	}

	public void drawApp() {
		// update texture
		texture.beginDraw();
		texture.background(0);
		texture.noFill();
		texture.stroke(255);
		texture.strokeWeight(3f);
		float cellW = (float) texture.width / cols;
		float cellH = (float) (texture.height) / rows;
		for (int x = 0; x <= texture.width; x += cellW) {
			float curX = P.min(x, texture.width - 1);
			texture.line(curX, 0, curX, texture.height);
		}
		for (int y = 0; y <= texture.height; y += cellH) {
			float curY = P.min(y, texture.height - 1);
			texture.line(0, curY, texture.height, curY);
		}
		texture.endDraw();
		
		// context & camera
		background(0);
		//p.image(texture, 0, 0);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);

		// deform mesh
		displaceTexture.offsetX(p.frameCount * 0.01f);
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(displaceTexture.texture());
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(200f);
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(p);

		// draw mesh
		p.scale(0.5f);
		p.shape(shape);
		p.resetShader();
	}
		
}