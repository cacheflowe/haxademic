package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.net.WebServerRequestHandlerUIControls;
import com.haxademic.core.system.SystemUtil;

import processing.core.PApplet;

public class ConcentricPolygonsLast 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/**
	 * @TODO: Convert the rest of the controls to InputTrigger
	 * @TODO: Add concentric polygons
	 * @TODO: Make animation config array
	 * @TODO: Eased all values instead of setting immediately
	 */
	
	protected WebServer server;
	protected ImageGradient imageGradient;
	protected boolean shouldRecord = false;

	protected InputTrigger knob1 = new InputTrigger(null, null, new Integer[]{21}, null, new String[]{"slider1"});
	protected InputTrigger knob2 = new InputTrigger(null, null, new Integer[]{22}, null, new String[]{"slider2"});
	protected InputTrigger knob3 = new InputTrigger(null, null, new Integer[]{23}, null, new String[]{"slider3"});
	protected InputTrigger knob4 = new InputTrigger(null, null, new Integer[]{24}, null, new String[]{"slider4"});
	protected InputTrigger knob5 = new InputTrigger(null, null, new Integer[]{25}, null, new String[]{"slider5"});
	protected InputTrigger knob6 = new InputTrigger(null, null, new Integer[]{26}, null, new String[]{"slider6"});
	protected InputTrigger knob7 = new InputTrigger(null, null, new Integer[]{27}, null, new String[]{"slider7"});
	protected InputTrigger knob8 = new InputTrigger(null, null, new Integer[]{28}, null, new String[]{"slider8"});
	
	protected float radius = 50;
	protected float vertices = 3f;
	protected float iterateShrink = 0.1f;
	protected float lineWeight = 1;
	protected int maxLevels = 1;
	protected float circleRadius = 0f;
	protected float childDistanceAmp = 1f;
	protected boolean toggleChildRotation = false;
	
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
		if(knob2.triggered()) vertices = P.round(3f + P.map(knob2.value(), 0, 1, 0, 5));

		// rotate polygon to sit on a flat bottom
		p.translate(p.width/2, p.height/2);
		float segmentRads = P.TWO_PI / vertices;
		p.rotate(P.HALF_PI);
		p.rotate(segmentRads / 2f);
		
		// draw shape
		
		// set up concentric polygon config
//		if(p.midiState.midiCCPercent(KNOB_1) > 0) radius = P.map(p.midiState.midiCCPercent(KNOB_1), 0, 1, 50, 500);
		if(knob1.triggered()) radius = P.map(knob1.value(), 0, 1, 50, 500);

		// line weight
//		if(p.midiState.midiCCPercent(KNOB_5) > 0) lineWeight = P.round(3f + P.map(p.midiState.midiCCPercent(KNOB_5), 0, 1, 1, 20));
		if(knob5.triggered()) lineWeight = P.round(3f + P.map(knob5.value(), 0, 1, 1, 20));

		// number of children
//		if(p.midiState.midiCCPercent(KNOB_3) > 0) maxLevels = (int) P.map(p.midiState.midiCCPercent(KNOB_3), 0, 1, 1, 5);
		if(knob3.triggered()) maxLevels = (int) P.map(knob3.value(), 0, 1, 1, 5);
		
		// set shrink amount
		if(knob4.triggered()) iterateShrink = P.map(knob4.value(), 0, 1, 0.1f, 1f);
		
		// set toggleChildRotation
		knob6.triggered();  // call knob to update latest values
//		if(knob6.triggered()) toggleChildRotation = knob6.value() > 0.5f;
		
		// set childDistanceAmp
		if(knob7.triggered()) childDistanceAmp = P.map(knob7.value(), 0, 1, 0.1f, 2f);
		
		// set circleRadius
		if(knob8.triggered()) circleRadius = P.map(knob8.value(), 0, 1, 0.1f, 1f);
		
		// draw mesh
		p.pushMatrix();
		p.fill(255);
		drawDisc(p, radius, radius - lineWeight, (int) vertices, 0, childDistanceAmp, 0);
		p.popMatrix();
		
		// save file
		finishPDFRender();
	}
	
	public void drawDisc( PApplet p, float radius, float innerRadius, int numSegments, float offsetRads, float childDistAmp, int level ) {
		p.pushMatrix();
		
		float segmentRads = P.TWO_PI / numSegments;
		
		float nextRadius = radius * iterateShrink;
		float nextInnerRadius = innerRadius * iterateShrink;
		nextInnerRadius = nextRadius - lineWeight;
		
		offsetRads = (offsetRads == 0) ? (segmentRads / 2f) * knob6.value() : 0;
		
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
			if(circleRadius > 0.2f && level < 99) {
				float circleR = radius * circleRadius;
				float circleInnerR = circleR - lineWeight / 2f;
				drawDisc(p, circleR, circleInnerR, 60, offsetRads, 999, 999);
			}
			
			// draw children 
			if(level < maxLevels) {
				p.pushMatrix();
				float radiusFromParent = (radius - ((radius - innerRadius) / 2f));
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

	
	
	
	// draw concentric shapes
	//	while(radius < 1000 && lineWeight > 5) {
	//		p.pushMatrix();
	//		p.fill(255);
	//		drawDisc(p, radius, radius - lineWeight, (int) vertices);  // Shapes.
	//		radius += lineWeight + spacing;
	//		lineWeight *= 0.85f;
	//		p.popMatrix();
	//	}
		

}
