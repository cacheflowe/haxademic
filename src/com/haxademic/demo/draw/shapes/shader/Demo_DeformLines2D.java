package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;

import processing.core.PShape;

public class Demo_DeformLines2D 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int rows = 40;
	protected int cols = 200;

	protected PShaderHotSwap linesShader;

	protected PShape shape;
	protected SimplexNoise3dTexture displaceTexture;
	protected float shapeExtent = 100;

	protected void firstFrame() {
		// displace texture - 3D noise gives us 3-color output plus an offsetZ to scroll without repeats
		displaceTexture = new SimplexNoise3dTexture(512, 512, true);

		// build a grid of horizontal line PShapes - one polyline per row
		shape = p.createShape(P.GROUP);
		float sheetWidth = 800;
		float sheetHeight = 800;
		float cellW = sheetWidth / (float) cols;
		for (int row = 0; row <= rows; row++) {
			PShape line = P.p.createShape();
			line.beginShape();
			line.stroke(255);
			line.strokeWeight(1);
			line.noFill();
			float y = -sheetHeight / 2f + row * (sheetHeight / (float) rows);
			for (int col = 0; col <= cols; col++) {
				float x = -sheetWidth / 2f + col * cellW;
				line.vertex(x, y, 0);
			}
			line.endShape();
			shape.addChild(line);
		}

		// UV coords + texture are required for the PROCESSING_LINE_SHADER pipeline,
		// even though lines-deform-2d-vert.glsl samples displacement by position, not texCoord
		PShapeUtil.addTextureUVToShape(shape, displaceTexture.texture());
		shape.disableStyle();
		shapeExtent = PShapeUtil.getMaxExtent(shape);

		// custom copy of lines-deform-vert/frag - free to hand-edit the displacement math
		linesShader = new PShaderHotSwap(
			FileUtil.getPath("haxademic/shaders/vertex/lines-deform-2d-vert.glsl"),
			FileUtil.getPath("haxademic/shaders/vertex/lines-deform-2d-frag.glsl")
		);

		// debug view
		DebugView.setValue("shape.getVertexCount();", PShapeUtil.vertexCount(shape));
		DebugView.setTexture("displaceTexture", displaceTexture.texture());
	}

	protected void drawApp() {
		p.background(0);

		// update displacement texture
		displaceTexture.offsetZ(p.frameCount * 0.01f);
		displaceTexture.zoom(2.0f);

		// context & camera
		p.g.push();
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g, 0.1f);

		// apply lines-deform shader and draw mesh
		// mouse x picks the displacement mode: left = z push, right = x/y push on the z-plane
		boolean xyDisplaceMode = Mouse.xNorm > 0.5f;
		linesShader.shader().set("displacementMap", displaceTexture.texture());
		linesShader.shader().set("colorMap", displaceTexture.texture());
		linesShader.shader().set("weight", 10f);
		linesShader.shader().set("modelMaxExtent", shapeExtent);
		linesShader.shader().set("displaceMode", xyDisplaceMode ? 1 : 0);
		linesShader.shader().set("displaceAmp", Mouse.yNorm * (xyDisplaceMode ? 60f : 200f));
		linesShader.update();
		p.shader(linesShader.shader());
		p.stroke(255);
		p.shape(shape);
		p.resetShader();
		p.g.pop();

		// recompile if needed & show shader compile error messages
		p.g.push();
		p.g.translate(300, 0);
		linesShader.showShaderStatus(p.g);
		p.g.pop();
	}
		
}