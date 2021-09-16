package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.SphericalCoord;
import com.haxademic.core.ui.UI;

public class Demo_VoxelSphere
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// cube layout
	protected String LIGHT_SPACING = "LIGHT_SPACING";
	protected String LIGHT_SIZE = "LIGHT_SIZE";
	protected String NUM_LIGHTS = "NUM_LIGHTS";
	protected String BOUNDING_SPHERE = "BOUNDING_SPHERE";
	protected String ROT_X = "ROT_X";
	protected String ROT_Y = "ROT_Y";
	
	// pattern controls
	protected String NUM_SEGMENTS_VERT = "PATTERN_NUM_SEGMENTS_VERT";
	protected String START_RADS_VERT = "PATTERN_START_RADS_VERT";
	protected String GAP_THRESH_VERT = "PATTERN_GAP_THRESH_VERT";

	protected String NUM_SEGMENTS_HORIZ = "PATTERN_NUM_SEGMENTS_HORIZ";
	protected String START_RADS_HORIZ = "PATTERN_START_RADS_HORIZ";
	protected String GAP_THRESH_HORIZ = "PATTERN_GAP_THRESH_HORIZ";
	
	// sphere math helpers
	protected SphericalCoord sphereCoord = new SphericalCoord();
	protected int FRAMES = (3 * 2 + 1) * 3 * 60;	// 3 configs (plus default sphere at 2 sizes, for 3 seconds @ 60fps
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 960 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.LOOP_TICKS, 3 * 2 );
		Config.setProperty( AppSettings.SHOW_UI, false );
		
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}

	protected void firstFrame() {
		UI.addTitle("LED Grid");
		UI.addSlider(LIGHT_SPACING, 80, 1, 100f, 1, false);
		UI.addSlider(LIGHT_SIZE, 18, 1, 100f, 1, false);
		UI.addSlider(NUM_LIGHTS, 15f, 5, 40, 1, false);
		UI.addSlider(BOUNDING_SPHERE, 0.97f, 0.1f, 3f, 0.01f, false);
		UI.addSlider(ROT_X, 0, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		UI.addSlider(ROT_Y, 0, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		
		UI.addTitle("Patterns");
		UI.addSlider(START_RADS_VERT, -3f * P.HALF_PI, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		UI.addSlider(NUM_SEGMENTS_VERT, 3, 2, 24, 1, false);
		UI.addSlider(GAP_THRESH_VERT, 0.3f, -1f, 1f, 0.01f, false);
		UI.addSlider(START_RADS_HORIZ, -3f * P.HALF_PI, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		UI.addSlider(NUM_SEGMENTS_HORIZ, 3, 2, 24, 1, false);
		UI.addSlider(GAP_THRESH_HORIZ, 0.3f, -1f, 1f, 0.01f, false);
	}

	protected void drawApp() {
		// set context
		p.background(0);
		p.noStroke();
		p.noFill();
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		PG.setBasicLights(p);
		p.translate(0, 0, -1000);
//		PG.basicCameraFromMouse(p.g);
		p.rotateX(UI.valueEased(ROT_X));
		p.rotateY(UI.valueEased(ROT_Y));
//		p.rotateY(FrameLoop.progressRads() * 2f);	// auto-rotate for rendering
		
		// set animation stops
		/*
		if(FrameLoop.isTick()) {
			if(FrameLoop.curTick() == 0) UI.loadValuesFromJSON(CONFIG_SPHERE_ALT);
			if(FrameLoop.curTick() == 4) UI.loadValuesFromJSON(CONFIG_1_ALT);
			if(FrameLoop.curTick() == 1) UI.loadValuesFromJSON(CONFIG_4_ALT);
			if(FrameLoop.curTick() == 5) UI.loadValuesFromJSON(CONFIG_2_ALT);
			if(FrameLoop.curTick() == 2) UI.loadValuesFromJSON(CONFIG_5_ALT);
			if(FrameLoop.curTick() == 3) UI.loadValuesFromJSON(CONFIG_6_ALT);
			if(FrameLoop.curTick() == 6) UI.loadValuesFromJSON(CONFIG_3_ALT);
//			if(FrameLoop.curTick() == 0) UI.loadValuesFromJSON(CONFIG_SPHERE);
//			if(FrameLoop.curTick() == 1) UI.loadValuesFromJSON(CONFIG_1);
//			if(FrameLoop.curTick() == 2) UI.loadValuesFromJSON(CONFIG_2);
//			if(FrameLoop.curTick() == 3) UI.loadValuesFromJSON(CONFIG_3);
//			if(FrameLoop.curTick() == 4) UI.loadValuesFromJSON(CONFIG_1_ALT);
//			if(FrameLoop.curTick() == 5) UI.loadValuesFromJSON(CONFIG_2_ALT);
//			if(FrameLoop.curTick() == 6) UI.loadValuesFromJSON(CONFIG_3_ALT);
		}
		*/
		
		// calculate sphere size
//		p.scale(sphereScale);
		
		// set sizes
		float lightSize = UI.valueEased(LIGHT_SIZE);
		float lightSpacing = UI.valueEased(LIGHT_SPACING);
		int numLights = UI.valueInt(NUM_LIGHTS);
		float cubeSize = UI.valueEased(LIGHT_SPACING) * UI.valueEased(NUM_LIGHTS);
		float cubeSizeHalf = cubeSize / 2f;
		int numLightsShown = 0;
		
		// show center sphere
//		noFill();
//		stroke(255, 50);
		p.sphereDetail(4);
//		p.sphere(cubeSizeHalf * UI.value(BOUNDING_SPHERE));
		
		// For every x,y coordinate in a 3D space, calculate a noise value and produce a brightness value
		for (float x=0; x < numLights; x++) {
			for (float y=0; y < numLights; y++) {
				for (float z=0; z < numLights; z++) {
					// position
					float curX = -cubeSizeHalf + x * lightSpacing;
					float curY = -cubeSizeHalf + y * lightSpacing;
					float curZ = -cubeSizeHalf + z * lightSpacing;
					if(numLights % 2 == 1) {	// offset if odd number of lights
						curX += lightSpacing/2f;
						curY += lightSpacing/2f;
						curZ += lightSpacing/2f;
					}
					sphereCoord.setCartesian(curX, curY, curZ);
					
					// distance to constrain to sphere
					float distFromCenter = distance(0,0,0,curX,curY,curZ);
					if(distFromCenter < cubeSizeHalf * UI.valueEased(BOUNDING_SPHERE)) { //  + halfWidth * 0.75f * P.sin(progressRads)
						// oscillate active lights in a spherical pulse
//						float distOscToggle = P.sin(FrameLoop.count(-0.1f) + distFromCenter * 0.01f);
						
						// do some polar calcs
						float radsToCenterVert = MathUtil.getRadiansToTarget(curX, curZ, 0, 0);
						float radsCircleProgress = radsToCenterVert / P.TWO_PI;
						float radialOscVert = P.sin(UI.valueEased(START_RADS_VERT) + radsToCenterVert * UI.valueEased(NUM_SEGMENTS_VERT));
//						float radialOscVert = P.sin(UI.valueEased(START_RADS_VERT) + sphereCoord.theta * UI.valueEased(NUM_SEGMENTS_VERT));
						float radsToCenterHoriz = MathUtil.getRadiansToTarget(curX, curY, 0, 0);
						float radsCircleProgressH = radsToCenterHoriz / P.TWO_PI;
//						float radialOscHoriz = P.sin(UI.valueEased(START_RADS_HORIZ) + radsToCenterHoriz * UI.valueEased(NUM_SEGMENTS_HORIZ));
						float radialOscHoriz = P.sin(UI.valueEased(START_RADS_HORIZ) + sphereCoord.phi * UI.valueEased(NUM_SEGMENTS_HORIZ));
						
						// color from dist
						float r = P.sin(curX*0.01f) * 255f + 60f;
						float g = P.sin(curY*0.01f) * 255f + 70f;
						float b = P.sin(curZ*0.01f) * 255f + 100f;
						p.fill(r, g, b);
						p.pushMatrix();
						p.translate(curX, curY, curZ);
//						if(distOscToggle < 0f) {
						if(radialOscVert < UI.valueEased(GAP_THRESH_VERT) || radialOscHoriz < UI.valueEased(GAP_THRESH_HORIZ)) {
							p.fill(100, 50);
//							p.box(lightSize/3);
						} else {
//							p.fill(P.map(distOscToggle, 0f, 1f, 0, 255));
							p.fill(255);
							p.box(lightSize);
						}
						p.popMatrix();
						numLightsShown++;
					} else {
						// inactive lights
						p.pushMatrix();
						p.translate(curX, curY, curZ);
						p.fill(100, 50);
//						p.box(lightSize / 3);
						p.popMatrix();
					}
				}
			}
		}
		DebugView.setValue("numLightsShown", numLightsShown);
		
		// post process
		BrightnessStepFilter.instance(p).setBrightnessStep(-1f/255f);
		BrightnessStepFilter.instance(p).applyTo(p.g);
		BloomFilter.instance(p).setStrength(1.5f);
		BloomFilter.instance(p).setBlurIterations(3);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance(p).applyTo(p.g);
	}
	
	protected float distance( float x1, float y1, float z1, float x2, float y2, float z2 ) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;
	    return P.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			String jsonOutput = UI.valuesToJSON(new String[] {"PATTERN_"});
			jsonOutput = UI.valuesToJSON();
			P.out(jsonOutput);
		}
		if(p.key == '1') UI.loadValuesFromJSON(CONFIG_SPHERE);
		if(p.key == '2') UI.loadValuesFromJSON(CONFIG_1);
		if(p.key == '3') UI.loadValuesFromJSON(CONFIG_1_ALT);
		if(p.key == '4') UI.loadValuesFromJSON(CONFIG_2);
		if(p.key == '5') UI.loadValuesFromJSON(CONFIG_2_ALT);
		if(p.key == '6') UI.loadValuesFromJSON(CONFIG_3);
		if(p.key == '7') UI.loadValuesFromJSON(CONFIG_3_ALT);
	}
	
	protected final String CONFIG_SPHERE = "{\r\n" + 
			"	\"LIGHT_SPACING\": 80.0,\r\n" + 
			"	\"LIGHT_SIZE\": 12.0,\r\n" + 
			"	\"NUM_LIGHTS\": 15.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": 0.0,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": -1.0,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": 1.3899996,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 6.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": -1.0\r\n" + 
			"}\r\n" + 
			"";
	protected final String CONFIG_SPHERE_ALT = "{\r\n" + 
			"	\"LIGHT_SPACING\": 42.0,\r\n" + 
			"	\"LIGHT_SIZE\": 6.0,\r\n" + 
			"	\"NUM_LIGHTS\": 30.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": 0.0,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": -1.0,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": 1.3899996,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 6.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": -1.0\r\n" + 
			"}\r\n" + 
			"";
	protected final String CONFIG_1 = "{\r\n" + 
			"	\"LIGHT_SPACING\": 80.0,\r\n" + 
			"	\"LIGHT_SIZE\": 12.0,\r\n" + 
			"	\"NUM_LIGHTS\": 15.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": -1.4099991,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": 0.14999998,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": -1.5199993,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": -0.2100001\r\n" + 
			"}";
	protected final String CONFIG_1_ALT = "{\r\n" + 
			"	\"LIGHT_SPACING\": 42.0,\r\n" + 
			"	\"LIGHT_SIZE\": 6.0,\r\n" + 
			"	\"NUM_LIGHTS\": 30.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": -1.4099991,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": 0.14999998,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": -1.5199993,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": -0.2100001\r\n" + 
			"}\r\n" + 
			"";
	protected final String CONFIG_2 = "{\r\n" + 
			"	\"LIGHT_SPACING\": 80.0,\r\n" + 
			"	\"LIGHT_SIZE\": 12.0,\r\n" + 
			"	\"NUM_LIGHTS\": 15.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": 1.4231837,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 6.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": 0.16000009,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": -1.4300014,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": 0.3199999\r\n" + 
			"}";;
	protected final String CONFIG_2_ALT = "{\r\n" + 
			"	\"LIGHT_SPACING\": 42.0,\r\n" + 
			"	\"LIGHT_SIZE\": 6.0,\r\n" + 
			"	\"NUM_LIGHTS\": 30.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": 1.4231837,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 6.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": 0.16000009,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": -1.4300014,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": 0.3199999\r\n" + 
			"}";
	protected final String CONFIG_3 = "{\r\n" + 
			"	\"LIGHT_SPACING\": 80.0,\r\n" + 
			"	\"LIGHT_SIZE\": 12.0,\r\n" + 
			"	\"NUM_LIGHTS\": 15.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": -1.4568152,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 8.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": -0.13000038,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": 1.539997,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 6.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": -0.01999961\r\n" + 
			"}";
	protected final String CONFIG_3_ALT = "{\r\n" + 
			"	\"LIGHT_SPACING\": 42.0,\r\n" + 
			"	\"LIGHT_SIZE\": 6.0,\r\n" + 
			"	\"NUM_LIGHTS\": 30.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": -1.4568152,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 8.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": -0.13000038,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": 1.539997,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 6.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": -0.01999961\r\n" + 
			"}";
	protected final String CONFIG_4_ALT = "{\r\n" + 
			"	\"LIGHT_SPACING\": 42.0,\r\n" + 
			"	\"LIGHT_SIZE\": 6.0,\r\n" + 
			"	\"NUM_LIGHTS\": 30.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": 1.4231837,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 8.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": -0.2899999,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": 1.4499977,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 6.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": 0.6499998\r\n" + 
			"}";
	protected final String CONFIG_5_ALT = "{\r\n" + 
			"	\"LIGHT_SPACING\": 42.0,\r\n" + 
			"	\"LIGHT_SIZE\": 6.0,\r\n" + 
			"	\"NUM_LIGHTS\": 30.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": 1.4231837,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 8.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": 0.3700001,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": -1.4000006,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 8.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": 0.6499998\r\n" + 
			"}\r\n" + 
			"";
	protected final String CONFIG_6_ALT = "{\r\n" + 
			"	\"LIGHT_SPACING\": 42.0,\r\n" + 
			"	\"LIGHT_SIZE\": 6.0,\r\n" + 
			"	\"NUM_LIGHTS\": 30.0,\r\n" + 
			"	\"BOUNDING_SPHERE\": 0.97,\r\n" + 
			"	\"ROT_X\": -0.36,\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": 1.4131837,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 8.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": 0.6999999,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": 1.409998,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": 0.5800004\r\n" + 
			"}";
}
