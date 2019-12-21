package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PShapeTypes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.video.Movie;

public class Demo_Shapes_halfCircleUV
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie video;
	protected PGraphics testPattern;
	
	protected String displayW = "displayW";
	protected String displayH = "displayH";
	protected String offsetX = "offset";
	protected String webcam = "webcam";
	protected String detailVal = "detail";
	protected String showLines = "showLines";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.ALWAYS_ON_TOP, false );
	}

	protected void setupFirstFrame() {
		// load video
		video = DemoAssets.movieFractalCube();
		video.loop();
		
		testPattern = PG.newPG(pg.width, pg.height);
		testPattern.beginDraw();
		PG.drawTestPattern(testPattern);
		testPattern.endDraw();
		
		// set UI
		UI.addSlider(displayW, p.width, 0, 2000, 1f, false);
		UI.addSlider(displayH, 960, 0, 2000, 1f, false);
		UI.addSlider(offsetX, 0, 0, 10, 0.01f, false);
		UI.addSlider(webcam, 0, 0, 1, 1, false);
		UI.addSlider(showLines, 0, 0, 1, 1, false);
		UI.addSlider(detailVal, 500, 10, 1000, 1, false);
	}

	public void drawApp() {
		p.background(0);
		p.push();
		
		// set debug colors
		p.stroke(0, 255, 0);
		p.strokeWeight(1);
		p.fill(255);
		if(UI.valueInt(showLines) == 0) p.noStroke();
		
		// set shape size
		float shapeW = UI.value(displayW);
		float shapeH = UI.value(displayH);
		float detail = UI.value(detailVal);
		float shapeOriginX = shapeW / 2f;
		float shapeOriginY = shapeH;
		float startU = UI.value(offsetX);
		
		// draw half-circle & apply texture
		PG.setTextureRepeat(p.g, true);
		p.translate(shapeOriginX, shapeOriginY);
		p.beginShape(PShapeTypes.TRIANGLES);
		p.textureMode(P.NORMAL);
		p.texture(video);
//		p.texture(testPattern);
		if(UI.valueInt(webcam) == 1) p.texture(WebCam.instance().image());
		float startRads = P.PI;
		float segmentRads = P.PI / detail;
		for (int i = 0; i < detail; i++) {
			float x = P.cos(startRads + i * segmentRads);
			float y = P.sin(startRads + i * segmentRads);
			float xNext = P.cos(startRads + (i+1) * segmentRads);
			float yNext = P.sin(startRads + (i+1) * segmentRads);
			float progressX = (float)i / detail;
			float progressXNext = (float)(i+1) / detail;
			float u = startU + progressX;	// texture wraps around half the circle instead of full circle, so speed up UV by multiplying by 2
			float uNext = startU + progressXNext;
			p.vertex(x * shapeW/2, y * shapeH, 0, u, 0);
			p.vertex(xNext * shapeW/2, yNext * shapeH, 0, uNext, 0);
			p.vertex(0, 0, 0, u, 1);
		}
		p.endShape();
		
		p.pop();
		
//		p.background(255,255,0);
	}

}
