package com.haxademic.sketch.render;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PShape;

public class Hearts 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 180;
	PShape heartShape;
	float baseScale = 1f;
	PGraphics heartMap;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames + 1 );
	}

	public void setup() {
		super.setup();
		noStroke();
		OpenGLUtil.setQuality(p.g, OpenGLUtil.HIGH);
		heartShape = p.loadShape(FileUtil.getFile("svg/heart.svg"));
		baseScale = p.height * 0.03f / heartShape.height;
		
		// create map
		heartMap = p.createGraphics(p.width, p.height);
	}

	public void drawApp() {
		p.background(0);
		DrawUtil.setDrawCenter(p);
		
		// get progress
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete % 1, 0, 1, 1);
		float percentCompleteRadians = percentComplete * P.TWO_PI;

		// draw heart map
		heartMap.beginDraw();
		float heartMapScale = heartMap.height * 0.75f / heartShape.height;
		heartMapScale *= 0.7f + 0.3f * P.sin(P.PI/4f + percentCompleteRadians);
		DrawUtil.setDrawCenter(heartMap);
		heartShape.disableStyle();
		heartMap.fill(0);
		heartMap.background(255);
		heartMap.translate(heartMap.width/2, heartMap.height * 0.53f);
		heartMap.shape(heartShape, 0, 0, heartShape.width * heartMapScale, heartShape.height * heartMapScale);
		heartMap.endDraw();

		
		
		
		heartShape.disableStyle();
		p.fill(255, 0, 0);
		
		float heartW = heartShape.width * baseScale;
		float spacingW = heartW * 1.12f;
		float heartH = heartShape.height * baseScale;
		float spacingH = heartH * 0.85f;
		int row = 0;
		float xOffset = 0;
		float yOffset = 0;
		
		for (float y = -heartShape.height; y < p.height + heartShape.height; y+=spacingH) {
			row++;
			if(row % 2 == 0) {
				xOffset += spacingW * 0.5f;
				yOffset = 0;
			} else {
				xOffset += spacingW * 0.23f;
				yOffset = spacingH * 0.12f;
			}
			for (float x = -p.width * 2; x < p.width + heartShape.width; x+=spacingW) {
				float curX = x + xOffset;
				float curY = y + yOffset;
				
				// get distance 
				float dist = MathUtil.getDistance(p.width/2, p.height/2, curX, curY);
				float scaleAdd = 0.5f + 0.3f * P.sin(dist/100f - percentCompleteRadians);
				
				// set color from map
				int pixelColor = ImageUtil.getPixelColor(heartMap, P.round(curX), P.round(curY));
//				P.println(pixelColor);
				if(pixelColor == ImageUtil.BLACK_INT) {
					p.fill(220, 0, 0);
				} else {
					p.fill(127, 0, 0);
				}
				
				// position & draw small hearts
				p.pushMatrix();
				p.translate(curX, curY);
//				p.translate(x, 0);
				if(row % 2 == 0) {
					p.rotate(P.PI);
				}
				p.shape(heartShape, 0, 0, heartW * scaleAdd, heartH * scaleAdd);
				p.popMatrix();
			}
		}

//		p.shape(heartShape, 0, 0);
		VignetteFilter.instance(p).applyTo(p);
		
//		DrawUtil.setDrawCorner(p);
//		p.image(heartMap, 0, 0);
	}
		
}
