package com.haxademic.sketch.render;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
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
  	 * @TODO: Add sticks between parents/children
  	 * @TODO: Add sticks from center to vertices
	 */
	
	protected WebServer server;
	protected ImageGradient imageGradient;
	protected boolean shouldRecord = false;

	boolean easingEaseIn = true;
	float easingVal = 10f;

	protected float startRads = 0;
	
	protected InputTrigger knob1 = new InputTrigger(null, null, null, new Integer[]{21}, new String[]{"slider1"}); 
	protected EasingFloat radius = new EasingFloat(50, easingVal / 2f);
	protected InputTrigger knob2 = new InputTrigger(null, null, null, new Integer[]{22}, new String[]{"slider2"});
	protected EasingFloat vertices = new EasingFloat(3, easingVal);
	protected InputTrigger knob3 = new InputTrigger(null, null, null, new Integer[]{23}, new String[]{"slider3"});
	protected EasingFloat maxLevels = new EasingFloat(1, easingVal);
	protected InputTrigger knob4 = new InputTrigger(null, null, null, new Integer[]{24}, new String[]{"slider4"});
	protected EasingFloat iterateShrink = new EasingFloat(0.1f, easingVal);
	protected InputTrigger knob5 = new InputTrigger(null, null, null, new Integer[]{25}, new String[]{"slider5"});
	protected EasingFloat lineWeight = new EasingFloat(1, easingVal);
	protected InputTrigger knob6 = new InputTrigger(null, null, null, new Integer[]{26}, new String[]{"slider6"});
	protected EasingFloat offsetRotation = new EasingFloat(0, easingVal);
	protected InputTrigger knob7 = new InputTrigger(null, null, null, new Integer[]{27}, new String[]{"slider7"});
	protected EasingFloat childDistanceAmp = new EasingFloat(1, easingVal);
	protected InputTrigger knob8 = new InputTrigger(null, null, null, new Integer[]{28}, new String[]{"slider8"});
	protected EasingFloat circleRadius = new EasingFloat(0, easingVal);
	protected InputTrigger knob9 = new InputTrigger(null, null, null, new Integer[]{LaunchControl.KNOB_01}, new String[]{"slider9"});
	protected EasingFloat radialConnections = new EasingFloat(0, easingVal);
	protected InputTrigger knob10 = new InputTrigger(null, null, null, new Integer[]{LaunchControl.KNOB_02}, new String[]{"slider10"});
	protected EasingFloat circleLevelDisplay = new EasingFloat(0, 1);
	
	protected InputTrigger renderTrigger = new InputTrigger(new char[]{'r'}, null, new Integer[]{LaunchControl.PAD_01}, null, new String[]{"button1"});
	protected InputTrigger saveConfigTrigger = new InputTrigger(new char[]{'s'}, null, new Integer[]{LaunchControl.PAD_02}, null, new String[]{"button2"});
	protected InputTrigger animatingTrigger = new InputTrigger(new char[]{'a'}, null, new Integer[]{LaunchControl.PAD_03}, null, new String[]{"button3"});
	protected InputTrigger prevTrigger = new InputTrigger(new char[]{'1'}, null, new Integer[]{LaunchControl.PAD_04}, null, new String[]{"button4"});
	protected InputTrigger nextTrigger = new InputTrigger(new char[]{'2'}, null, new Integer[]{LaunchControl.PAD_05}, null, new String[]{"button5"});
	
	protected ArrayList<float[]> animationStops = new ArrayList<float[]>();
	protected boolean isAnimating = true;
	protected int animateIndex = -1;
	
	// draw analysis
	protected int circleResolution = 60;
	protected int numVertices = 0;
	protected float minY = -1;
	protected float maxY = 1;
	protected float shapeHeight = 0;
	protected boolean responsiveHeight = true;
	protected EasingFloat offsetY = new EasingFloat(0, easingVal);
	
	protected void overridePropsFile() {
		int FRAMES = 140;
//		p.appConfig.setProperty( AppSettings.RENDERER, PRenderers.PDF );
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 7 );		// num animations + 1. 4 will render a loop of 3 shapes
	}
	
	public void setupFirstFrame() {
		server = new WebServer(new WebServerRequestHandlerUIControls(), false);
		if(PRenderers.currentRenderer() == PRenderers.PDF) shouldRecord = true;
		imageGradient = new ImageGradient(ImageGradient.BLACK_HOLE());
		
		// david selects
		animationStops.add(new float[] {271.9227f, 3.8200908f, 3.2502189f, 0.32259604f, 20.0f, 1.0f, 1.0781834f, 1.099141f, 0.0f, 1f});
		animationStops.add(new float[] {299.5558f, 3.8200908f, 3.7274318f, 0.29237255f, 20.0f, 1.0f, 0.9119542f, 1.2438956f, 0.0f, 1f});
		animationStops.add(new float[] {278.70633f, 3.8200908f, 3.6956174f, 0.20170206f, 18.0f, 0, 1.1990775f, 1.1659509f, 0.0f, 1f});
		animationStops.add(new float[] {245.33487f, 3.8200908f, 4.045574f, 0.2772608f, 20.0f, 1.0f, 1.1990775f, 1.4331902f, 0.0f, 1f});
		animationStops.add(new float[] {224.85864f, 3.8200908f, 4.045574f, 0.383043f, 20.0f, 0, 1.1990775f, 1.3441104f, 0.0f, 1f});
		animationStops.add(new float[] {198.92818f, 3.6530662f, 4.1728306f, 0.35281953f, 15.0f, 0, 1.3955301f, 1.5315359f, 0.0f, 1f});


		// justin selects
		animationStops.add(new float[] {290.46152f, 3.8200908f, 3.8546886f, 0.54927224f, 16.0f, 1.0f, 0.70038974f, 0.10812853f, 0.0f, 0.0f});
		animationStops.add(new float[] {223.62204f, 6.4251966f, 1.031496f, 0.6102362f, 4.0f, 0, 0.69842523f, 0.0f, 0, 0.0f});
		animationStops.add(new float[] {81.90229f, 3.4860415f, 3.3456614f, 1.576871f, 13.0f, 1.0f, 0.20170206f, 0.0f, 0.12510936f, 0.0f});
		animationStops.add(new float[] {174.30208f, 3.4664757f, 1.8185794f, 1.0f, 4.0f, 1.0f, 0.8212837f, 1.0f, 0, 0.0f});
		animationStops.add(new float[] {256.62134f, 3.0f, 3.4092898f, 0.8067287f, 6.0f, 1.0f, 0.383043f, 0.0f, 0, 0.0f});
		animationStops.add(new float[] {210.09306f, 6.4251966f, 2.963891f, 0.5776664f, 4.0f, 1.0f, 0.6701663f, 0.0f, 0, 0.0f});
		animationStops.add(new float[] {424.29422f, 3.108566f, 5.0f, 0.50608444f, 4.0f, 0.0f, 0.503937f, 0.0f, 0, 0.0f});
		animationStops.add(new float[] {253.04224f, 3.108566f, 1.6913227f, 0.5848246f, 11.0f, 0, 0.685278f, 0.9856836f, 0.5f, 0.0f});
		animationStops.add(new float[] {253.04224f, 3.108566f, 1.7867653f, 0.5848246f, 8.0f, 1.0f, 0.59460753f, 1.0f, 0, 0.0f});
		animationStops.add(new float[] {122.76751f, 5.4946313f, 2.3276067f, 0.7494632f, 9.0f, 1.0f, 1.3350831f, 0.09806729f, 0, 0.0f});
		animationStops.add(new float[] {253.04224f, 4.579973f, 2.2957926f, 0.70651394f, 4.0f, 1.0f, 0.6097192f, 0.20544022f, 0, 0.0f});
		animationStops.add(new float[] {156.40659f, 3.7448502f, 3.2502189f, 0.51324266f, 4.0f, 0, 1.3048596f, 0.2412312f, 0, 0.0f});
		animationStops.add(new float[] {399.99997f, 3.8200908f, 2.8366342f, 0.3074843f, 23.0f, 0.0f, 0.36793125f, 0.0f, 0, 0.0f});
		animationStops.add(new float[] {292.4123f, 4.222063f, 1.1504812f, 0.5776664f, 5.0f, 1.0f, 0.6097192f, 0.48460987f, 0, 0.0f});
		animationStops.add(new float[] {195.77667f, 4.222063f, 2.1685357f, 0.8067287f, 5.0f, 1.0f, 0.6097192f, 0.23407301f, 0, 0.0f});
		animationStops.add(new float[] {102.02553f, 3.5062437f, 3.1865902f, 0.634932f, 4.0f, 1.0f, 2.0f, 1.0f, 0, 0.0f});
		animationStops.add(new float[] {127.7738f, 6.329754f, 2.7730055f, 0.599141f, 4.0f, 0, 1.0479599f, 0.0f, 0, 0.0f});
		animationStops.add(new float[] {206.51396f, 6.091148f, 1.7867653f, 0.72798854f, 23.0f, 1.0f, 0.94217765f, 0.0f, 0, 0.0f});
		animationStops.add(new float[] {399.99994f, 3.8200908f, 2.8366342f, 0.18659031f, 18.0f, 1.0f, 0.35281953f, 1.0657362f, 0, 1.0f});
		animationStops.add(new float[] {142.0902f, 3.010101f, 2.3912354f, 0.6778812f, 7.0f, 0, 1.3955301f, 0.49176806f, 0, 0.0f});
		animationStops.add(new float[] {199.2112f, 6.568361f, 2.4230494f, 0.34144592f, 4.0f, 0, 1.3350831f, 0.0f, 0, 0.0f});
		animationStops.add(new float[] {97.58435f, 3.9039211f, 2.8366342f, 1.0f, 4.0f, 1.0f, 1.7582121f, 0.49892625f, 0, 0.0f});
		animationStops.add(new float[] {260.44373f, 5.4946313f, 1.59588f, 0.87115246f, 7.0f, 0, 0.4132665f, 0.09806729f, 0, 0.0f});
		animationStops.add(new float[] {107.44368f, 3.8200908f, 3.7274318f, 1.0026246f, 20.0f, 1.0f, 1.1084069f, 0.09699356f, 0.0f, 0.0f});

		if(isAnimating) nextAnimation(1);
	}
	
	protected void updateControls() {
		// set up concentric polygon config
		if(knob1.triggered()) radius.setTarget(P.map(knob1.value(), 0.01f, 1, 50, 500));
		radius.update(true);

		// num vertices
		if(knob2.triggered()) vertices.setTarget(3f + P.map(knob2.value(), 0.01f, 1, 0, 7));
		vertices.update(easingEaseIn);
		
		// number of children
		if(knob3.triggered()) maxLevels.setTarget(P.map(knob3.value(), 0.01f, 1, 1, 5));
		maxLevels.update(easingEaseIn);
		
		// set shrink amount
		if(knob4.triggered()) iterateShrink.setTarget(P.map(knob4.value(), 0.01f, 1, 0.1f, 2f));
		iterateShrink.update(easingEaseIn);
		
		// line weight
		if(knob5.triggered()) lineWeight.setTarget(P.round(3f + P.map(knob5.value(), 0.01f, 1, 1, 20)));
		lineWeight.update(easingEaseIn);

		// set toggleChildRotation
		if(knob6.triggered()) offsetRotation.setTarget(P.map(knob6.value(), 0.01f, 1, 0, 1f));
		offsetRotation.update(easingEaseIn);
		
		// set childDistanceAmp
		if(knob7.triggered()) childDistanceAmp.setTarget(P.map(knob7.value(), 0.01f, 1, 0.1f, 2f));
		childDistanceAmp.update(easingEaseIn);
		
		// set circleRadius
		if(knob8.triggered()) circleRadius.setTarget(P.map(knob8.value(), 0.01f, 1, 0.1f, 2.0f));
		circleRadius.update(easingEaseIn);
		
		// set radialConnections
		if(knob9.triggered()) radialConnections.setTarget(P.map(knob9.value(), 0.01f, 1, 0f, 1f));
		radialConnections.update(easingEaseIn);
		
		// set circleLevelCutoff
		if(knob10.triggered()) circleLevelDisplay.setTarget(P.round(P.map(knob10.value(), 0.01f, 1, 0f, 5)));
		circleLevelDisplay.update();
		
		// animation index
		if(prevTrigger.triggered()) nextAnimation(-1);
		if(nextTrigger.triggered()) nextAnimation(1);
	}

	protected void updateDebug() {
		p.debugView.setValue("numVertices", numVertices);
		p.debugView.setValue("minY", minY);
		p.debugView.setValue("maxY", maxY);
		p.debugView.setValue("shapeHeight", shapeHeight);
		p.debugView.setValue("offsetY.value()", offsetY.value());
//		p.debugView.setValue("(minY + maxY) / 2f", (minY + maxY) / 2f);	
	}
	
	protected void storeParams() {
		float[] paramsArray = new float[] {
				radius.target(),
				vertices.target(),
				maxLevels.target(),
				iterateShrink.target(),
				lineWeight.target(),
				offsetRotation.target(),
				childDistanceAmp.target(),
				circleRadius.target(),
				radialConnections.target(),
				circleLevelDisplay.target(),
		};
		animationStops.add(paramsArray);
		String toStr = "animationStops.add(new float[] {";
		for (int i = 0; i < paramsArray.length; i++) toStr += (i == 0) ? paramsArray[i] + "f" : ", " + paramsArray[i] + "f";
		toStr += "});";
		P.println(toStr);
	}
	
	protected void nextAnimation(int step) {
		if(animationStops.size() == 0) return;
		animateIndex += step;
		if(animateIndex >= animationStops.size()) animateIndex = 0;
		if(animateIndex < 0) animateIndex = animationStops.size() - 1;
		p.debugView.setValue("animateIndex", animateIndex + " / " + (animationStops.size() - 1));
		float[] paramsArray = animationStops.get(animateIndex);
		// apply stored params
		radius.setTarget(paramsArray[0]);
		vertices.setTarget(paramsArray[1]);
		maxLevels.setTarget(paramsArray[2]);
		iterateShrink.setTarget(paramsArray[3]);
		lineWeight.setTarget(paramsArray[4]);
		offsetRotation.setTarget(paramsArray[5]);
		childDistanceAmp.setTarget(paramsArray[6]);
		circleRadius.setTarget(paramsArray[7]);
		radialConnections.setTarget(paramsArray[8]);
		circleLevelDisplay.setTarget(paramsArray[9]);
	}
	
	public void drawApp() {
		// context setup
		p.background(0);
		if(renderTrigger.triggered()) shouldRecord = true;
		preparePDFRender();
		p.noStroke();
		DrawUtil.setDrawCenter(p);
		
		// handle input
		updateControls();
		updateDebug();
		if(saveConfigTrigger.triggered()) storeParams();
		if(animatingTrigger.triggered()) isAnimating = !isAnimating;
		
		// override params if animating
		if(isAnimating && loop.progress() == 0) nextAnimation(1);
		
		// offset y
		p.translate(p.width/2, p.height/2);
		offsetY.setTarget(-(minY + maxY) / 2f);
		offsetY.update();
		p.translate(0, offsetY.value());
		
		// start rotation to keep polygon bottom flat
		float segmentRads = P.TWO_PI / (float) P.floor(vertices.value());
		startRads = P.HALF_PI + segmentRads / 2f;
		
		// draw shapes
		p.pushMatrix();
		p.fill(255);
		numVertices = 0;
		minY = 0;
		maxY = 0;
		drawDisc(p, radius.value(), radius.value() - lineWeight.value(), (int) vertices.value(), 0, childDistanceAmp.value(), 0, 0, 0);
		p.popMatrix();
		
		// responsive height
		shapeHeight = maxY - minY;
		if(responsiveHeight == true) radius.setTarget(radius.value() * MathUtil.scaleToTarget(shapeHeight, p.height * 0.75f));
		
		// save file
		finishPDFRender();
	}
	
	public void drawDisc( PApplet p, float radius, float innerRadius, int numSegments, float offsetRads, float childDistAmp, int level, float x, float y ) {
		p.pushMatrix();
		
		float segmentRads = P.TWO_PI / numSegments;
//		float halfThickness = lineWeight.value() / 2f;
		
		float nextRadius = radius * iterateShrink.value();
		float nextInnerRadius = innerRadius * iterateShrink.value();
		nextInnerRadius = nextRadius - lineWeight.value();
		
		offsetRads = (offsetRads == 0) ? (segmentRads / 2f) * offsetRotation.value() : 0;
		if(level < 2) offsetRads = 0;
		
		for( int i = 0; i < numSegments; i++ ) {
			
			// calc vertex
			float curRads = startRads + i * segmentRads + offsetRads;
			float nextRads = startRads + (i + 1) * segmentRads + offsetRads;
			
			// draw polygon mesh
			p.beginShape(P.TRIANGLES);
			p.vertex( P.cos( curRads ) * innerRadius, 	P.sin( curRads ) * innerRadius );
			p.vertex( P.cos( curRads ) * radius, 		P.sin( curRads ) * radius );
			p.vertex( P.cos( nextRads ) * radius, 		P.sin( nextRads ) * radius );
			
			p.vertex( P.cos( curRads ) * innerRadius, 	P.sin( curRads ) * innerRadius );
			p.vertex( P.cos( nextRads ) * innerRadius, 	P.sin( nextRads ) * innerRadius );
			p.vertex( P.cos( nextRads ) * radius, 		P.sin( nextRads ) * radius );
			p.endShape();
			
			// update analysis
			numVertices += 6;
			minY = P.min(minY, y + P.sin( curRads ) * radius);
			maxY = P.max(maxY, y + P.sin( curRads ) * radius);
			
			// draw radial sticks
			if(radialConnections.value() > 0 && level < 99) {
				p.pushStyle();
				p.stroke(255);
				p.strokeWeight(lineWeight.value() * radialConnections.value());
				p.line(0, 0, P.cos( curRads ) * innerRadius, P.sin( curRads ) * innerRadius);
				p.popStyle();
			}
			
			// draw circle
			if(circleRadius.value() > 0.2f && (level == circleLevelDisplay.value() || circleLevelDisplay.value() == 0) && level < 99) {
				float circleR = radius * circleRadius.value();
				float circleInnerR = circleR - lineWeight.value() / 2f;
				
//				p.pushStyle();
//				p.stroke(255);
//				p.noFill();
//				p.strokeWeight(lineWeight.value());
////				p.ellipse(0, 0, innerRadius * 2f + lineWeight.value() / 2f, innerRadius * 2f + lineWeight.value() / 2f);
//				p.ellipse(0, 0, circleR * 2f, circleR * 2f);
//				p.popStyle();

				if(numVertices < 300000) drawDisc(p, circleR, circleInnerR, circleResolution, offsetRads, 999, 999, x, y);
			}
			
			// draw children 
			if(level < P.floor(maxLevels.value()) && radius > 5) {
				// draw child polygon at vertices
				float radiusFromParent = (radius - ((radius - innerRadius)));
				radiusFromParent *= childDistAmp;
				float xAdd = P.cos( curRads ) * radiusFromParent;
				float yAdd = P.sin( curRads ) * radiusFromParent;
				p.pushMatrix();
				p.translate(xAdd, yAdd);	// recursion makes this additive
				if(numVertices < 300000) drawDisc(p, nextRadius, nextInnerRadius, numSegments, offsetRads, childDistAmp, level + 1, x + xAdd, y + yAdd);
				p.popMatrix();
				
				// draw stick from parent to child
//				if(radialConnections.value() > 0 && level < 99) {
//					p.pushStyle();
//					p.stroke(255);
//					p.strokeWeight(lineWeight.value() * radialConnections.value());
//					p.line(0, 0, xAdd, yAdd);
//					p.popStyle();
//				}
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
	
}
