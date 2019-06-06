package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.VignetteAltFilter;

public class BlindSpot
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float numSegments = 10f;
	protected float numShapes = 500f;
	protected float frames = 200f;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, Math.round(frames) );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(frames * 2) );
	}


	public void setup() {
		super.setup();	
	}

	public void drawApp() {
		if(p.frameCount == 1) p.background(255);

		p.blendMode(P.BLEND);
		DrawUtil.feedback(p.g, p.color(255), 0.15f, 1f);
		
		DrawUtil.setDrawFlat2d(p, true);
		
		p.blendMode(P.SUBTRACT);
		p.noFill();
		p.noStroke();
		p.strokeWeight(0.85f);
		
		float progress = (p.frameCount % frames) / frames;
		float progressRadians = progress * P.TWO_PI;
		
		int innerColor = p.color(255, 10);
		int outerColor = p.color(255, 0, 255, 00);
		
		p.pushMatrix();

		float halfW = width/2;
		float halfH = height/2;
		float baseRadius = halfH/2.3f;

		float segmentRadians = P.TWO_PI / numSegments;
		float shapeRadians = P.TWO_PI / numShapes;
		
		
		p.translate(halfW, halfH);
		p.rotateY(progressRadians);
//		p.rotateX(progressRadians);
//		p.rotateZ(-progressRadians);
		
		/*
		DrawUtil.setDrawCenter(p);
		boolean drawEllipses = false;
		if(drawEllipses == true) {
			for (int i = 0; i < numShapes; i++) {
				float size = i * p.width / 2f / numShapes;
				p.stroke(0, 100);
				p.ellipse(0, 0, size, size);
		//		p.sphere(100);
				p.rotateY(shapeRadians);
			}
		}
		*/
		
		p.pushMatrix();
		for (float i = 0; i < numShapes; i++) {
//			p.rotateY(shapeRadians);
//			p.rotateX(shapeRadians);
//			p.rotateZ(shapeRadians);
			p.rotateY(1f + p.noise(i, i * 0.25f, i * 0.65f));
			p.rotateX(1f + p.noise(i * 0.25f, i, i * 0.65f));
			p.rotateZ(1f + p.noise(i * 0.65f, i, i * 0.25f));
			outerColor = p.color(
				100 + 40f * sin(i), 
				120 + 50f * sin(i), 
				160 + 90f * sin(i), 
				10
			);
			
			float radiusOscillations = 5f + 3f * P.sin(i/3f);
			for(float r=0; r < P.TWO_PI; r += segmentRadians) {
				float r2 = r + segmentRadians;
				p.beginShape();
				p.stroke(innerColor);
//				p.fill(innerColor);
				p.vertex(0,0);
				p.stroke(outerColor);
//				p.fill(outerColor);
				float curRadius = baseRadius + (baseRadius * 0.6f * P.sin(progressRadians + i+r*radiusOscillations));
				float nextRadius = baseRadius + (baseRadius * 0.6f * P.sin(progressRadians + i+r2*radiusOscillations));
				p.vertex(P.sin(r) * curRadius, P.cos(r) * curRadius);
				p.vertex(P.sin(r2) * nextRadius, P.cos(r2) * nextRadius);
				p.endShape(P.CLOSE);
			}
		}
		p.popMatrix();
		

		p.popMatrix();
		
		// post process
//		ColorDistortionFilter.instance(p).applyTo(p);
//		SphereDistortionFilter.instance(p).setAmplitude(0.5f);
//		SphereDistortionFilter.instance(p).applyTo(p);
//		DilateFilter.instance(p).applyTo(p);
//		CubicLensDistortionFilter.instance(p).setTime( 10f + 3f * P.sin(progressRadians));
//		CubicLensDistortionFilter.instance(p).applyTo(p);
		VignetteAltFilter.instance(p).setDarkness(-4);
		VignetteAltFilter.instance(p).setSpread(0.2f);
		VignetteAltFilter.instance(p).applyTo(p);
	}

}