package com.haxademic.demo.draw.shapes;

import java.util.HashMap;
import java.util.Set;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.SphericalCoord;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PFont;

public class Demo_VoxelSphere2
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
	
	// animation helpers
	protected String ANIMATE_MODE = "ANIMATE_MODE";
	
	// sphere math helpers
	protected SphericalCoord sphereCoord = new SphericalCoord();
	
	// configs
	protected static HashMap<String, String> configs;
	static {
		configs = new HashMap<String, String>();
		configs.put("a", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 14.0, \"PATTERN_GAP_THRESH_VERT\": -0.5843284, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 20.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.46324384 } ");
		configs.put("b", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 22.0, \"PATTERN_GAP_THRESH_VERT\": 0.7173357, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 20.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.96176517 } ");
		configs.put("c", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 4.0, \"PATTERN_GAP_THRESH_VERT\": 0.45192134, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 22.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.5585952 } ");
		configs.put("d", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 16.0, \"PATTERN_GAP_THRESH_VERT\": 0.029999735, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 20.0, \"PATTERN_GAP_THRESH_HORIZ\": -1.0 } ");
		configs.put("e", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 6.0, \"PATTERN_GAP_THRESH_VERT\": -0.91326684, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 16.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.061478257 } ");
		configs.put("f", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 22.0, \"PATTERN_GAP_THRESH_VERT\": -0.14026618, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 20.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.4583429 } ");
		configs.put("g", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 16.0, \"PATTERN_GAP_THRESH_VERT\": -0.7391555, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 18.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.14310849 } ");
		configs.put("h", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 8.0, \"PATTERN_GAP_THRESH_VERT\": 0.5923357, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 16.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.5397266 } ");
		configs.put("i", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 18.0, \"PATTERN_GAP_THRESH_VERT\": -0.39235595, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 14.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.26771063 } ");
		configs.put("j", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 16.0, \"PATTERN_GAP_THRESH_VERT\": -0.2991562, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 20.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.36995012 } ");
		configs.put("k", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 16.0, \"PATTERN_GAP_THRESH_VERT\": -0.70216703, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 14.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.61245894 } ");
		configs.put("l", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 8.0, \"PATTERN_GAP_THRESH_VERT\": -0.02406156, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 20.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.17982471 } ");
		configs.put("m", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 22.0, \"PATTERN_GAP_THRESH_VERT\": -0.8154269, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 14.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.036954403 } ");
		configs.put("n", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 6.0, \"PATTERN_GAP_THRESH_VERT\": 0.6526089, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 12.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.8325277 } ");
		configs.put("o", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 12.0, \"PATTERN_GAP_THRESH_VERT\": 0.7170665, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 16.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.102694035 } ");
		configs.put("p", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 16.0, \"PATTERN_GAP_THRESH_VERT\": 0.22329724, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 4.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.7296017 } ");
		configs.put("q", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 14.0, \"PATTERN_GAP_THRESH_VERT\": 0.13128905, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 18.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.63811255 } ");
		configs.put("r", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 12.0, \"PATTERN_GAP_THRESH_VERT\": 0.46750873, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 12.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.5228157 } ");
		configs.put("s", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 14.0, \"PATTERN_GAP_THRESH_VERT\": -0.8393265, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 22.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.8369159 } ");
		configs.put("t", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 4.0, \"PATTERN_GAP_THRESH_VERT\": 0.2787292, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 8.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.54687464 } ");
		configs.put("u", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 12.0, \"PATTERN_GAP_THRESH_VERT\": -0.19053984, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 22.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.6054472 } ");
		configs.put("v", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 6.0, \"PATTERN_GAP_THRESH_VERT\": 0.85479033, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 14.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.6111987 } ");
		configs.put("w", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 6.0, \"PATTERN_GAP_THRESH_VERT\": 0.80701435, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 12.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.54716444 } ");
		configs.put("x", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 4.0, \"PATTERN_GAP_THRESH_VERT\": 0.49174082, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 4.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.07977557 } ");
		configs.put("y", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 10.0, \"PATTERN_GAP_THRESH_VERT\": -0.31146777, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 12.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.8188895 } ");
		configs.put("z", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 14.0, \"PATTERN_GAP_THRESH_VERT\": 0.25727415, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 14.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.9009448 } ");
		configs.put(" ", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 16.0, \"PATTERN_GAP_THRESH_VERT\": 1.0, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 14.0, \"PATTERN_GAP_THRESH_HORIZ\": 1.0 } ");

//		configs = new HashMap<String, String>();
//		configs.put("w", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 6.0, \"PATTERN_GAP_THRESH_VERT\": 0.80701435, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 12.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.54716444 } ");
//		configs.put("a", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 14.0, \"PATTERN_GAP_THRESH_VERT\": -0.5843284, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 20.0, \"PATTERN_GAP_THRESH_HORIZ\": 0.46324384 } ");
//		configs.put("t", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 4.0, \"PATTERN_GAP_THRESH_VERT\": 0.2787292, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 8.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.54687464 } ");
//		configs.put("e", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 6.0, \"PATTERN_GAP_THRESH_VERT\": -0.91326684, \"PATTERN_START_RADS_HORIZ\": -4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 16.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.061478257 } ");
//		configs.put("r", "{ \"PATTERN_START_RADS_VERT\": -4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 6.0, \"PATTERN_GAP_THRESH_VERT\": -0.86276597, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 18.0, \"PATTERN_GAP_THRESH_HORIZ\": -0.803981 } ");
//		configs.put(" ", "{ \"PATTERN_START_RADS_VERT\": 4.712389, \"PATTERN_NUM_SEGMENTS_VERT\": 16.0, \"PATTERN_GAP_THRESH_VERT\": 1.0, \"PATTERN_START_RADS_HORIZ\": 4.712389, \"PATTERN_NUM_SEGMENTS_HORIZ\": 14.0, \"PATTERN_GAP_THRESH_HORIZ\": 1.0 } ");
	}
	protected String[] letterKeys;
	protected String curLetter = "";
	protected int numLetters = configs.keySet().size();

	protected static HashMap<String, String> vowelColors;
	static {
		vowelColors = new HashMap<String, String>();
		vowelColors.put("a", "#C724B1");
		vowelColors.put("e", "#FF7F41");
		vowelColors.put("i", "#B5BD00");
		vowelColors.put("o", "#59CBE8");
		vowelColors.put("u", "#8B84D7");
	}
	protected String[] otherColors = new String[] {
			"#C5B4E3",
			"#E2ACD7",
			"#046A38",
			"#86C8BC",
	};
	protected EasingColor globalColor = new EasingColor(0xffffffff);

	protected int FRAMES = numLetters * 3 * 60;	// 3 configs (plus default sphere at 2 sizes, for 3 seconds @ 60fps
//	protected int FRAMES = numLetters * 45;	// 3 configs (plus default sphere at 2 sizes, for 3 seconds @ 60fps

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 960 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.LOOP_TICKS, numLetters );
		Config.setProperty( AppSettings.SHOW_UI, false );
		
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}

	protected void firstFrame() {
		UI.addTitle("LED Grid");
		UI.addSlider(LIGHT_SPACING, 80, 1, 100f, 1, false);
		UI.addSlider(LIGHT_SIZE, 18, 1, 100f, 1, false);
		UI.addSlider(NUM_LIGHTS, 14, 5, 40, 1, false);
		UI.addSlider(BOUNDING_SPHERE, 0.97f, 0.1f, 3f, 0.01f, false);
		UI.addSlider(ROT_X, 0, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		UI.addSlider(ROT_Y, 0, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		
		UI.addTitle("Patterns");
		UI.addSlider(START_RADS_VERT, -3f * P.HALF_PI, -3f * P.HALF_PI, 3f * P.HALF_PI, 6f * P.HALF_PI, false);
		UI.addSlider(NUM_SEGMENTS_VERT, 4, 2, 24, 1, false);
		UI.addSlider(GAP_THRESH_VERT, 0.3f, -1f, 1f, 0.01f, false);
		UI.addSlider(START_RADS_HORIZ, -3f * P.HALF_PI, -3f * P.HALF_PI, 3f * P.HALF_PI, 6f * P.HALF_PI, false);
		UI.addSlider(NUM_SEGMENTS_HORIZ, 4, 2, 24, 1, false);
		UI.addSlider(GAP_THRESH_HORIZ, 0.3f, -1f, 1f, 0.01f, false);

		UI.addSlider(ANIMATE_MODE, 1, 0, 1, 1, false);
		
		// build alphabet keys array
		Set<String> keys = configs.keySet();
		letterKeys = new String[keys.size()];
		int index = 0;
		for(String element : keys) letterKeys[index++] = element;
		
		// speicifc word animation-only
//		letterKeys[0] = "w";
//		letterKeys[1] = "a";
//		letterKeys[2] = "t";
//		letterKeys[3] = "e";
//		letterKeys[4] = "r";
//		letterKeys[5] = " ";
	}

	protected void drawApp() {
		// set context
		p.background(0);
		p.noStroke();
		p.noFill();
		p.push();
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		PG.setBasicLights(p);
		p.translate(0, 0, -1000);
//		PG.basicCameraFromMouse(p.g);
		p.rotateX(UI.valueEased(ROT_X));
		p.rotateY(UI.valueEased(ROT_Y));

		p.rotateX(-0.2f + 0.15f * P.sin(2f * FrameLoop.progressRads() * numLetters));	// auto-rotate for rendering
		p.rotateY(0.7f * P.sin(FrameLoop.progressRads() * numLetters));	// auto-rotate for rendering
//		p.rotateX(-0.2f + 0.15f * P.sin(2f * FrameLoop.progressRads()));	// auto-rotate for rendering
//		p.rotateY(0.7f * P.sin(FrameLoop.progressRads()));	// auto-rotate for rendering
		
		// set animation stops
		if(FrameLoop.isTick() && UI.valueInt(ANIMATE_MODE) == 1) {
			// get sphere config
			curLetter = letterKeys[FrameLoop.curTick()];
			if(configs.containsKey(curLetter)) {
				UI.loadValuesFromJSON(configs.get(curLetter));
			}
//			setColorForLetter(curLetter);
		}
		
		// update color
		globalColor.update();
		
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
//				for (float z=numLights/2; z < numLights; z++) {		// only half a sphere
				for (float z=0; z < numLights; z++) {		// only half a sphere
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
							p.fill(globalColor.colorInt());
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
		BrightnessStepFilter.instance().setBrightnessStep(-1f/255f);
		BrightnessStepFilter.instance().applyTo(p.g);
		BloomFilter.instance().setStrength(1.5f);
		BloomFilter.instance().setBlurIterations(3);
		BloomFilter.instance().setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance().applyTo(p.g);
		
		// draw current letter
		p.pop();
		PG.setDrawCorner(p.g);
		PFont font = FontCacher.getFont(DemoAssets.fontInterPath, 78);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(curLetter.toUpperCase(), 50, 50);
	}
	
	protected void setColorForLetter(String curLetter) {
		// set color
		if(vowelColors.containsKey(curLetter)) {
			globalColor.setTargetHex(vowelColors.get(curLetter));
			P.out(curLetter, vowelColors.get(curLetter));
		} else {
			globalColor.setTargetInt(0xffffffff);
		}
	}
	
	protected float distance( float x1, float y1, float z1, float x2, float y2, float z2 ) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;
	    return P.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public void randomValues() {
		// rads start
		UI.setValue(START_RADS_VERT, MathUtil.randBoolean() ? -3f * P.HALF_PI : 3f * P.HALF_PI);
		UI.setValue(START_RADS_HORIZ, MathUtil.randBoolean() ? -3f * P.HALF_PI : 3f * P.HALF_PI);
		// num segments
		UI.setValue(NUM_SEGMENTS_VERT, 2 + 2 * MathUtil.randRange(0, 10));
		UI.setValue(NUM_SEGMENTS_HORIZ, 2 + 2 * MathUtil.randRange(0, 10));
		// density
		UI.setRandomValue(GAP_THRESH_VERT);
		UI.setRandomValue(GAP_THRESH_HORIZ);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			String jsonOutput = UI.valuesToJSON(new String[] {"PATTERN_"}, true);
//			jsonOutput = UI.valuesToJSON();
			P.out(JsonUtil.jsonToSingleLine(jsonOutput));
		}
		// random params
		if(p.key == '1') randomValues();
		// load letter
		String keyStr = key+"";
		if(configs.containsKey(keyStr) && key != ' ') {
			UI.loadValuesFromJSON(configs.get(keyStr));
			setColorForLetter(keyStr);
		}
	}
	
}
