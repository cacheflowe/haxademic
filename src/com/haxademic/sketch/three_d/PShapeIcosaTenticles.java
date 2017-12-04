package com.haxademic.sketch.three_d;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PShape;
import processing.core.PVector;

public class PShapeIcosaTenticles 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected PShape obj;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, 640);
	}
	
	protected void setupFirstFrame() {
		// build obj PShape and scale to window
		obj = Icosahedron.createIcosahedron(p.g, 2, null);
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, p.height * 0.8f);
	}

	public void drawApp() {		
		background(0);
		p.noFill();
		DrawUtil.setBetterLights(p);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, -p.width);

		// draw mesh with texture or without
		obj.disableStyle();
		p.stroke(255);
		p.strokeWeight(0.9f);
		// p.noStroke();
		
		// draw curves
		p.pushMatrix();
		p.rotateY(loop.progressRads());
		drawCurves(obj);
		p.popMatrix();
	}
		
	PVector center = new PVector();
	PVector[][] tentacles = null;
	float segments = 6;
	
	int PLANES = 0;
	int drawMode = 0;

	public void drawCurves(PShape shape) {
		// loop over every 3 vertices
		for (int i = 0; i < shape.getVertexCount() - 2; i+=3) {
			PVector v1 = shape.getVertex(i);
			PVector v2 = shape.getVertex(i+1);
			PVector v3 = shape.getVertex(i+2);
			
			float eqAmp = 1f + p._audioInput.getFFT().spectrum[ i % p._audioInput.getFFT().spectrum.length ];
			p.stroke(255, 255f * (-0.75f + eqAmp));

			// get center
			float centerX = MathUtil.averageOfThree(v1.x, v2.x, v3.x);
			float centerY = MathUtil.averageOfThree(v1.y, v2.y, v3.y);
			float centerZ = MathUtil.averageOfThree(v1.z, v2.z, v3.z);
			center.set(centerX, centerY, centerZ);
			
			// draw emanating points
			float tentLerp = 0.25f;
			float tentAmp = 1.08f + 0.1f * P.sin(i*0.1f + loop.progressRads());
			
			// draw flat planes
			if(drawMode == PLANES) {
				for (int j = 0; j < segments; j++) {
					float tenticleProgress = j / segments;
						
					if(j > 0) {
						v1.mult(tentAmp);
						v2.mult(tentAmp);
						v3.mult(tentAmp);
						
						v1.lerp(center, tentLerp);
						v2.lerp(center, tentLerp);
						v3.lerp(center, tentLerp);
					}
					
					p.beginShape();
					p.vertex(v1.x, v1.y, v1.z);
					p.vertex(v2.x, v2.y, v2.z);
					p.vertex(v3.x, v3.y, v3.z);
					p.vertex(v1.x, v1.y, v1.z);
					p.endShape();
	
	//				p.point(v1.x, v1.y, v1.z);
				}
			} else {
				// lazy-init based on icosa detail and segments config
				if(tentacles == null) tentacles = new PVector[shape.getVertexCount()/3 + 1][(int)segments];
				
				// lazy-init this face's location tracking
				int tenticleIndex = P.floor(i/3);
				if(tentacles[tenticleIndex] == null) {
					tentacles[tenticleIndex] = new PVector[(int)segments];
					for (int j = 0; j < tentacles.length; j++) {
						tentacles[tenticleIndex][j] = new PVector(centerX, centerY, centerZ); 
					}
				}
				
				// draw tenticles
				for (int j = 0; j < segments; j++) {
					float tenticleProgress = j / segments;
						
					if(j > 0) {
						v1.mult(tentAmp);
						v2.mult(tentAmp);
						v3.mult(tentAmp);
						
						v1.lerp(center, tentLerp);
						v2.lerp(center, tentLerp);
						v3.lerp(center, tentLerp);
					}
					
					p.beginShape();
					p.vertex(v1.x, v1.y, v1.z);
					p.vertex(v2.x, v2.y, v2.z);
					p.vertex(v3.x, v3.y, v3.z);
					p.vertex(v1.x, v1.y, v1.z);
					p.endShape();
	
	//				p.point(v1.x, v1.y, v1.z);
				}
			}
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			drawCurves(shape.getChild(j));
		}
	}

}