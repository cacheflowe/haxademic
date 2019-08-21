package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;

public class Demo_DeformMesh2D 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float cols = 5;
	protected float rows = 5;

	protected PShaderHotSwap polygonShader;
	protected PShaderHotSwap linesShader;
	
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
		
		polygonShader = new PShaderHotSwap(
			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-vert.glsl"),
			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-frag.glsl") 
		);
		linesShader = new PShaderHotSwap(
			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-lines-vert.glsl"),
			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-frag.glsl") 
		);
	}

	public void drawApp() {
		// context & camera
		p.background(0);
		p.push();
		p.lights();
		PG.setCenterScreen(p.g);
		// PG.basicCameraFromMouse(p.g);

		// update displacement
		displaceTexture.offsetX(p.frameCount * 0.01f);
		displaceTexture.zoom(2.5f);

		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		polygonShader.shader().set("time", p.frameCount);
		polygonShader.shader().set("displacementMap", displaceTexture.texture());
		polygonShader.shader().set("displaceAmp", 40f);
		polygonShader.shader().set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);

		// apply polygons shader
		p.shader(polygonShader.shader());  

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
		p.vertex(50, 100, 0, 50, 100);
		p.vertex(150, 80, 0, 150, 80);
		p.endShape();

		// end polygon shader
		p.resetShader();
		
		// apply lines shader
		linesShader.shader().set("time", p.frameCount);
		linesShader.shader().set("displacementMap", displaceTexture.texture());
		linesShader.shader().set("displaceAmp", 40f);
		linesShader.shader().set("weight", 1f);
		linesShader.shader().set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);
		p.shader(linesShader.shader());  

		// start lines shader
		// draw wave
		p.stroke(255, 0, 0);
		p.noFill();
//		p.fill(255);
		p.strokeWeight(10);
		p.beginShape();
		for (int i = 0; i < 10; i++) {
			float xx = i * 10f;
			float yy = P.sin(i/4f) * 100f;
			p.vertex(-50 + xx, yy, 0, 0.05f + (float)i / 11f, 0.5f);// + 0.5f * P.sin((float)i/4f)); // xx*2f/(float)p.width, yy*2f/(float)p.height);
//			P.out((float)i / 10f, 0.5f + 0.15f * P.sin(i/4f));
		}
		p.endShape();
		
		// draw box
		p.beginShape();
		p.vertex(-100, -100, 0, 0.1f, 0.1f);
		p.vertex( 100, -100, 0, 0.9f, 0.1f);
		p.vertex( 100,  100, 0, 0.9f, 0.9f);
		p.vertex(-100,  100, 0, 0.1f, 0.9f);
		p.vertex(-100, -100, 0, 0.1f, 0.1f);
		p.endShape();
		
		p.beginShape();
		p.vertex(-80, -80, 0, 0.2f, 0.2f);
		p.vertex( 80, -80, 0, 0.8f, 0.2f);
		p.vertex( 80,  80, 0, 0.8f, 0.8f);
		p.vertex(-80,  80, 0, 0.2f, 0.8f);
		p.vertex(-80, -80, 0, 0.2f, 0.2f);
		p.endShape();
		
		p.beginShape();
		p.vertex(-50, -50, 0, 0.3f, 0.3f);
		p.vertex( 50, -50, 0, 0.7f, 0.3f);
		p.vertex( 50,  50, 0, 0.7f, 0.7f);
		p.vertex(-50,  50, 0, 0.3f, 0.7f);
		p.vertex(-50, -50, 0, 0.3f, 0.3f);
		p.endShape();

//		
//		// reset context
		p.resetShader();
		p.pop();
		
		// recompile if needed & show shader compile error messages
		polygonShader.update();
		polygonShader.showShaderStatus(p.g);
		linesShader.update();
		linesShader.showShaderStatus(p.g);
	}
		
}