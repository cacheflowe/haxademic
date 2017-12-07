package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.net.WebServerRequestHandlerUIControls;
import com.haxademic.core.system.SystemUtil;

import processing.core.PApplet;

public class ConcentricPolygonsLast 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/**
  	 * @TODO: Add concentric polygons
	 * @TODO: Make animation config array
	 */
	
	protected WebServer server;
	protected ImageGradient imageGradient;
	protected boolean shouldRecord = false;

	float easingVal = 10f;
	protected InputTrigger knob1 = new InputTrigger(null, null, new Integer[]{21}, null, new String[]{"slider1"}); 
	protected EasingFloat radius = new EasingFloat(50, easingVal);
	protected InputTrigger knob2 = new InputTrigger(null, null, new Integer[]{22}, null, new String[]{"slider2"});
	protected EasingFloat vertices = new EasingFloat(3, easingVal);
	protected InputTrigger knob3 = new InputTrigger(null, null, new Integer[]{23}, null, new String[]{"slider3"});
	protected EasingFloat maxLevels = new EasingFloat(1, easingVal);
	protected InputTrigger knob4 = new InputTrigger(null, null, new Integer[]{24}, null, new String[]{"slider4"});
	protected EasingFloat iterateShrink = new EasingFloat(0.1f, easingVal);
	protected InputTrigger knob5 = new InputTrigger(null, null, new Integer[]{25}, null, new String[]{"slider5"});
	protected EasingFloat lineWeight = new EasingFloat(1, easingVal);
	protected InputTrigger knob6 = new InputTrigger(null, null, new Integer[]{26}, null, new String[]{"slider6"});
	protected EasingFloat offsetRotation = new EasingFloat(0, easingVal);
	protected InputTrigger knob7 = new InputTrigger(null, null, new Integer[]{27}, null, new String[]{"slider7"});
	protected EasingFloat childDistanceAmp = new EasingFloat(1, easingVal);
	protected InputTrigger knob8 = new InputTrigger(null, null, new Integer[]{28}, null, new String[]{"slider8"});
	protected EasingFloat circleRadius = new EasingFloat(0, easingVal);
	
	protected void overridePropsFile() {
		int FRAMES = 140;
//		p.appConfig.setProperty( AppSettings.RENDERER, PRenderers.PDF );
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES );
	}
	
	public void setupFirstFrame() {
		server = new WebServer(new WebServerRequestHandlerUIControls(), false);
		if(PRenderers.currentRenderer() == PRenderers.PDF) shouldRecord = true;
		imageGradient = new ImageGradient(ImageGradient.BLACK_HOLE());
	}

	public void drawApp() {
		p.background(0);
		preparePDFRender();
		p.noStroke();
		
		// add this for random concentric shapes?
//		float spacing = 50; 
//		if(p.midiState.midiCCPercent(KNOB_1) > 0) radius = P.map(p.midiState.midiCCPercent(KNOB_1), 0, 1, 100, 500);

		// num vertices
		if(knob2.triggered()) vertices.setTarget(3f + P.map(knob2.value(), 0, 1, 0, 5));
		vertices.update();

		// rotate polygon to sit on a flat bottom
		// offset y
		float radiusClosest = MathUtil.polygonClosestPoint(P.floor(vertices.value()), radius.value());
		float radiusDiff = radius.value() - radiusClosest;
		p.translate(p.width/2, p.height/2);
		p.translate(0, radiusDiff/2f);
		float segmentRads = P.TWO_PI / (float) P.floor(vertices.value());
		p.rotate(P.HALF_PI);
		p.rotate(segmentRads / 2f);
		
		// draw shape
		
		// set up concentric polygon config
		if(knob1.triggered()) radius.setTarget(P.map(knob1.value(), 0, 1, 50, 500));
		radius.update();

		// line weight
		if(knob5.triggered()) lineWeight.setTarget(P.round(3f + P.map(knob5.value(), 0, 1, 1, 20)));
		lineWeight.update();

		// number of children
		if(knob3.triggered()) maxLevels.setTarget(P.map(knob3.value(), 0, 1, 1, 5));
		maxLevels.update();
		
		// set shrink amount
		if(knob4.triggered()) iterateShrink.setTarget(P.map(knob4.value(), 0, 1, 0.1f, 1f));
		iterateShrink.update();
		
		// set toggleChildRotation
		if(knob6.triggered()) offsetRotation.setTarget(P.map(knob6.value(), 0, 1, 0, 1f));
		offsetRotation.update();
		
		// set childDistanceAmp
		if(knob7.triggered()) childDistanceAmp.setTarget(P.map(knob7.value(), 0, 1, 0.1f, 2f));
		childDistanceAmp.update();
		
		// set circleRadius
		if(knob8.triggered()) circleRadius.setTarget(P.map(knob8.value(), 0, 1, 0.1f, 1f));
		circleRadius.update();
		
		// draw mesh
		p.pushMatrix();
		p.fill(255);
		drawDisc(p, radius.value(), radius.value() - lineWeight.value(), (int) vertices.value(), 0, childDistanceAmp.value(), 0);
		p.popMatrix();
		
		// save file
		finishPDFRender();
	}
	
	public void drawDisc( PApplet p, float radius, float innerRadius, int numSegments, float offsetRads, float childDistAmp, int level ) {
		p.pushMatrix();
		
		float segmentRads = P.TWO_PI / numSegments;
		
		float nextRadius = radius * iterateShrink.value();
		float nextInnerRadius = innerRadius * iterateShrink.value();
		nextInnerRadius = nextRadius - lineWeight.value();
		
		offsetRads = (offsetRads == 0) ? (segmentRads / 2f) * offsetRotation.value() : 0;
		
		for( int i = 0; i < numSegments; i++ ) {
			
			float curRads = i * segmentRads + offsetRads;
			float nextRads = (i + 1) * segmentRads + offsetRads;
			
			// draw polygon mesh
			p.beginShape(P.TRIANGLES);
			p.vertex( P.cos( curRads ) * innerRadius, 	P.sin( curRads ) * innerRadius );
			p.vertex( P.cos( curRads ) * radius, 		P.sin( curRads ) * radius );
			p.vertex( P.cos( nextRads ) * radius, 		P.sin( nextRads ) * radius );
			
			p.vertex( P.cos( curRads ) * innerRadius, 	P.sin( curRads ) * innerRadius );
			p.vertex( P.cos( nextRads ) * innerRadius, 	P.sin( nextRads ) * innerRadius );
			p.vertex( P.cos( nextRads ) * radius, 		P.sin( nextRads ) * radius );
			p.endShape();
			
			// draw circle
			if(circleRadius.value() > 0.2f && level < 99) {
				float circleR = radius * circleRadius.value();
				float circleInnerR = circleR - lineWeight.value() / 2f;
				drawDisc(p, circleR, circleInnerR, 60, offsetRads, 999, 999);
			}
			
			// draw children 
			if(level < P.floor(maxLevels.value()) && radius > 10) {
				p.pushMatrix();
				float radiusFromParent = (radius - ((radius - innerRadius)));
				radiusFromParent *= childDistAmp;
				float x = P.cos( curRads ) * radiusFromParent;
				float y = P.sin( curRads ) * radiusFromParent;
				p.translate(x, y);
				drawDisc(p, nextRadius, nextInnerRadius, numSegments, offsetRads, childDistAmp, level + 1);
				p.popMatrix();
			}
		}
		
		p.popMatrix();
	}

	protected void preparePDFRender() {
		if(shouldRecord == true) {
			p.beginRecord(P.PDF, FileUtil.getHaxademicOutputPath() + "/pdf/frame-"+SystemUtil.getTimestamp(p)+".pdf");
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
