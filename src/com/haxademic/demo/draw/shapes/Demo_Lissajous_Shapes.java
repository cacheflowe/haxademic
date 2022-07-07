package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.camera.CameraUtil;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.OrientationUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;

public class Demo_Lissajous_Shapes
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String FREQ_3 = "FREQ_3";
	protected String ROT_3 = "ROT_3";
	protected String START_RADS_SPEED = "START_RADS_SPEED";
	protected String BOX_SCALE = "BOX_SCALE";
	protected String SHAPE_SCALE = "SHAPE_SCALE";
	protected String SCALE_OSC_AMP = "SCALE_OSC_AMP";
	protected String SCALE_OSC_FREQ = "SCALE_OSC_FREQ";
	
	protected float startRads = 0;
	protected PVector center = new PVector();
	protected PVector util = new PVector();
	
	protected String[] CONFIG_SAVED = new String[] {
			"{ \"START_RADS_SPEED\": 0.003999999, \"BOX_SCALE\": 15.0, \"SHAPE_SCALE\": 184.0, \"SCALE_OSC_AMP\": 0.7199998, \"SCALE_OSC_FREQ\": 3.0, \"FREQ_3_X\": 2.0, \"FREQ_3_Y\": 2.0, \"FREQ_3_Z\": 1.0, \"ROT_3_X\": 0.0, \"ROT_3_Y\": 0.0, \"ROT_3_Z\": 0.0 }",
			"{ \"START_RADS_SPEED\": 0.003999999, \"BOX_SCALE\": 32.0, \"SHAPE_SCALE\": 169.0, \"SCALE_OSC_AMP\": 0.32999998, \"SCALE_OSC_FREQ\": 1.0, \"FREQ_3_X\": 2.0, \"FREQ_3_Y\": 9.0, \"FREQ_3_Z\": 9.0, \"ROT_3_X\": 0.0, \"ROT_3_Y\": 0.0, \"ROT_3_Z\": 0.0 }",
			"{ \"START_RADS_SPEED\": 0.012999998, \"BOX_SCALE\": 20.0, \"SHAPE_SCALE\": 130.0, \"SCALE_OSC_AMP\": 0.95999956, \"SCALE_OSC_FREQ\": 4.0, \"FREQ_3_X\": 2.0, \"FREQ_3_Y\": 9.0, \"FREQ_3_Z\": 10.0, \"ROT_3_X\": 0.0, \"ROT_3_Y\": 0.0, \"ROT_3_Z\": 0.0 }",
			"{ \"START_RADS_SPEED\": 0.012999998, \"BOX_SCALE\": 8.0, \"SHAPE_SCALE\": 202.0, \"SCALE_OSC_AMP\": 0.0, \"SCALE_OSC_FREQ\": 4.0, \"FREQ_3_X\": 5.0, \"FREQ_3_Y\": 9.0, \"FREQ_3_Z\": 8.0, \"ROT_3_X\": 0.0, \"ROT_3_Y\": 0.0, \"ROT_3_Z\": 0.0 }",
			"{ \"START_RADS_SPEED\": 0.012999998, \"BOX_SCALE\": 53.0, \"SHAPE_SCALE\": 202.0, \"SCALE_OSC_AMP\": 0.26999998, \"SCALE_OSC_FREQ\": 4.0, \"FREQ_3_X\": 12.0, \"FREQ_3_Y\": 6.0, \"FREQ_3_Z\": 6.0, \"ROT_3_X\": 0.0, \"ROT_3_Y\": 0.0, \"ROT_3_Z\": 0.0 }",
			"{ \"START_RADS_SPEED\": 0.027, \"BOX_SCALE\": 26.0, \"SHAPE_SCALE\": 202.0, \"SCALE_OSC_AMP\": 0.26999998, \"SCALE_OSC_FREQ\": 4.0, \"FREQ_3_X\": 10.0, \"FREQ_3_Y\": 6.0, \"FREQ_3_Z\": 8.0, \"ROT_3_X\": 0.0, \"ROT_3_Y\": 0.0, \"ROT_3_Z\": 0.0 }",
			"{ \"START_RADS_SPEED\": 0.0050000004, \"BOX_SCALE\": 17.0, \"SHAPE_SCALE\": 178.0, \"SCALE_OSC_AMP\": 0.3100004, \"SCALE_OSC_FREQ\": 6.0, \"FREQ_3_X\": 8.0, \"FREQ_3_Y\": 4.0, \"FREQ_3_Z\": 3.0, \"ROT_3_X\": 0.0, \"ROT_3_Y\": 0.0, \"ROT_3_Z\": 0.0 }",
			"{ \"START_RADS_SPEED\": 0.017, \"BOX_SCALE\": 44.0, \"SHAPE_SCALE\": 202.0, \"SCALE_OSC_AMP\": 0.29999998, \"SCALE_OSC_FREQ\": 2.0, \"FREQ_3_X\": 5.0, \"FREQ_3_Y\": 2.0, \"FREQ_3_Z\": 1.0, \"ROT_3_X\": 0.0, \"ROT_3_Y\": 0.0, \"ROT_3_Z\": 0.0 }",
			"{ \"START_RADS_SPEED\": 0.007999999, \"BOX_SCALE\": 11.0, \"SHAPE_SCALE\": 157.0, \"SCALE_OSC_AMP\": 0.6299999, \"SCALE_OSC_FREQ\": 4.0, \"FREQ_3_X\": 5.0, \"FREQ_3_Y\": 2.0, \"FREQ_3_Z\": 4.0, \"ROT_3_X\": 0.0, \"ROT_3_Y\": 0.0, \"ROT_3_Z\": 0.0 }",
	};
	
	protected PGraphics boxTex;

	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}
	
	protected void firstFrame() {
		UI.addTitle("SHAPE");
		UI.addSlider(START_RADS_SPEED, 0.01f, 0, 0.1f, 0.001f, false);
		UI.addSlider(BOX_SCALE, 20, 2, 200, 1, false);
		UI.addSlider(SHAPE_SCALE, p.width * 0.25f, 2, p.width, 1, false);
		UI.addSlider(SCALE_OSC_AMP, 0.3f, 0, 1f, 0.01f, false);
		UI.addSlider(SCALE_OSC_FREQ, 1, 0, 10f, 1, false);
		UI.addSliderVector(FREQ_3, 1, 1, 36, 1, false);
		UI.addSliderVector(ROT_3, 0, -1f, 1f, 0.01f, false);
		
		boxTex = PG.newPG(512, 512);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') P.out(JsonUtil.jsonToSingleLine(UI.valuesToJSON()));
		if(p.key == '2') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED[0]));
		if(p.key == '3') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED[1]));
		if(p.key == '4') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED[2]));
		if(p.key == '5') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED[3]));
		if(p.key == '6') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED[4]));
		if(p.key == '7') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED[5]));
		if(p.key == '8') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED[6]));
		if(p.key == '9') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED[7]));
		if(p.key == '0') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED[8]));
	}
	
	protected void drawApp() {
		// set context
		p.background(0);
		p.noStroke();
		p.sphereDetail(6);
		PG.setBetterLights(p);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g, 0.4f);

		// rotate camera
		p.rotateX(UI.valueXEased(ROT_3));
		p.rotateY(UI.valueYEased(ROT_3));
		p.rotateZ(UI.valueZEased(ROT_3));
		
		// update box texture
		boxTex.beginDraw();
		boxTex.background(0);
		PG.drawGrid(boxTex, 0x00000000, 0xff222222, 20, 20, 4, false);
		boxTex.endDraw();
		
		// draw box based on user position
		CameraUtil.setCameraDistance(p.g, 100, 20000);
		p.push();
		PImage tex = boxTex;
		Shapes.drawTexturedCubeInside(
				p.g, p.width * 2, p.height* 2, p.height * 4, 
				tex, tex, tex, tex, tex, tex);
		p.pop();

		
		// draw origin axis
		PG.drawOriginAxis(p.g);
		
		// draw shapes
		startRads += UI.value(START_RADS_SPEED);
		float numShapes = 300;
		float rads = 1f / numShapes * P.TWO_PI;
		float boxScale = UI.valueEased(BOX_SCALE);
		float shapeScale = UI.valueEased(SHAPE_SCALE);
		float scaleOscAmp = UI.valueEased(SCALE_OSC_AMP);
		float scaleOscFreq = UI.valueEased(SCALE_OSC_FREQ);
		for (int i = 0; i < numShapes; i++) {
			float progress = (float) i / numShapes;
			float progressOsc = P.sin(progress * scaleOscFreq * P.TWO_PI);
			float radsX = P.cos(startRads + rads * UI.valueXEased(FREQ_3) * i);
			float radsY = P.sin(startRads + rads * UI.valueYEased(FREQ_3) * i);
			float radsZ = P.cos(startRads + rads * UI.valueZEased(FREQ_3) * i);
			float x = radsX * (shapeScale * (1f + scaleOscAmp * P.sin(progressOsc)));
			float y = radsY * (shapeScale * (1f + scaleOscAmp * P.sin(progressOsc)));
			float z = radsZ * (shapeScale * (1f + scaleOscAmp * P.sin(progressOsc)));
			
			p.push();
			p.fill(ColorsHax.colorFromGroupAt(11, i));
			p.translate(x, y, z);
			util.set(x, y, z);
			OrientationUtil.setRotationTowards2(p.g, util, center);
//			p.rotateZ(radsZ);
//			p.rotate(-radsY);
//			p.rotateY(-radsY);
//			p.sphere(boxScale);
			p.box(boxScale/2, boxScale/2, boxScale);
			p.pop();
		}
	}

}
