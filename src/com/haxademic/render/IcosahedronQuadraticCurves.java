package com.haxademic.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PShape;
import processing.core.PVector;

public class IcosahedronQuadraticCurves 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected PShape obj;

	protected void overridePropsFile() {
		int FRAMES = 640;
		p.appConfig.setProperty(AppSettings.WIDTH, 1280);
		p.appConfig.setProperty(AppSettings.HEIGHT, 720);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void setupFirstFrame() {
		AudioIn.instance();
		// build obj PShape and scale to window
		obj = Icosahedron.createIcosahedron(p.g, 3, null);
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, p.height * 0.8f);
	}

	public void drawApp() {		
		background(0);
		p.noFill();
//		PG.setBetterLights(p);
		
		// rotate
		float z = Penner.easeInOutExpo(0.5f + 0.5f * MathUtil.saw(loop.progressRads()), 0, 1, 1);
		p.translate(p.width/2f, p.height/2f, 100 + z * -p.width); // -p.width

		// draw mesh with texture or without
		p.stroke(255);
		p.strokeWeight(0.8f);
		
		// draw curves
		p.pushMatrix();
		p.rotateY(loop.progressRads());
		drawCurves(obj);
		p.popMatrix();
	}
		
	
	public void drawCurves(PShape shape) {
		for (int i = 0; i < shape.getVertexCount() - 2; i+=3) {
			PVector v1 = shape.getVertex(i);
			PVector v2 = shape.getVertex(i+1);
			PVector v3 = shape.getVertex(i+2);
			
			float eqAmp = 1f + AudioIn.audioFreq(i);
			p.stroke(255f * (-0.75f + eqAmp));
			
			// override for render
			// p.blendMode(PBlendModes.ADD);
			eqAmp = 1;
			p.stroke(185);

			p.beginShape();
			p.vertex(v1.x, v1.y, v1.z);
			p.quadraticVertex(v2.x * eqAmp, v2.y * eqAmp, v2.z * eqAmp, v3.x, v3.y, v3.z);
			p.endShape();

			p.beginShape();
			p.vertex(v2.x, v2.y, v2.z);
			p.quadraticVertex(v1.x * eqAmp, v1.y * eqAmp, v1.z * eqAmp, v3.x, v3.y, v3.z);
			p.endShape();
			
			p.beginShape();
			p.vertex(v1.x, v1.y, v1.z);
			p.quadraticVertex(v3.x * eqAmp, v3.y * eqAmp, v3.z * eqAmp, v2.x, v2.y, v2.z);
			p.endShape();
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			drawCurves(shape.getChild(j));
		}
	}

}