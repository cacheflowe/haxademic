package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.Renderer;

import processing.core.PShape;
import processing.core.PVector;

public class HeartDad 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	int FRAMES = 60 * 15;
	protected PShape heartObj;
	protected PShape heartOrig;
	
	protected LinearFloat rotX = new LinearFloat(0, 0.004f);
	protected LinearFloat rotY = new LinearFloat(0, 0.004f);
	protected HeartFace[] heartFaces;
	protected int colorRedHeart = 0xffcc0000; 

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1920);
		Config.setProperty(AppSettings.HEIGHT, 1536);
		Config.setAppSize(1920/2, 1536/2);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, true);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		// set renderer
		Renderer.instance().videoRenderer.setPG(pg);
		
		// load heart model
		String objFile = FileUtil.getPath("models/heart/heart/Heart.obj");
//		objFile = FileUtil.getPath("models/heart/heart2/model.obj");
		objFile = FileUtil.getPath("models/heart/heart3/Love.obj");
		boolean tessellated = true;
		if(tessellated) {
			heartObj = p.loadShape(objFile).getTessellation();
			heartOrig = p.loadShape(objFile).getTessellation();
//			PShapeUtil.repairMissingSVGVertex(shape);
		} else {
			heartObj = p.loadShape(objFile);
			heartOrig = p.loadShape(objFile);
		}
		PShapeUtil.centerShape(heartObj);
		PShapeUtil.centerShape(heartOrig);
		PShapeUtil.meshRotateOnAxis(heartObj, P.PI, P.Z);
		PShapeUtil.meshRotateOnAxis(heartOrig, P.PI, P.Z);
		PShapeUtil.meshRotateOnAxis(heartObj, P.PI, P.Y);
		PShapeUtil.meshRotateOnAxis(heartOrig, P.PI, P.Y);
		float modelSize = p.height * 0.7f;
		PShapeUtil.scaleShapeToHeight(heartObj, modelSize);
		PShapeUtil.scaleShapeToHeight(heartOrig, modelSize);
		
		// get centers of each face
		// if tessellated
		if(!tessellated) {
			heartFaces = new HeartFace[heartObj.getChildCount()];
			for (int i = 0; i < heartObj.getChildren().length; i++ ) {
				PShape child = heartObj.getChild(i);
				PShape childOrig = heartOrig.getChild(i);
				heartFaces[i] = new HeartFace(i, 0xffcc0000, child, childOrig);
			}
		} else {
			PShape shapeGroup = p.createShape(P.GROUP);
			PShape shapeOrigGroup = p.createShape(P.GROUP);
			int numTriangles = heartObj.getVertexCount()/3;
			heartFaces = new HeartFace[numTriangles];
			
			for (int i = 0; i < heartObj.getVertexCount(); i+=3) {
				PShape origShape = p.createShape();
				origShape.beginShape();
				origShape.fill(colorRedHeart);
				origShape.noStroke();
				origShape.vertex(heartObj.getVertex(i).x, heartObj.getVertex(i).y, heartObj.getVertex(i).z);
				origShape.vertex(heartObj.getVertex(i+1).x, heartObj.getVertex(i+1).y, heartObj.getVertex(i+1).z);
				origShape.vertex(heartObj.getVertex(i+2).x, heartObj.getVertex(i+2).y, heartObj.getVertex(i+2).z);
				origShape.endShape();
				shapeOrigGroup.addChild(origShape);

				PShape newShape = p.createShape();
				newShape.beginShape();
				newShape.fill(colorRedHeart);
				newShape.noStroke();
				newShape.vertex(heartObj.getVertex(i).x, heartObj.getVertex(i).y, heartObj.getVertex(i).z);
				newShape.vertex(heartObj.getVertex(i+1).x, heartObj.getVertex(i+1).y, heartObj.getVertex(i+1).z);
				newShape.vertex(heartObj.getVertex(i+2).x, heartObj.getVertex(i+2).y, heartObj.getVertex(i+2).z);
//				if(i < vertexCount - 2) {	// protect against rogue vertices?
				newShape.endShape();
				shapeGroup.addChild(newShape);
				heartFaces[i/3] = new HeartFace(i, colorRedHeart, newShape, origShape);
			}

			// swap groups of tessellated triangle shapes with original loaded model
			heartObj = shapeGroup;
			heartOrig = shapeOrigGroup;
			heartObj.disableStyle(); 
			heartOrig.disableStyle(); 
		}
		DebugView.setValue("shape.getVertexCount();", PShapeUtil.vertexCount(heartObj));
	}

	protected void drawApp() {

		// set up context
		pg.beginDraw();
		pg.background(15, 0, 5);
		pg.push();
		pg.perspective();
//		pg.ortho();
				
		// set context & camera
		PG.setCenterScreen(pg);
		pg.translate(0, -pg.height * 0.2f, -pg.width * 1.0f);
//		pg.lights();
		PG.setBetterLights(pg);
		
		// camera/rotation
//		PG.basicCameraFromMouse(pg);
		rotX.update(true);
		rotY.update(true);
		float easedRotX = Penner.easeInOutCubic(rotX.value());
		float easedRotY = Penner.easeInOutCubic(rotY.value());
		pg.rotateX(-0.35f + P.sin(FrameLoop.progressRads()) * 0.125f);
//		pg.rotateY(P.PI * easedRotY);
		pg.rotateY(FrameLoop.progressRads());
		
		// update heart faces
		for (int i = 0; i < heartFaces.length; i++) {
			heartFaces[i].update();
		}

		// draw large heart mesh
		pg.fill(255, 0, 0);
		pg.shape(heartObj);

		// prep context for heart rings 
		heartOrig.disableStyle();
		fill(colorRedHeart);
		if(FrameLoop.loopCurFrame() > 400) pg.shape(heartOrig);
		
		// draw heart rings
		float segmentRads8 = P.TWO_PI / 8f;
		float radius8 = p.width / 1.75f;
		float ring8FramesProg = P.constrain(P.map(FrameLoop.loopCurFrame(), 250, 400, 0, 1), 0, 1);
		if(FrameLoop.loopCurFrame() > 500) ring8FramesProg = 0;
		if(FrameLoop.loopCurFrame() < 250) ring8FramesProg = 0;
		float easedProgress = Penner.easeInOutQuart(ring8FramesProg);
		for (int i = 0; i < 8; i++) {
			float curRads = i * segmentRads8;
			float x = P.cos(curRads) * radius8;
			float z = P.sin(curRads) * radius8;
			pg.push();
			pg.translate(P.lerp(x, 0, easedProgress), P.lerp(50, 0, easedProgress), P.lerp(z, 0, easedProgress));
			float startRot = P.HALF_PI - curRads;
			float endRot = (curRads > PI) ? -P.TWO_PI : 0;
			pg.rotateY(P.lerp(startRot, endRot, easedProgress));
			pg.scale(P.lerp(0.4f, 1f, easedProgress));
			pg.shape(heartOrig);
			pg.pop();
		}
		
		float segmentRads16 = P.TWO_PI / 16f;
		float radius16 = p.width / 1.25f;
		float ring16FramesScaleProg = P.constrain(P.map(FrameLoop.loopCurFrame(), 600, 700, 0, 1), 0, 1);
//		if(FrameLoop.loopCurFrame() > 700) ring16FramesScaleProg = P.constrain(P.map(FrameLoop.loopCurFrame(), 700, 800, 0, 1), 0, 1);
		if(FrameLoop.loopCurFrame() < 500) ring16FramesScaleProg = 1;
		float ring16FramesScaleProgEased = Penner.easeOutCirc(ring16FramesScaleProg);
		float ring16FramesMoveProg = P.constrain(P.map(FrameLoop.loopCurFrame(), 400, 500, 0, 1), 0, 1);
		if(FrameLoop.loopCurFrame() < 400) ring16FramesMoveProg = 0;
		if(FrameLoop.loopCurFrame() > 500) ring16FramesMoveProg = 0;
		easedProgress = Penner.easeInOutQuart(ring16FramesMoveProg);
		for (int i = 0; i < 16; i++) {
			float curRads = i * segmentRads16;
			float curRadsHalf = P.floor(i/2) * segmentRads8;
			float x = P.cos(curRads) * radius16;
			float z = P.sin(curRads) * radius16;
			float xEnd = P.cos(curRadsHalf) * radius8;
			float zEnd = P.sin(curRadsHalf) * radius8;
			pg.push();
			pg.translate(P.lerp(x, xEnd, easedProgress), P.lerp(90, 50, easedProgress), P.lerp(z, zEnd, easedProgress));
			float startRot = P.HALF_PI - curRads;
			float endRot = P.HALF_PI - curRadsHalf;
			pg.rotateY(P.lerp(startRot, endRot, easedProgress));
			pg.scale(P.lerp(ring16FramesScaleProgEased * 0.2f, 0.4f, easedProgress));
			pg.shape(heartOrig);
			pg.pop();
		}

		// end drawing on main buffer
		pg.pop();
		pg.endDraw();
		
		// post process
		BloomFilter.instance(p).setStrength(0.05f);
		BloomFilter.instance(p).setBlurIterations(2);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
//		BloomFilter.instance(p).applyTo(pg);
		BloomFilter.instance(p).applyTo(pg);
		
		GodRays.instance(p).setDecay(0.8f);
		GodRays.instance(p).setWeight(0.2f);
		GodRays.instance(p).setRotation(1.9f + 0.2f * P.sin(FrameLoop.progressRads() * 3f));
		GodRays.instance(p).setAmp(0.18f);
		GodRays.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.9f);
		VignetteFilter.instance(p).applyTo(pg);

		GrainFilter.instance(p).setTime(p.frameCount * 0.02f);
		GrainFilter.instance(p).setCrossfade(0.02f);
		GrainFilter.instance(p).applyTo(pg);
		
		// draw to screeen
		p.image(pg, 0, 0);
		if(FrameLoop.loopCurFrame() == FRAMES-2) for (int i = 0; i < heartFaces.length; i++) heartFaces[i].reset();
//		if(FrameLoop.loopCurFrame() % 100 == 0) for (int i = 0; i < heartFaces.length; i++) heartFaces[i].heartComplete();
		if(FrameLoop.loopCurFrame() == 360) for (int i = 0; i < heartFaces.length; i++) heartFaces[i].heartComplete();
	}
	
	class HeartFace {
		
		protected int index; 
		protected EasingColor faceColor;
		protected PShape shape;
		protected PShape shapeOrig;
		protected float gravity = -0.035f;
		protected float floatSpeed = 0f;
		protected float floatY = 0f;
		protected float floatOutSpeed = 1f;
		protected float floatOut = 0f;
		protected float floatOutRads = 0f;
		protected float alpha = 255f;
		protected float alphaStep = 1.8f;
		protected PVector rot = new PVector();
		protected PVector faceCenter;
		protected PVector faceCenterOrig;
		protected boolean floatMode = false;
		protected int frameOffset; 
		protected int startFloatFrame = -1; 
		
		protected PVector particle = new PVector();
		protected PVector particleDirection = new PVector();
		protected float particleSpeed = 1;
		protected float particleFriction = 0.99f;
		protected float particleAlpha = 0;
		protected float particleAlphaFade = -2.5f;
		
		public HeartFace(int index, int faceColor, PShape child, PShape childOrig) {
			this.index = index;
			this.faceColor = new EasingColor(faceColor);
			this.shape = child;
			this.shapeOrig = childOrig;
			
			faceCenter = MathUtil.computeTriangleCenter(child.getVertex(0), child.getVertex(1), child.getVertex(2)).copy();
			faceCenterOrig = faceCenter.copy();
			particle = faceCenterOrig.copy();
			particleDirection = particle.copy().normalize();
			this.frameOffset = (int) P.map(faceCenter.y, -p.height/2f, p.height/2f, 0, 140); 
			gravity = P.map(faceCenter.y, -p.height/2f, p.height/2f, -0.035f, 0.010f);
			frameOffset += MathUtil.randRange(0, 50);
			floatOutRads = MathUtil.getRadiansToTarget(0, 0, faceCenter.x, faceCenter.z);
			
			this.storeColors();
			
			// test rand gravty
			gravity *= MathUtil.randRangeDecimal(0.7f, 1.3f);
		}
		
		public void heartComplete() {
			particle = faceCenterOrig.copy();
			particleDirection = particle.copy().normalize();
			particleSpeed = MathUtil.randRangeDecimal(5, 7);
			particleFriction = MathUtil.randRangeDecimal(0.975f, 0.99f);
			particleAlpha = 255;
			particleAlphaFade = MathUtil.randRangeDecimal(1.5f, 3.5f);
		}
		
		public void reset() {
			floatMode = false;
			floatY = 0;
			floatSpeed = 0f;
			alpha = 255f;
			faceColor.setTargetInt(colorRedHeart);
			faceColor.setCurrentInt(colorRedHeart);	// was just alpha channel before
			floatOut = 0;
			faceCenter.set(faceCenterOrig);
			startFloatFrame = p.frameCount;
			

			// reset big heart vertices to original positions
			for(int i = 0; i < this.shape.getVertexCount(); i++) {
				PVector vertexOrig = this.shapeOrig.getVertex(i);
				this.shape.setVertex(
					i, 
					vertexOrig.x, 
					vertexOrig.y, 
					vertexOrig.z
				);
			}			
		}
		
		public void storeColors() {
			// shift colors
			this.faceColor.setTargetR(this.faceColor.r() * MathUtil.randRangeDecimal(0.9f, 1.1f));
			this.faceColor.setTargetG(this.faceColor.g() * MathUtil.randRangeDecimal(0.9f, 1.1f));
			this.faceColor.setTargetB(this.faceColor.b() * MathUtil.randRangeDecimal(0.9f, 1.1f));
		}
		
		public void update() {
			// update vertices
			if(frameCount == startFloatFrame + frameOffset) {
				floatMode = true;
			}
			
//			noiseVerts();
			if(floatMode) floatVerts();
			
			// update color
			faceColor.update();
			this.shape.setFill(faceColor.colorInt());
			
			// update particles
			if(particleAlpha > 0) {
				particleAlpha -= particleAlphaFade;
				particleSpeed *= particleFriction;
				particle.add(particleDirection.x * particleSpeed, particleDirection.y * particleSpeed, particleDirection.z * particleSpeed);
				pg.push();
				pg.translate(particle.x, particle.y, particle.z);
				pg.fill(255, particleAlpha);
				pg.sphere(P.map(particleAlpha, 255, 0, pg.height * 0.0045f, 0));
				pg.pop();
			}
		}
		
		protected void floatVerts() {
			// apply physics & fade away
			floatSpeed += gravity;
			floatY += floatSpeed;
			alpha -= alphaStep;
//			faceColor.setTargetA(alpha);
			// float outward
			floatOut += floatOutSpeed;
			float offsetX = P.cos(floatOutRads) * floatOut;
			float offsetZ = P.sin(floatOutRads) * floatOut * 2f;
			float progAlpha = P.min(P.map(alpha, 255, 0, 0, 1), 1);
			// update vertices
			for(int i = 0; i < shape.getVertexCount(); i++) {
				PVector vertexOrig = shapeOrig.getVertex(i);
				// set vertices of manipulated object
				shape.setVertex(
					i, 
					lerp(vertexOrig.x + offsetX, faceCenter.x, progAlpha),
					lerp(vertexOrig.y + floatY, faceCenter.y, progAlpha), 
					lerp(vertexOrig.z + offsetZ, faceCenter.z, progAlpha)
				);
//				this.shape.rotateY(FrameLoop.count(0.0001f));
			}
			// update centroid while floating
			faceCenter.set(MathUtil.computeTriangleCenter(shape.getVertex(0), shape.getVertex(1), shape.getVertex(2)));
		}
		
		protected void noiseVerts() {
			for(int vIndex = 0; vIndex < this.shape.getVertexCount(); vIndex++) {
				PVector vertexOrig = this.shapeOrig.getVertex(vIndex);

				// get distance to sweeping x coord
				float noiseForVert = 0;
				float noiseScale = 0.1f;
				noiseForVert = 0.2f * P.sin(vertexOrig.x/10f + FrameLoop.progressRads() * 3f);
//				noiseForVert = p.noise(
//					vertexOrig.x / noiseScale + FrameLoop.count(0.004f), 
//					vertexOrig.y / noiseScale + FrameLoop.count(0.004f), 
//					vertexOrig.z / noiseScale + FrameLoop.count(0.004f)
//				);

				// set vertices of manipulated object
				shape.setVertex(
					vIndex, 
					vertexOrig.x * (1f + 0.5f * noiseForVert), 
					vertexOrig.y * (1f + 0.5f * noiseForVert), 
					vertexOrig.z * (1f + 0.5f * noiseForVert)
				);
			}
		}
	}
		
}