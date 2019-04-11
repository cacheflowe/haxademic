package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;

import processing.core.PImage;

public class Demo_SpatialLighting 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String R = "R";
	protected String G = "G";
	protected String B = "B";
	
	protected String ROTATION = "ROTATION";

	protected String LIGHT_POS_1 = "LIGHT_POS_1";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true);
	}
	
	public void setupFirstFrame () {
		p.prefsSliders.addSlider(R, 255, 0, 255, 0.5f);
		p.prefsSliders.addSlider(G, 255, 0, 255, 0.5f);
		p.prefsSliders.addSlider(B, 255, 0, 255, 0.5f);
		p.prefsSliders.addSliderVector(ROTATION, 0, -1f, 1f, 0.001f, false);
		p.prefsSliders.addSliderVector(LIGHT_POS_1, 0, -1f, 1f, 0.001f, false);
		P.out(p.prefsSliders.toJSON());
	}
	
	public void drawApp() {
		// bg components
		p.background(
			p.prefsSliders.value(R),
			p.prefsSliders.value(G),
			p.prefsSliders.value(B)
		);
		
		// 3d rotation
		// p.ortho();
		p.perspective();
		p.lights();
		DrawUtil.setCenterScreen(p.g);
		DrawUtil.setDrawCenter(p.g);
		p.translate(0, 0, p.prefsSliders.value(ROTATION + "_Z") * p.width);
		p.rotateX(p.prefsSliders.value(ROTATION + "_X") * P.TWO_PI);
		p.rotateY(p.prefsSliders.value(ROTATION + "_Y") * P.TWO_PI);
		
		// room size
		float roomW = 500;
		float roomH = 200;
		float roomD = 300;
		
		// draw bounding box
		p.noFill();
		p.stroke(255);
		Shapes.drawDashedBox(p.g, roomW, roomH, roomD, 20, true);
		
		// draw floor plan
		p.noStroke();
		PImage floorplan = DemoAssets.textureNebula();
		DrawUtil.push(p.g);
		p.translate(0, roomH / 2f, 0);
		p.rotateX(P.HALF_PI);
		p.image(floorplan, 0, 0, roomW, roomD);
//		Shapes.drawTexturedRect(p.g, floorplan);
		DrawUtil.pop(p.g);
		
		// draw lights
		DrawUtil.setDrawCorner(p.g);
		float lightX = roomW * p.prefsSliders.value(LIGHT_POS_1 + "_X");
		float lightY = roomH * p.prefsSliders.value(LIGHT_POS_1 + "_Y");
		float lightZ = roomD * p.prefsSliders.value(LIGHT_POS_1 + "_Z");
		DrawUtil.push(p.g);
		p.fill(colorFromPosition(lightX, lightY, lightZ));
		p.translate(lightX, lightY, lightZ);
		p.sphere(20);
		DrawUtil.pop(p.g);
		
		// 3d noise
		p.noStroke();
		float noiseSpacing = 33.33f;
		for (int x = 0; x <= roomW; x += noiseSpacing) {
			for (int y = 0; y <= roomH; y += noiseSpacing) {
				for (int z = 0; z <= roomD; z += noiseSpacing) {
					float gridX = -roomW/2f + x;
					float gridY = -roomH/2f + y;
					float gridZ = -roomD/2f + z;
					p.fill(colorFromPosition(gridX, gridY, gridZ));
					p.pushMatrix();
					p.translate(gridX, gridY, gridZ);
					p.box(3);
					p.popMatrix();
				}	
			}
		}
	}
	
	protected int colorFromPosition(float x, float y, float z) {
		float noiseAmp = 0.005f;
		float noiseOffset = p.frameCount * 0.01f;
		return p.color(
			0 + 255f * p.noise(x * noiseAmp + noiseOffset), 
			0 + 255f * p.noise(y * noiseAmp + noiseOffset), 
			0 + 255f * p.noise(z * noiseAmp + noiseOffset)
		);
	}
	
}
