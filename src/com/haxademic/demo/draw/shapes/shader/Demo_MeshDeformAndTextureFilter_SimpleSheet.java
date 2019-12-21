package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.PerlinTexture;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_MeshDeformAndTextureFilter_SimpleSheet 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage texture;
	protected PerlinTexture perlin;

	protected void firstFrame() {
		// load texture
		perlin = new PerlinTexture(p, 128, 64);
		texture = perlin.texture();
		
		// build sheet mesh
		shape = Shapes.createSheet(60, texture);
		
		// debug view
		DebugView.setValue("shape.getVertexCount();", shape.getVertexCount());
		DebugView.setTexture("PerlinTexture", texture);
		DebugView.setTexture("textureNebula", DemoAssets.textureNebula());
	}

	public void drawApp() {
		// update displacement texture
		perlin.update(0.05f, 0.05f, p.frameCount * 0.01f, 0);
		
		// context & camera
		background(0);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);

		// deform mesh
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(texture);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(100f);
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(p);
		// set texture using PShape method
		shape.setTexture(DemoAssets.textureNebula());

		// draw mesh
		p.scale(4f);
		p.shape(shape);
		p.resetShader();
	}
		
}