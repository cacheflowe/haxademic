package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_MeshDeformAndTextureFilter_SVG 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float cols = 5;
	protected float rows = 5;

	protected PShape shape;
	protected PGraphics texture;
	protected SimplexNoiseTexture displaceTexture;

	protected void firstFrame() {
		int sheetW = P.round(cols * 100);
		int sheetH = P.round(cols * 100);
		
		// displace texture
		displaceTexture = new SimplexNoiseTexture(sheetW, sheetH);
		
		// create wireframe texture
		texture = p.createGraphics(displaceTexture.texture().width, displaceTexture.texture().width, P.P2D);
		texture.beginDraw();
		texture.background(0, 255, 0);
		texture.endDraw();
		
		// build sheet mesh
		shape = p.loadShape( FileUtil.haxademicDataPath() + "svg/pot-leaf-expanded.svg" ).getTessellation();
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.7f);
		PShapeUtil.addTextureUVExactWidthHeight(shape, texture, displaceTexture.texture().width * 0.1f, displaceTexture.texture().height * 0.1f);
		shape.setFill(255);
		PShapeUtil.setMaterialColor(shape, p.color(255));
		
		// debug view
		DebugView.setValue("shape.getVertexCount();", shape.getVertexCount());
		DebugView.setTexture("texture", texture);
		DebugView.setTexture("displaceTexture", displaceTexture.texture());
	}

	public void drawApp() {		
		// context & camera
		background(0);
		//p.image(texture, 0, 0);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);

		// post effect on displacement texture
//		ThresholdFilter.instance(p).applyTo(displaceTexture.texture());
		
		// deform mesh
		displaceTexture.offsetX(p.frameCount * 0.01f);
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(displaceTexture.texture());
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(200f);
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(p);

		// draw mesh
		shape.disableStyle();
		// p.scale(0.35f);
		p.fill(255);
		p.stroke(255);
		p.shape(shape);
		p.resetShader();
	}
		
}