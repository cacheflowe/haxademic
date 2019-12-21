package com.haxademic.render;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.system.SystemUtil;

import processing.core.PApplet;

public class ConcentricPolygonsEased 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/**
  	 * @TODO: Add concentric polygons?
	 */
	
	protected WebServer server;
	protected boolean shouldRecord = false;
	boolean RENDERING = false;

	boolean easingEaseIn = true;
	float easingVal = 10f;
	float easingLevelActive = 0.025f;

	protected float startRads = 0;
	
	protected InputTrigger knob1 = new InputTrigger(null, null, null, new Integer[]{21}, new String[]{"slider1"}); 
	protected EasingFloat radius = new EasingFloat(50, easingVal / 2f);
	protected InputTrigger knob2 = new InputTrigger(null, null, null, new Integer[]{22}, new String[]{"slider2"});
	protected EasingFloat vertices = new EasingFloat(3, easingVal);
	protected InputTrigger knob3 = new InputTrigger(null, null, null, new Integer[]{23}, new String[]{"slider3"});
	protected EasingFloat maxLevels = new EasingFloat(1, easingVal);
	protected LinearFloat[] levelsActive = new LinearFloat[] { new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive) };
	protected LinearFloat[] circleLevelActive = new LinearFloat[] { new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive) };
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
	protected boolean isAnimating = false;
	protected int animateIndex = -1;
	
	// draw analysis
	protected int circleResolution = 60;
	protected int numVertices = 0;
	protected float minY = -1;
	protected float maxY = 1;
	protected float shapeHeight = 0;
	protected boolean responsiveHeight = false;
	protected EasingFloat offsetY = new EasingFloat(0, easingVal);
	
	protected void config() {
		int FRAMES = 140;
		// if(RENDERING == true) 
//		Config.setProperty( AppSettings.RENDERER, PRenderers.PDF );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
//		Config.setProperty( AppSettings.WIDTH, 1920 );
//		Config.setProperty( AppSettings.HEIGHT, 1080 );
		Config.setProperty( AppSettings.FILLS_SCREEN, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE, RENDERING );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 75 );		// num animations + 1. 4 will render a loop of 3 shapes
	}
	
	public void firstFrame() {
		MidiDevice.init(0, 0);
		server = new WebServer(new UIControlsHandler(), false);
		if(PRenderers.currentRenderer() == PRenderers.PDF) shouldRecord = true;
		if(isAnimating) nextAnimation(1);
	}
	
	protected void updateControls() {
		// set up concentric polygon config
		if(knob1.triggered()) radius.setTarget(P.map(knob1.value(), 0.01f, 1, 50, 500));
		radius.update(true);
		DebugView.setValue("radius", radius.value());
		DebugView.setValue("radius target", radius.target());

		// num vertices
		if(knob2.triggered()) vertices.setTarget(3f + P.map(knob2.value(), 0.01f, 1, 0, 7));
		// vertices.setTarget(3);
		if(vertices.target() < 3) vertices.setTarget(3);
		vertices.update(easingEaseIn);
		
		// number of children
		if(knob3.triggered()) maxLevels.setTarget(P.round(P.map(knob3.value(), 0.01f, 1, 1, 5)));
		maxLevels.update(easingEaseIn);
		DebugView.setValue("maxLevels.value()",maxLevels.target());
		
		// set shrink amount
		if(knob4.triggered()) iterateShrink.setTarget(P.map(knob4.value(), 0.01f, 1, 0.1f, 2f));
		if(iterateShrink.target() < 0.01f) iterateShrink.setTarget(0);
		iterateShrink.update(easingEaseIn);
		
		// line weight
		if(knob5.triggered()) lineWeight.setTarget(P.round(3f + P.map(knob5.value(), 0.01f, 1, 1, 50)));
		if(lineWeight.target() < 0.01f) lineWeight.setTarget(0);
		lineWeight.update(easingEaseIn);

		// set toggleChildRotation
		if(knob6.triggered()) offsetRotation.setTarget(P.map(knob6.value(), 0.01f, 1, 0, 1f));
		if(offsetRotation.target() < 0.01f) offsetRotation.setTarget(0);
		offsetRotation.update(easingEaseIn);
		
		// set childDistanceAmp
		if(knob7.triggered()) childDistanceAmp.setTarget(P.map(knob7.value(), 0.01f, 1, 0.1f, 3f));
		if(childDistanceAmp.target() < 0.01f) childDistanceAmp.setTarget(0);
		childDistanceAmp.update(easingEaseIn);
		
		// set circleRadius
		if(knob8.triggered()) circleRadius.setTarget(P.map(knob8.value(), 0.01f, 1, 0.0f, 2.1f));
		if(circleRadius.target() < 0.01f) circleRadius.setTarget(0);
		circleRadius.update(easingEaseIn);
		
		// set radialConnections
		if(knob9.triggered()) radialConnections.setTarget(P.map(knob9.value(), 0.01f, 1, 0f, 1f));
		if(radialConnections.target() < 0.01f) radialConnections.setTarget(0);
		radialConnections.update(easingEaseIn);
		
		// set circleLevelCutoff
		if(knob10.triggered()) circleLevelDisplay.setTarget(P.round(P.map(knob10.value(), 0.01f, 1, 0f, 5)));
		circleLevelDisplay.update();
		
		// animation index
		if(prevTrigger.triggered()) nextAnimation(-1);
		if(nextTrigger.triggered()) nextAnimation(1);
	}
	
	protected void updateLevelsActive() {
		for (int i = 0; i < levelsActive.length; i++) {
			if(i < P.round(maxLevels.target() - 1f)) {
				levelsActive[i].setTarget(1);  
			} else {
				levelsActive[i].setTarget(0); 
			}
			levelsActive[i].update();
		}
	}
	
	protected void updateDebug() {
		DebugView.setValue("numVertices", numVertices);
		DebugView.setValue("minY", minY);
		DebugView.setValue("maxY", maxY);
		DebugView.setValue("shapeHeight", shapeHeight);
		DebugView.setValue("offsetY.value()", offsetY.value());
//		DebugView.setValue("(minY + maxY) / 2f", (minY + maxY) / 2f);	
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
		SystemUtil.copyStringToClipboard(toStr);
	}
	
	protected void nextAnimation(int step) {
		if(animationStops.size() == 0) return;
		animateIndex += step;
		if(animateIndex >= animationStops.size()) animateIndex = 0;
		if(animateIndex < 0) animateIndex = animationStops.size() - 1;
		DebugView.setValue("animateIndex", animateIndex + " / " + (animationStops.size() - 1));
		float[] paramsArray = animationStops.get(animateIndex);
		// apply stored params
		radius.setTarget(paramsArray[0]);
		vertices.setTarget(paramsArray[1]);
		maxLevels.setTarget(paramsArray[2]);
		updateLevelsActive();
		iterateShrink.setTarget(paramsArray[3]);
		lineWeight.setTarget(paramsArray[4]);
		offsetRotation.setTarget(paramsArray[5]);
		childDistanceAmp.setTarget(paramsArray[6]);
		circleRadius.setTarget(paramsArray[7]);
		radialConnections.setTarget(paramsArray[8]);
		circleLevelDisplay.setTarget(paramsArray[9]);
	}
	
	protected void toggleAnimating() {
		isAnimating = !isAnimating;
	}
	
	public void drawApp() {
		// context setup
		p.background(0);
//		p.background(100 * beatDisplay);
//		if(beatDisplay > 0) beatDisplay -= 0.01f;
		
		if(renderTrigger.triggered()) shouldRecord = true;
		preparePDFRender();
		p.noStroke();
		PG.setDrawCenter(p);
		
		// override params if animating
		if(isAnimating && loop.progress() == 0) nextAnimation(1);
		
		// handle input
		updateControls();
		updateLevelsActive();
		updateDebug();
		if(saveConfigTrigger.triggered()) storeParams();
		if(animatingTrigger.triggered()) toggleAnimating();
		
		// offset y
		p.translate(p.width/2, p.height/2);
		offsetY.setTarget(-(minY + maxY) / 2f);
		offsetY.update();
		p.translate(0, offsetY.value());
		
		// start rotation to keep polygon bottom flat
		float segmentRads = P.TWO_PI / (float) P.floor(vertices.value());
		// segmentRads = P.TWO_PI / vertices.value();
		startRads = P.HALF_PI + segmentRads / 2f;
		
		// draw shapes
		p.pushMatrix();
		p.fill(255);
		numVertices = 0;
		minY = 0;
		maxY = 0;
		drawPolygon(p, radius.value(), radius.value() - lineWeight.value(), (int) vertices.value(), 0, childDistanceAmp.value(), 0, 0, 0);
		p.popMatrix();
		
		// responsive height
		shapeHeight = maxY - minY;
		if(responsiveHeight == true) radius.setTarget(radius.value() * MathUtil.scaleToTarget(shapeHeight, p.height * 0.75f));
		
		// save file
		finishPDFRender();
	}
	
	public void drawPolygon( PApplet p, float radius, float innerRadius, int numSegments, float offsetRads, float childDistAmp, int level, float x, float y ) {
		p.pushMatrix();
		
		float segmentRads = P.TWO_PI / numSegments;
//		float halfThickness = lineWeight.value() / 2f;
		
		float leveActiveAmp = (level < 20) ? levelsActive[level].value() : 0;
		
		float nextRadius = radius * iterateShrink.value() * leveActiveAmp;
		float nextInnerRadius = innerRadius * iterateShrink.value() * leveActiveAmp;
		nextInnerRadius = nextRadius - lineWeight.value() * leveActiveAmp;
		
		offsetRads = (offsetRads == 0) ? (segmentRads / 2f) * offsetRotation.value() : 0;
		if(level < 1) offsetRads = 0;
		
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
			
			// draw circles at vertices
			if(level < maxLevels.target() - 1) {
				if(circleRadius.value() > 0.0f && (level == circleLevelDisplay.target() || circleLevelDisplay.target() == 0) && level < 99) {
					float circleR = radius * circleRadius.value();
					float circleInnerR = circleR - lineWeight.value() / 2f;
					p.pushMatrix();
					p.translate(P.cos( curRads ) * innerRadius, P.sin( curRads ) * innerRadius);
					if(circleR > 1) drawPolygon(p, circleR, circleInnerR, circleResolution, offsetRads, 999, 999, x, y);
					p.popMatrix();
				}
			}
			
			// draw radial sticks
			if(radialConnections.value() > 0 && level < 99) {
				p.pushStyle();
				p.stroke(255);
				p.strokeWeight(lineWeight.value() * radialConnections.value());
				p.line(0, 0, P.cos( curRads ) * innerRadius, P.sin( curRads ) * innerRadius);
				p.popStyle();
			}
			
			// draw children 
			if(level < maxLevels.target() && radius > 1) {
				// draw child polygon at vertices
				float radiusFromParent = (radius - ((radius - innerRadius)));
				radiusFromParent *= childDistAmp;
				float xAdd = P.cos( curRads ) * radiusFromParent;
				float yAdd = P.sin( curRads ) * radiusFromParent;
				p.pushMatrix();
				p.translate(xAdd, yAdd);	// recursion makes this additive
				drawPolygon(p, nextRadius, nextInnerRadius, numSegments, offsetRads, childDistAmp, level + 1, x + xAdd, y + yAdd);
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
			
			// draw circle from center of shape
//			if(circleRadius.value() > 0.0f && (level == circleLevelDisplay.target() || circleLevelDisplay.target() == 0) && level < 99) {
//				float circleR = radius * circleRadius.value();
//				float circleInnerR = circleR - lineWeight.value() / 2f;
//				if(circleR > 1) drawPolygon(p, circleR, circleInnerR, circleResolution, offsetRads, 999, 999, x, y);
//			}
			
		}
		
		p.popMatrix();
	}

	protected void preparePDFRender() {
		if(shouldRecord == true) {
			circleResolution = 300;
			p.beginRecord(P.PDF, FileUtil.getHaxademicOutputPath() + "/pdf/frame-"+SystemUtil.getTimestamp()+".pdf");
		}
	}
	
	protected void finishPDFRender() {
		if(shouldRecord == true) {
			p.endRecord();
			shouldRecord = false;
		}
		circleResolution = 60;
	}
	
}
