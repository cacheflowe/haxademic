package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.Mesh2dDeformFilter;
import com.haxademic.core.draw.shapes.pshader.PShaderHotSwap;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;

public class Demo_DeformMesh2D 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float cols = 5;
	protected float rows = 5;

	protected PShaderHotSwap shaderHotSwap;
	
	protected PShape shape;
	protected SimplexNoiseTexture displaceTexture;

	protected void setupFirstFrame() {
		int sheetW = P.round(cols * 100);
		int sheetH = P.round(cols * 100);
		
		// displace texture
		displaceTexture = new SimplexNoiseTexture(sheetW, sheetH);
		
		// build sheet mesh
		shape = Shapes.createSheet(10, 400, 400);
		
		// build lines shape
//		shape = p.createShape(P.GROUP);
////		int rows = 200;
////		int cols = 500;
//		int detail = 10;
//		float cellW = width / detail;
//		float cellH = height / detail;
////		for (int y = 0; y < rows; y++) {
////			PShape line = P.p.createShape();
////			line.beginShape();
////			line.stroke(255);
////			line.strokeWeight(1);
////			line.noFill();
////			for (int x = 0; x < cols; x++) {
////				line.vertex(x * 10f, y * 10f, 0);
////			}
////			line.endShape();
////			shape.addChild(line);
////		}
//		for (int col = 0; col < detail; col++) {
//			for (int row = 0; row < detail; row++) {
//				float xU = col * cellW;
//				float yV = row * cellH;
//				float x = -width/2f + col * cellW;
//				float y = -height/2f + row * cellH;
//				float z = 0;
//				PShape line = P.p.createShape();
//				line.beginShape();
//				line.stroke(255);
//				line.strokeWeight(1);
//				line.noFill();
//				line.vertex(x, y, z, xU, yV);
//				line.vertex(x, y + cellH, z, xU, yV);
//				line.endShape();
//				shape.addChild(line);
////
////				sh.normal(x, y, z);
////				sh.vertex(x, y, z, P.map(xU, 0, width, 0, 1), P.map(yV, 0, height, 0, 1));
////				sh.vertex(x, y + cellH, z, P.map(xU, 0, width, 0, 1), P.map(yV + cellH, 0, height, 0, 1));    
////				sh.vertex(x + cellW, y + cellH, z, P.map(xU + cellW, 0, width, 0, 1), P.map(yV + cellH, 0, height, 0, 1));    
////				sh.vertex(x + cellW, y, z, P.map(xU + cellW, 0, width, 0, 1), P.map(yV, 0, height, 0, 1));
////				// numVertices += 4;
//			}
//		}

		
//		PShapeUtil.centerShape(shape);
//		PShapeUtil.scaleShapeToHeight(shape, p.height * 2f);
//		PShapeUtil.addTextureUVToShape(shape, DemoAssets.justin());
//		shape.disableStyle();

		
		// debug view
		p.debugView.setValue("shape.getVertexCount();", shape.getVertexCount());
		p.debugView.setTexture("displaceTexture", displaceTexture.texture());
		
		shaderHotSwap = new PShaderHotSwap(
			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-vert.glsl"),
//			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-lines-vert.glsl"),
			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-frag.glsl") 
		);
	}

	public void drawApp() {
		// context & camera
		p.background(0);
		p.push();
		p.lights();
		//p.image(texture, 0, 0);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);

		// update displacement
		displaceTexture.offsetX(p.frameCount * 0.01f);
		displaceTexture.zoom(0.5f);

		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		shaderHotSwap.shader().set("time", p.frameCount);
		shaderHotSwap.shader().set("displacementMap", displaceTexture.texture());
		shaderHotSwap.shader().set("displaceAmp", 40f);
		shaderHotSwap.shader().set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);

		// apply shader
		p.shader(shaderHotSwap.shader());  

		// draw mesh
		shape.disableStyle();
		p.fill(0, 255, 0);
		p.stroke(255);
		p.shape(shape);
		
		// draw extra shape
//		p.fill(0, 255, 0);
		p.translate(0, 0, 100);
		p.beginShape(P.TRIANGLE);
		p.vertex(0, 0, 0, 0, 0);
		p.vertex(100, 200, 0, 100, 200);
		p.vertex(250, 150, 0, 250, 150);
		p.endShape();
		
		// reset context
		p.resetShader();
		p.pop();
		
		// recompile if needed & show shader compile error messages
		shaderHotSwap.update();
		shaderHotSwap.showShaderStatus(p.g);
	}
		
}