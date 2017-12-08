package com.haxademic.sketch.render;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ImageGradient;
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
  	 * Add resposive extent tracking to keep shape centered
	 */
	
	protected WebServer server;
	protected ImageGradient imageGradient;
	protected boolean shouldRecord = false;

	boolean easingEaseIn = false;
	float easingVal = 8f;
	protected InputTrigger knob1 = new InputTrigger(null, null, null, new Integer[]{21}, new String[]{"slider1"}); 
	protected EasingFloat radius = new EasingFloat(50, easingVal);
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
	
	protected InputTrigger renderTrigger = new InputTrigger(new char[]{'r'}, null, new Integer[]{LaunchControl.PAD_01}, null, new String[]{"button1"});
	protected InputTrigger saveConfigTrigger = new InputTrigger(new char[]{'s'}, null, new Integer[]{LaunchControl.PAD_02}, null, new String[]{"button2"});
	protected InputTrigger animatingTrigger = new InputTrigger(new char[]{'a'}, null, new Integer[]{LaunchControl.PAD_03}, null, new String[]{"button3"});
	
	protected ArrayList<float[]> animationStops = new ArrayList<float[]>();
	protected boolean isAnimating = false;
	protected int animateIndex = -1;
	
	// draw analysis
	protected int numVertices = 0;
	protected float minY = 0;
	protected float maxY = 0;
	
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
		
		animationStops.add(new float[] {223.62204f, 6.4251966f, 1.031496f, 0.6102362f, 4.0f, 0.007874016f, 0.69842523f, 0.0f});
		animationStops.add(new float[] {174.30208f, 3.4664757f, 1.8185794f, 1.0f, 4.0f, 1.0f, 0.8212837f, 1.0f});
		animationStops.add(new float[] {256.62134f, 3.0f, 3.4092898f, 0.8067287f, 6.0f, 1.0f, 0.383043f, 0.0f});
		animationStops.add(new float[] {210.09306f, 6.4251966f, 2.963891f, 0.5776664f, 4.0f, 1.0f, 0.6701663f, 0.0f});
		animationStops.add(new float[] {328.2033f, 3.108566f, 5.0f, 0.51324266f, 4.0f, 0, 0.503937f, 0.0f});
		animationStops.add(new float[] {253.04224f, 3.108566f, 1.6913227f, 0.5848246f, 11.0f, 0, 0.685278f, 0.9856836f});
		animationStops.add(new float[] {253.04224f, 3.108566f, 1.7867653f, 0.5848246f, 8.0f, 1.0f, 0.59460753f, 1.0f});
		animationStops.add(new float[] {253.04224f, 4.579973f, 2.2957926f, 0.70651394f, 4.0f, 1.0f, 0.6097192f, 0.20544022f});
		animationStops.add(new float[] {156.40659f, 3.7448502f, 3.2502189f, 0.51324266f, 4.0f, 0, 1.3048596f, 0.2412312f});
		animationStops.add(new float[] {292.4123f, 4.222063f, 1.1504812f, 0.5776664f, 5.0f, 1.0f, 0.6097192f, 0.48460987f});
		animationStops.add(new float[] {195.77667f, 4.222063f, 2.1685357f, 0.8067287f, 5.0f, 1.0f, 0.6097192f, 0.23407301f});
		animationStops.add(new float[] {127.7738f, 6.329754f, 2.7730055f, 0.599141f, 4.0f, 0, 1.0479599f, 0.0f});

	}
	
	protected void updateControls() {
		// set up concentric polygon config
		if(knob1.triggered()) radius.setTarget(P.map(knob1.value(), 0.01f, 1, 50, 500));
		radius.update(easingEaseIn);

		// num vertices
		if(knob2.triggered()) vertices.setTarget(3f + P.map(knob2.value(), 0.01f, 1, 0, 5));
		vertices.update(easingEaseIn);
		
		// number of children
		if(knob3.triggered()) maxLevels.setTarget(P.map(knob3.value(), 0.01f, 1, 1, 5));
		maxLevels.update(easingEaseIn);
		
		// set shrink amount
		if(knob4.triggered()) iterateShrink.setTarget(P.map(knob4.value(), 0.01f, 1, 0.1f, 1f));
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
		if(knob8.triggered()) circleRadius.setTarget(P.map(knob8.value(), 0.01f, 1, 0.1f, 1f));
		circleRadius.update(easingEaseIn);
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
		};
		animationStops.add(paramsArray);
		String toStr = "animationStops.add(new float[] {";
		for (int i = 0; i < paramsArray.length; i++) toStr += (i == 0) ? paramsArray[i] + "f" : ", " + paramsArray[i] + "f";
		toStr += "});";
		P.println(toStr);
	}
	
	protected void nextAnimation() {
		if(animationStops.size() == 0) return;
		animateIndex++;
		if(animateIndex >= animationStops.size()) animateIndex = 0;
		p.debugView.setValue("animateIndex", animateIndex);
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
	}
	
	public void drawApp() {
		// context setup
		p.background(0);
		preparePDFRender();
		p.noStroke();
		
		// handle input
		updateControls();
		if(saveConfigTrigger.triggered()) storeParams();
		if(animatingTrigger.triggered()) isAnimating = !isAnimating;
		
		// override params if animating
		if(isAnimating && p.frameCount % 120 == 0) nextAnimation();
		
		// rotate polygon to sit on a flat bottom
		// offset y
		float radiusClosest = MathUtil.polygonClosestPoint(P.floor(vertices.value()), radius.value());
		float radiusDiff = radius.value() - radiusClosest;
		p.translate(p.width/2, p.height/2);
		p.translate(0, radiusDiff/2f);
		float segmentRads = P.TWO_PI / (float) P.floor(vertices.value());
		p.rotate(P.HALF_PI);
		p.rotate(segmentRads / 2f);
		
		// draw shapes
		p.pushMatrix();
		p.fill(255);
		numVertices = 0;
		drawDisc(p, radius.value(), radius.value() - lineWeight.value(), (int) vertices.value(), 0, childDistanceAmp.value(), 0);
		p.popMatrix();
		p.debugView.setValue("numVertices", numVertices);
		
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
			
			numVertices += 6;
			
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
	
}
