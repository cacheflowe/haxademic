package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PShapeTypes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;

import processing.core.PGraphics;

public class DazzleTubes 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics dazzleBuff;
	
	protected void overridePropsFile() {
		int FRAMES = 300;
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1024);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 1);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void setupFirstFrame() {
		// create rainbow buffer source
		dazzleBuff = p.createGraphics(640, 2048, P.P3D);
	}
	
	protected void updateStripes() {
		dazzleBuff.beginDraw();
		dazzleBuff.clear();
		dazzleBuff.background(0, 0);
		dazzleBuff.noStroke();
		float stripes = 120 + 10f * P.sin(p.loop.progressRads());
		float stripeH = dazzleBuff.height / stripes;
		float scrollSpeed = 16f;
		float scrollAmp = p.loop.progress() * scrollSpeed;
		float startY = (-stripeH * scrollSpeed) + scrollAmp * stripeH * 2f; // *2 because stripes
		float indx = 0;
		for (float y = startY; y < dazzleBuff.height; y += stripeH) {
			dazzleBuff.fill((indx % 2 == 0) ? p.color(255) : p.color(0));
			dazzleBuff.rect(0, y, dazzleBuff.width, stripeH);
			indx++;
		}
		dazzleBuff.endDraw();
		
		DebugView.setTexture("dazzleBuff", dazzleBuff);
	}
	
	public void drawApp() {
		// pre draw
		updateStripes();
		
		// set up context
		background(0);
		p.noStroke();
//		PG.setDrawCenter(p.g);
		PG.setBetterLights(p.g);
//		p.lights();
		
		// draw shape
		PG.setCenterScreen(p.g);
//		PG.basicCameraFromMouse(p.g);
		p.rotateX(P.sin(p.loop.progressRads()) * 0.3f);
		p.rotateY(-p.loop.progressRads());
		
		// draw tube
		drawTube(36, 200, 200, p.height * 1.3f);
		
		// draw sheet
//		Shapes.drawTexturedRect(p.g, dazzleBuff);
		
		// post processing
//		BrightnessFilter.instance(p).setBrightness(1.35f);
//		BrightnessFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).setDarkness(0.85f);
		VignetteFilter.instance(p).applyTo(p);
	}
	
	protected void drawTube(float resCircumference, float resHeight, float radius, float height) {
		// calc positions
		float segmentRads = P.TWO_PI / resCircumference;
		float segmentH = height / resHeight;
		float halfH = height / 2f;
		float startY = -halfH;

		p.noFill();
//		p.stroke(255);
		
		p.beginShape(PShapeTypes.TRIANGLES);
		p.texture(dazzleBuff);
		p.textureMode(P.NORMAL);
		PG.setTextureRepeat(p.g, true);
		
		for (float y=startY; y < halfH; y += segmentH) {
			for (float i=0; i < resCircumference; i++) {
				// next y helper
				float yNext = y + segmentH;
				
				// helpers
				float progressCirc = i / resCircumference;
				float progressCircNext = (i+1f) / resCircumference;
				float tubeRadiusOscAmp = 0.6f;
				float tubeRadiusOscFreq = 0.0085f;
				float radiusCur = radius * (1f + tubeRadiusOscAmp * P.sin(p.loop.progressRads() * 2f + y * tubeRadiusOscFreq));
				float radiusNext = radius * (1f + tubeRadiusOscAmp * P.sin(p.loop.progressRads() * 2f + yNext * tubeRadiusOscFreq));
				float curRads = i * segmentRads;
				float nextRads = (i+1) * segmentRads;
				
				// position
				float x = P.cos(curRads) * radiusCur;
				float z = P.sin(curRads) * radiusCur;
				float xNext = P.cos(nextRads) * radiusCur;
				float zNext = P.sin(nextRads) * radiusCur;
				float xNextRow = P.cos(curRads) * radiusNext;
				float zNextRow = P.sin(curRads) * radiusNext;
				float xNextNextRow = P.cos(nextRads) * radiusNext;
				float zNextNextRow = P.sin(nextRads) * radiusNext;
				
				// uv coords
				float u = i / resCircumference;
				float uNext = (i+1) / resCircumference;
				float v = P.map(y, -halfH, halfH, 1, 0);
				float vNext = P.map(y, -halfH, halfH, 1, 0);
				float vNextRow = P.map(yNext, -halfH, halfH, 1, 0);
				float vNextNextRow = P.map(yNext, -halfH, halfH, 1, 0);
				
				// warp
				float freq = 2f;
				float amp = 0.02f;
				v += amp * P.sin(progressCirc * P.TWO_PI * freq);
				vNext += amp * P.sin(progressCircNext * P.TWO_PI * freq);
				vNextRow += amp * P.sin(progressCirc * P.TWO_PI * freq);
				vNextNextRow += amp * P.sin(progressCircNext * P.TWO_PI * freq);
				
				// draw 2 triangles
				p.vertex(x, y, z, u, v);
				p.vertex(xNextRow, yNext, zNextRow, u, vNextRow);
				p.vertex(xNext, y, zNext, uNext, vNext);
				
				p.vertex(xNextRow, yNext, zNextRow, u, vNextRow);
				p.vertex(xNext, y, zNext, uNext, vNext);
				p.vertex(xNextNextRow, yNext, zNextNextRow, uNext, vNextNextRow);
			}
		}
		p.endShape();

	}

	
}