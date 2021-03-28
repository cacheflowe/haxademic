package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;

public class Demo_Shapes_createDisc3d
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	
	protected void firstFrame() {
		// radius, innerRadius, depth, detail, texture
		shape = Shapes.createDisc3d(p.height / 3f, p.height / 6f, 100, 90, DemoAssets.justin());
//		shape.setTexture(DemoAssets.justin());
	}

	protected void drawApp() {
		// set context
		background(40);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);
		p.noStroke();
		
		// draw can, either as PShape, or by drawing triangles with an arbitrary texture
		if(p.frameCount % 300 < 150) {
			MeshDeformAndTextureFilter.instance(p).setDisplacementMap(DemoAssets.justin());
			MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(FrameLoop.osc(0.03f, 0, 0.3f));
			MeshDeformAndTextureFilter.instance(p).setSheetMode(false);
			MeshDeformAndTextureFilter.instance(p).applyTo(p);

			p.shape(shape);
			
			p.resetShader();
		} else {
			p.lights();
			PShapeUtil.drawTriangles(p.g, shape, DemoAssets.squareTexture(), 1f);
		}
	}

}
