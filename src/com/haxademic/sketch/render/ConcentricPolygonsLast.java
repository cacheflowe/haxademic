package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PApplet;
import processing.core.PGraphics;

public class ConcentricPolygonsLast 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ImageGradient imageGradient;
	protected boolean shouldRecord = false;
	protected float iterateShrink;
	protected float lineWeight;
	
	protected void overridePropsFile() {
		int FRAMES = 140;
//		p.appConfig.setProperty( AppSettings.RENDERER, PRenderers.PDF );
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES );
	}
	
	public void setupFirstFrame() {
		if(PRenderers.currentRenderer() == PRenderers.PDF) shouldRecord = true;
		imageGradient = new ImageGradient(ImageGradient.BLACK_HOLE());
	}

	public void drawApp() {
		p.background(0);
		preparePDFRender();
		p.noStroke();
		
		// set up concentric polygon config
		float radius = p.height * 0.4f; 
		float spacing = p.height * 0.03f; 
		lineWeight = radius * P.map(p.mouseY, 0, p.height, 0.01f, 0.05f);
		float vertices = P.round(5.5f + 2.5f * P.sin(loop.progressRads()));
		vertices = 6f;
		
		// rotate polygon to sit on a flat bottom
		p.translate(p.width/2, p.height * 0.5f);
		// offset y
		float radiusClosest = MathUtil.polygonClosestPoint(vertices, radius);
		float radiusDiff = radius - radiusClosest;
//		p.translate(0, radiusDiff);
		// rotate
		float segmentRads = P.TWO_PI / vertices;
		p.rotate(P.HALF_PI);
		p.rotate(segmentRads / 2f);
		
		// draw concentric shapes
//		while(radius < 1000 && lineWeight > 5) {
//			p.pushMatrix();
//			p.fill(255);
//			drawDisc(p, radius, radius - lineWeight, (int) vertices);  // Shapes.
//			radius += lineWeight + spacing;
//			lineWeight *= 0.85f;
//			p.popMatrix();
//		}
		
		// draw fractal
		iterateShrink = P.map(p.mouseX, 0, p.width, 0.2f, 1f);
		p.pushMatrix();
		p.fill(255);
		drawDisc(p, radius, radius - lineWeight, (int) vertices, 0);  // Shapes.
		p.popMatrix();
		
		
		finishPDFRender();
	}
	
	public void drawDisc( PApplet p, float radius, float innerRadius, int numSegments, int level ) {
		p.pushMatrix();
		
		float nextRadius = radius * iterateShrink;
		float nextInnerRadius = innerRadius * iterateShrink;
		nextInnerRadius = nextRadius - lineWeight;
		
		float segmentRads = P.TWO_PI / numSegments;
		for( int i = 0; i < numSegments; i++ ) {
			p.beginShape(P.TRIANGLES);
			
			p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius );
			p.vertex( P.cos( i * segmentRads ) * radius, P.sin( i * segmentRads ) * radius );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius );
			
			p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius );
			p.vertex( P.cos( (i + 1) * segmentRads ) * innerRadius, P.sin( (i + 1) * segmentRads ) * innerRadius );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius );
			p.endShape();
			
			if(level < 3) {
				p.pushMatrix();
				float x = P.cos( i * segmentRads ) * (radius - ((radius - innerRadius) / 2f));
				float y = P.sin( i * segmentRads ) * (radius - ((radius - innerRadius) / 2f));
				p.translate(x, y);
				drawDisc(p, nextRadius, nextInnerRadius, numSegments, level + 1);
				p.popMatrix();
			}
		}
		
		p.popMatrix();
	}

	
	
	protected void preparePDFRender() {
		if(shouldRecord == true) {
//			p.beginRecord(P.PDF, FileUtil.getHaxademicOutputPath() + "/pdf/frame-####.pdf");
			p.beginRecord(P.PDF, FileUtil.getHaxademicOutputPath() + "/pdf/frame-"+SystemUtil.getTimestampFine(p)+".pdf");
		}
	}
	
	protected void finishPDFRender() {
		if(shouldRecord == true) {
			p.endRecord();
			shouldRecord = false;
		}
	}
	
	public void keyPressed() {
		if (p.key == 'r') {
			shouldRecord = true;
		}
	}

}
