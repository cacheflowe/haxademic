package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.MarchingCubes;
import com.haxademic.core.hardware.mouse.Mouse;

import processing.core.PVector;

public class Demo_MarchingCubes 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	MarchingCubes mc;
	Boolean bUseFill;
	
	protected void firstFrame() {
		float extent = 350;
		PVector aabbMin = new PVector(-extent, -extent, -extent);
		PVector aabbMax = new PVector(extent, extent, extent);
		float mcResolution = 50;
		PVector numPoints = new PVector(mcResolution, mcResolution, mcResolution);
		float isoLevel = 2;
		mc = new MarchingCubes(this, aabbMin, aabbMax, numPoints, isoLevel);

		bUseFill = false;
	}

	protected void drawApp() {
		// set content
		p.background(0);
		pushMatrix();
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);
		PG.setBetterLights(p.g);
		
		// generate metaballs balls
		mc.reset();
		for (int i = 0; i < 15; i++) {
			// add data to marching cubes metaballs algorithm
			PVector metaBallPos = new PVector(
					150 * P.sin(i + frameCount * 0.05f * (i+1)/10f),
					150 * P.sin(i + frameCount * 0.04f * (i+1)/10f),
					150 * P.sin(i + frameCount * 0.03f)
					);
			float size = 10 + i * 5;
			mc.addMetaBall(metaBallPos, size * 2.5f, 2 + 30f * Mouse.xNorm);
			
			// draw source balls
			p.pushMatrix();
			p.translate(metaBallPos.x, metaBallPos.y, metaBallPos.z);
			p.sphere(size);
			p.popMatrix();
		}
		
		// create mesh
		mc.createMesh();
		if(bUseFill){
			fill(0,255,0);
			noStroke();
		}
		else {
			noFill();
			stroke(127);
		}
		
		mc.renderMesh();
//		mc.renderGrid();
		popMatrix();
	}
	
	public void keyPressed(){
		super.keyPressed();
		if(key == ' ') bUseFill = !bUseFill;
	}
}
