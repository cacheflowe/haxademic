package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class Demo_Shapes_createDisc
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	
	protected void firstFrame() {
		shape = Shapes.createDisc(p.height / 2, 36, 20, null);
		shape.setTexture(DemoAssets.textureNebula());
	}

	protected void drawApp() {
		background(0);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);
		
		DebugView.setTexture("texture", DemoAssets.textureNebula());
		// draw can
//		shape.disableStyle();
//		p.fill(255);
//		p.noFill();
//		p.stroke(255);
//		p.strokeWeight(2);
		if(p.frameCount % 100 < 50) {
			// deform mesh
			MeshDeformAndTextureFilter.instance().setDisplacementMap(DemoAssets.textureNebula());
			MeshDeformAndTextureFilter.instance().setDisplaceAmp(100f * P.sin(p.frameCount * 0.03f));
			MeshDeformAndTextureFilter.instance().setSheetMode(false);
			MeshDeformAndTextureFilter.instance().setOnContext(p);

			// draw mesh
			p.shape(shape);
			p.resetShader();

			
//			p.shape(shape);
		} else {
			PShapeUtil.drawTriangles(p.g, shape, DemoAssets.squareTexture(), 1f);
		}
	}

}
