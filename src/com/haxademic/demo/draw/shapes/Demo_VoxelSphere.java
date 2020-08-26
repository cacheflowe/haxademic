package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.SystemUtil;
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
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 960 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
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
		UI.addSlider(START_RADS_VERT, 0, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		UI.addSlider(NUM_SEGMENTS_VERT, 3, 2, 10, 1, false);
		UI.addSlider(GAP_THRESH_VERT, 0.3f, -1f, 1f, 0.01f, false);
		UI.addSlider(START_RADS_HORIZ, 0, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		UI.addSlider(NUM_SEGMENTS_HORIZ, 3, 2, 10, 1, false);
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
		p.rotateY(FrameLoop.count(0.01f));

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
					float curX = -cubeSizeHalf + x * lightSpacing + lightSpacing/2f;
					float curY = -cubeSizeHalf + y * lightSpacing + lightSpacing/2f;
					float curZ = -cubeSizeHalf + z * lightSpacing + lightSpacing/2f;
					// distance to constrain to sphere
					float distFromCenter = distance(0,0,0,curX,curY,curZ);
					if(distFromCenter < cubeSizeHalf * UI.valueEased(BOUNDING_SPHERE)) { //  + halfWidth * 0.75f * P.sin(progressRads)
						// oscillate active lights in a spherical pulse
//						float distOscToggle = P.sin(FrameLoop.count(-0.1f) + distFromCenter * 0.01f);
						
						// do some polar calcs
						float radsToCenterVert = MathUtil.getRadiansToTarget(curX, curZ, 0, 0);
						float radsCircleProgress = radsToCenterVert / P.TWO_PI;
						float radialOscVert = P.sin(UI.valueEased(START_RADS_VERT) + radsToCenterVert * UI.valueEased(NUM_SEGMENTS_VERT));
						float radsToCenterHoriz = MathUtil.getRadiansToTarget(curX, curY, 0, 0);
						float radsCircleProgressH = radsToCenterHoriz / P.TWO_PI;
						float radialOscHoriz = P.sin(UI.valueEased(START_RADS_HORIZ) + radsToCenterHoriz * UI.valueEased(NUM_SEGMENTS_HORIZ));
						
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
			P.out(jsonOutput);
		}
		if(p.key == '1') UI.loadValuesFromJSON(CONFIG_1);
		if(p.key == '2') UI.loadValuesFromJSON(CONFIG_2);
		if(p.key == '3') UI.loadValuesFromJSON(CONFIG_3);
	}
	
	protected final String CONFIG_1 = "{\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": 0.0,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": 0.3,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": 1.3899996,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 6.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": 0.059999853\r\n" + 
			"}";
	protected final String CONFIG_2 = "{\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": 6.2831855,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 8.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": 0.07000005,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": -1.6700011,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": -0.31000006\r\n" + 
			"}";
	protected final String CONFIG_3 = "{\r\n" + 
			"	\"PATTERN_START_RADS_VERT\": 6.2831855,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_VERT\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_VERT\": 0.6000004,\r\n" + 
			"	\"PATTERN_START_RADS_HORIZ\": -1.6700011,\r\n" + 
			"	\"PATTERN_NUM_SEGMENTS_HORIZ\": 4.0,\r\n" + 
			"	\"PATTERN_GAP_THRESH_HORIZ\": -0.24000008\r\n" + 
			"}";
}
