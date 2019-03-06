package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PShape;
import processing.core.PVector;

public class IcosaTentacles 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected PShape obj;
	protected LinearFloat spikeShrink = new LinearFloat(0, 0.01f);
	protected float easedSpikeShrink = 0;
	protected LinearFloat explodeExtend = new LinearFloat(1, 0.01f);
	protected float easedExplode = 0;

	protected void overridePropsFile() {
		int FRAMES = 640;
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1024);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 2);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}
	
	protected void setupFirstFrame() {
		// build obj PShape and scale to window
		obj = Icosahedron.createIcosahedron(p.g, 2, null);
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, p.height * 0.8f);
	}

	public void drawApp() {		
		background(0);
		p.noFill();
//		p.ortho();
		p.perspective();
//		DrawUtil.setBasicLights(p);
		p.lights();
		DrawUtil.setCenterScreen(p);
		float zFar = -p.width * 1.45f;
		float zNear = -p.width * 0.25f;
		p.translate(0, 0, P.map(easedExplode, 0, 1, zNear, zFar));

		// draw mesh with texture or without
		obj.disableStyle();
		p.stroke(255);
		p.strokeWeight(0.9f);
		
		// toggle spike shrink
		spikeShrink.setTarget(0f);
//		if(p.mousePercentX() > 0.5f) spikeShrink.setTarget(1f);
		if(p.loop.progress() > 0.5f) spikeShrink.setTarget(1f);
		spikeShrink.update();
		easedSpikeShrink = Penner.easeInOutExpo(spikeShrink.value(), 0, 1, 1);
		
		// toggle icosa explosion
		if(p.loop.progress() > 0.2f) explodeExtend.setTarget(1f);
//		if(p.mousePercentX() > 0.5f) spikeShrink.setTarget(1f);
		if(p.loop.progress() > 0.8f) explodeExtend.setTarget(0.01f);
		explodeExtend.update();
		easedExplode = Penner.easeInOutExpo(explodeExtend.value(), 0, 1, 1);

		// draw tentacles
		p.pushMatrix();
//		DrawUtil.basicCameraFromMouse(p.g);
		p.rotateX(0.2f);
		p.rotateY(loop.progressRads());
//		PShapeUtil.meshRotateOnAxis(obj, 0.02f * P.sin(loop.progressRads()), P.Y);
		drawTriangles(obj);
		p.popMatrix();
	}
		
	PVector center = new PVector();
	PVector centerOutward = new PVector();
	PVector centerOffset = new PVector();
	PVector v1Last = new PVector();
	PVector v2Last = new PVector();
	PVector v3Last = new PVector();
	PVector[][] tentacles = null;
	float outSegments = 12;
	float segmentLerpIn = 1f / outSegments;
	
	int PLANES = 0;
	int tentaCLES = 1;
	int drawMode = PLANES;

	
	protected int colorForProgress(float shapeIndex, float progress) {
		shapeIndex /= 4f;
		float darken = P.constrain(progress + 0.04f, 0, 1);
		return p.color(
				darken * (127 + 127 * P.sin(0+ shapeIndex - progress * 3f + p.loop.progressRads() * 5f)),
				darken * (127 + 127 * P.sin(1+ shapeIndex - progress * 3f + p.loop.progressRads() * 5f)),
				darken * (127 + 127 * P.sin(2+ shapeIndex - progress * 3f + p.loop.progressRads() * 5f))
		);
	}
	
	public void drawTriangles(PShape shape) {
		// loop over every 3 vertices
		for (int i = 0; i < shape.getVertexCount() - 2; i+=3) {
			PVector v1 = shape.getVertex(i);
			PVector v2 = shape.getVertex(i+1);
			PVector v3 = shape.getVertex(i+2);
			
			PVector v1Out = shape.getVertex(i);
			PVector v2Out = shape.getVertex(i+1);
			PVector v3Out = shape.getVertex(i+2);
			
			float eqAmp = 1f + p.audioFreq(i);
			p.fill(255, 255f * (-0.75f + eqAmp));
			p.noStroke();

			// get center
			float centerX = MathUtil.averageOfThree(v1.x, v2.x, v3.x);
			float centerY = MathUtil.averageOfThree(v1.y, v2.y, v3.y);
			float centerZ = MathUtil.averageOfThree(v1.z, v2.z, v3.z);
			center.set(centerX, centerY, centerZ);
			centerOutward.set(center);	// new outward center

			// draw emanating points
//			float shrinkLerp = 0.05f;
			float tentLength = 0.2f + 0.1f * P.sin(i*0.1f + loop.progressRads() * 3f);
			float tentSegmentLength = tentLength / outSegments;
			
//			tentLength *= 0;
			tentSegmentLength *= easedExplode;
			
			// draw flat planes
			if(drawMode == PLANES) {
				for (int j = 0; j < outSegments; j++) {					
					// store previous vertices
					v1Last.set(v1);
					v2Last.set(v2);
					v3Last.set(v3);

					// 0-1 from base to tip
					float tentacleProgress = j / (outSegments - 1);
					float tentacleProgressLast = (j - 1) / (outSegments - 1);
					
					// find next outward centerpoint based on original centerpoint
					centerOffset.set(center);
					centerOffset.mult(1f + j * tentSegmentLength);
					centerOffset.sub(center);		// turn into offset to add to vertices

//					centerOutward.mult(j * tentLength);
					centerOutward.add(centerOffset);
					
					// and add to vertices, allowing them to keep their original size, but just move outward
					v1Out.add(centerOffset);
					v2Out.add(centerOffset);
					v3Out.add(centerOffset);
					
					v1.set(v1Out);
					v2.set(v2Out);
					v3.set(v3Out);

					// shrink triangle towards center					
					v1.lerp(centerOutward, tentacleProgress * easedSpikeShrink);
					v2.lerp(centerOutward, tentacleProgress * easedSpikeShrink);
					v3.lerp(centerOutward, tentacleProgress * easedSpikeShrink);
					
					// debug
//					p.pushMatrix();
//					p.translate(centerOutward.x, centerOutward.y, centerOutward.z);	// displace outward
//					p.rect(0, 0, 10, 10);
//					p.popMatrix();
					
					p.fill(colorForProgress(i, tentacleProgress));
					p.beginShape(P.TRIANGLE);
					p.vertex(v1.x, v1.y, v1.z);
					p.vertex(v2.x, v2.y, v2.z);
					p.vertex(v3.x, v3.y, v3.z);
					p.endShape();
					
					// fill sides
					if(j > 0) {
						// draw side 1-2
						p.beginShape();
						p.fill(colorForProgress(i, tentacleProgressLast));
						p.vertex(v1Last.x, v1Last.y, v1Last.z);
						p.fill(colorForProgress(i, tentacleProgress));
						p.vertex(v1.x, v1.y, v1.z);
						p.fill(colorForProgress(i, tentacleProgressLast));
						p.vertex(v2Last.x, v2Last.y, v2Last.z);
						p.endShape();
		
						p.beginShape();
						p.fill(colorForProgress(i, tentacleProgress));
						p.vertex(v1.x, v1.y, v1.z);
						p.vertex(v2.x, v2.y, v2.z);
						p.fill(colorForProgress(i, tentacleProgressLast));
						p.vertex(v2Last.x, v2Last.y, v2Last.z);
						p.endShape();
					
						// draw side 2-3
						p.beginShape();
						p.fill(colorForProgress(i, tentacleProgressLast));
						p.vertex(v2Last.x, v2Last.y, v2Last.z);
						p.fill(colorForProgress(i, tentacleProgress));
						p.vertex(v2.x, v2.y, v2.z);
						p.fill(colorForProgress(i, tentacleProgressLast));
						p.vertex(v3Last.x, v3Last.y, v3Last.z);
						p.endShape();
						
						p.beginShape();
						p.fill(colorForProgress(i, tentacleProgress));
						p.vertex(v2.x, v2.y, v2.z);
						p.vertex(v3.x, v3.y, v3.z);
						p.fill(colorForProgress(i, tentacleProgressLast));
						p.vertex(v3Last.x, v3Last.y, v3Last.z);
						p.endShape();
						
						// draw side 3-1
						p.beginShape();
						p.fill(colorForProgress(i, tentacleProgressLast));
						p.vertex(v3Last.x, v3Last.y, v3Last.z);
						p.fill(colorForProgress(i, tentacleProgress));
						p.vertex(v3.x, v3.y, v3.z);
						p.fill(colorForProgress(i, tentacleProgressLast));
						p.vertex(v1Last.x, v1Last.y, v1Last.z);
						p.endShape();
						
						p.beginShape();
						p.fill(colorForProgress(i, tentacleProgress));
						p.vertex(v3.x, v3.y, v3.z);
						p.vertex(v1.x, v1.y, v1.z);
						p.fill(colorForProgress(i, tentacleProgressLast));
						p.vertex(v1Last.x, v1Last.y, v1Last.z);
						p.endShape();
					}
	
				}
			} else {
				// lazy-init based on icosa detail and segments config
				if(tentacles == null) tentacles = new PVector[shape.getVertexCount()/3 + 1][(int)outSegments];
				
				// lazy-init this face's location tracking
				int tentacleIndex = P.floor(i/3);
				if(tentacles[tentacleIndex] == null) {
					tentacles[tentacleIndex] = new PVector[(int)outSegments];
					for (int j = 0; j < tentacles.length; j++) {
						tentacles[tentacleIndex][j] = new PVector(centerX, centerY, centerZ); 
					}
				}
								
				// draw tentacles
				for (int j = 0; j < outSegments; j++) {
					float tentacleProgress = j / outSegments;
						
					if(j > 0) {
						// store previous
						v1Last.set(v1);
						v2Last.set(v2);
						v3Last.set(v3);

						// move current ring in the stack out
						v1.mult(tentLength);
						v2.mult(tentLength);
						v3.mult(tentLength);

						// re-calc center
						centerX = MathUtil.averageOfThree(v1.x, v2.x, v3.x);
						centerY = MathUtil.averageOfThree(v1.y, v2.y, v3.y);
						centerZ = MathUtil.averageOfThree(v1.z, v2.z, v3.z);
						center.set(centerX, centerY, centerZ);
						
						// lerp towards straight-out target
						tentacles[tentacleIndex][j].lerp(center, 0.2f);
						PVector dist = PVector.sub(tentacles[tentacleIndex][j], center); // use a util PVector
						
						// and shrink circumference
						float shrink = (j == outSegments - 1) ? 1f : 1f; 
						v1.lerp(center, shrink);
						v2.lerp(center, shrink);
						v3.lerp(center, shrink);
					
						// offset
						p.translate(dist.x, dist.y, dist.z);
						
						// draw side 1-2
						p.beginShape();
						p.vertex(v1Last.x, v1Last.y, v1Last.z);
						p.vertex(v1.x, v1.y, v1.z);
						p.vertex(v2Last.x, v2Last.y, v2Last.z);
						p.endShape();
		
						p.beginShape();
						p.vertex(v1.x, v1.y, v1.z);
						p.vertex(v2.x, v2.y, v2.z);
						p.vertex(v2Last.x, v2Last.y, v2Last.z);
						p.endShape();
					
						// draw side 2-3
						p.beginShape();
						p.vertex(v2Last.x, v2Last.y, v2Last.z);
						p.vertex(v2.x, v2.y, v2.z);
						p.vertex(v3Last.x, v3Last.y, v3Last.z);
						p.endShape();
						
						p.beginShape();
						p.vertex(v2.x, v2.y, v2.z);
						p.vertex(v3.x, v3.y, v3.z);
						p.vertex(v3Last.x, v3Last.y, v3Last.z);
						p.endShape();
						
						// draw side 3-1
						p.beginShape();
						p.vertex(v3Last.x, v3Last.y, v3Last.z);
						p.vertex(v3.x, v3.y, v3.z);
						p.vertex(v1Last.x, v1Last.y, v1Last.z);
						p.endShape();
						
						p.beginShape();
						p.vertex(v3.x, v3.y, v3.z);
						p.vertex(v1.x, v1.y, v1.z);
						p.vertex(v1Last.x, v1Last.y, v1Last.z);
						p.endShape();
						
					}
					p.point(v1.x, v1.y, v1.z);
				}
			}
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			drawTriangles(shape.getChild(j));
		}
	}

}