package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PShader;

public class JOY_01 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage sprite;
	protected PVector modelSize;
	protected boolean overrideColor = false;
	protected PShader fattenerVertShader;
	
	protected PVector rotation = new PVector();
	protected PVector position = new PVector();
	protected int targetIndex = 0;
	
	protected PVector[] posTargets = new PVector[] {
			new PVector(78.0f, 10.0f, 232.0f),
			new PVector(-86.0f, 64.0f, 262.0f),
			new PVector(-126.0f, -92.0f, 296.0f),
			new PVector(142.0f, 36.0f, 438.0f),
			new PVector(126.0f, -96.0f, 350.0f),
	};
	
	protected PVector[] rotTargets = new PVector[] {
			new PVector(-1.459999f, -2.8799977f, -0.68999964f),
			new PVector(-2.689998f, -2.2199984f, -0.67999965f),
			new PVector(-3.0299976f, 0.35999978f, 2.3199983f),
			new PVector(-3.3799973f, -0.27000022f, 7.9800873f % P.TWO_PI),
			new PVector(-3.2099974f, -3.3399973f, 11.260162f % P.TWO_PI),
	};
	
	/*
	pos: 78.0 10.0 232.0
	rot: -1.459999 -2.8799977 -0.68999964
	pos: -86.0 64.0 262.0
	rot: -2.689998 -2.2199984 -0.67999965
	pos: -126.0 -92.0 296.0
	rot: -3.0299976 0.35999978 2.3199983
	pos: 142.0 36.0 438.0
	rot: -3.3799973 -0.27000022 7.9800873
	pos: 126.0 -96.0 350.0
	rot: -3.2099974 -3.3399973 11.260162
	*/


	protected void overridePropsFile() {
		int FRAMES = 120;
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 768);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
	}
	
	protected void setupFirstFrame() {
		// build shape and assign texture
		shape = p.loadShape(FileUtil.getFile("models/joy-hoop/joy_hoop.obj"));
//		shape = DemoAssets.objSkullRealistic();
		shape = PShapeUtil.meshShapeToPointsShape(shape);
		
		// normalize shape (scaling centers)
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.5f);
//		PShapeUtil.setOnGround(shape);
//		PShapeUtil.setRegistrationOffset(shape, 0, -0.5f, 0);
//		PShapeUtil.scaleShapeToWidth(shape, p.width * 0.4f);
//		PShapeUtil.scaleShapeToDepth(shape, p.width * 0.4f);
//		PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Z);
		
		// check size
		modelSize = new PVector(PShapeUtil.getWidth(shape), PShapeUtil.getHeight(shape), PShapeUtil.getDepth(shape));
		DebugView.setValue("shape.width", modelSize.x);
		DebugView.setValue("shape.height", modelSize.y);
		DebugView.setValue("shape.depth", modelSize.z);
		DebugView.setValue("shape vertices", PShapeUtil.vertexCount(shape));
		
		// load shader
		fattenerVertShader = p.loadShader(
			FileUtil.getFile("haxademic/shaders/vertex/joy-displacer-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/vertex/joy-displacer-vert.glsl")
		);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'p') {
			targetIndex++;
			if(targetIndex == rotTargets.length) targetIndex = 0;
		}
		if(p.key == ' ') {
			P.println("pos:", position.x, position.y, position.z);
			P.println("rot:", rotation.x, rotation.y, rotation.z);
		}
	}
		
	public void drawApp() {
		// clear the screen
		background(0);
		p.noStroke();
//		PG.setBetterLights(p.g);
		
		// keylistening
		float moveInc = 2;
		float rotInc = 0.01f;
		if(p.keyPressed == true) {
			if(p.key == 'q') position.x -= moveInc;
			if(p.key == 'w') position.x += moveInc;
			if(p.key == 'a') position.y -= moveInc;
			if(p.key == 's') position.y += moveInc;
			if(p.key == 'z') position.z -= moveInc;
			if(p.key == 'x') position.z += moveInc;
			if(p.key == 't') rotation.x -= rotInc;
			if(p.key == 'y') rotation.x += rotInc;
			if(p.key == 'g') rotation.y -= rotInc;
			if(p.key == 'h') rotation.y += rotInc;
			if(p.key == 'b') rotation.z -= rotInc;
			if(p.key == 'n') rotation.z += rotInc;
		}
		
		// lerp toward targets
		position.lerp(posTargets[targetIndex], 0.02f);
		rotation.lerp(rotTargets[targetIndex], 0.02f);


		// rotate camera
		p.translate(p.width/2 + position.x, p.height/2 + position.y, position.z);
		p.rotateX(rotation.x);
		p.rotateY(rotation.y);
		p.rotateZ(rotation.z);
//		p.rotateY(loop.progressRads());
		
		// apply shader
		fattenerVertShader.set("time", loop.progressRads() * 2f);
		fattenerVertShader.set("amp", p.height * 0.15f);
//		p.shader(fattenerVertShader);  

		// draw shape
		PG.setDrawCorner(p.g);
		p.shape(shape);
		
		p.resetShader();
	}
	
}