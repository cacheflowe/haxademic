package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.PerlinTexture;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_MeshDeformAndTextureFilter_SimpleSheetTextureMatch 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage displacementTexture;
	protected PImage texture;
	protected PerlinTexture perlin;
	
	protected void setupFirstFrame() {
		// load texture
		perlin = new PerlinTexture(p, 64, 64);
		displacementTexture = perlin.texture();
		
		// load texture
		texture = DemoAssets.justin();
		
		// build sheet mesh
		shape = Shapes.createSheet(48, 48, texture);
		
		// debug view
		p.debugView.setValue("shape.getVertexCount();", shape.getVertexCount());
		p.debugView.setTexture("displacementTexture", displacementTexture);
		p.debugView.setTexture("texture", texture);
	}

	public void drawApp() {
		// update displacement texture
		perlin.update(0.05f, 0.5f, p.frameCount * 0.01f, 0);
		
		// context & camera
		background(255);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);

		// deform mesh
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(displacementTexture);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(200f);
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(p);

		// draw mesh
		p.scale(1f);
		p.shape(shape);
		p.resetShader();
	}
		
}