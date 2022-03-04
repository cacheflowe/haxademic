package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeOpaquePixelsFilter;
import com.haxademic.core.draw.filters.pshader.SubtractOpacityFromMapFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_ShadowLayerOuterGlow
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics shadowLayer; 
	protected int FRAMES = 240;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}

	protected void firstFrame() {
		shadowLayer = PG.newPG(p.width, p.height);
		DebugView.setTexture("pg", pg);
		DebugView.setTexture("shadowLayer", shadowLayer);
	}

	protected void drawApp() {
		background(255);
		
		// bg gradient
		p.push();
		p.translate(p.width/2, p.height/2);
		p.rotate(P.HALF_PI);
		p.scale(2.5f);
		Gradients.linear(p, p.width, p.height, p.color(255, 200, 200), p.color(12, 70, 90));
		p.pop();

		// draw shapes to pg
		pg.beginDraw();
		pg.clear();
		
		PG.setDrawCenter(pg);
		PG.setCenterScreen(pg);
		pg.translate(0, pg.height * 0.1f, 0);
		PG.setDrawFlat2d(pg, true);
		pg.stroke(0);
		pg.strokeWeight(2);
		pg.noStroke();

		float moveAmp = pg.width * 0.99f;
		for (int i = 0; i < 40; i++) {
			float posX = -moveAmp + moveAmp*2f * FrameLoop.noiseLoop(0.15f, i*10f);
			float posY = -moveAmp + moveAmp*2f * FrameLoop.noiseLoop(0.15f, i*5f);
//			pg.translate(0, 0, 1);
			pg.push();
			pg.translate(posX, posY);
			pg.rotate(35f * FrameLoop.noiseLoop(0.1f, i*6f));
			pg.fill(ColorsHax.colorFromGroupAt(7, i));
			float objSize = 60 + 50f * P.sin(i/3f);
			if(i % 2 == 0) {
				pg.rect(0, 0, objSize, objSize);
			} else {
				pg.ellipse(0, 0, objSize, objSize);
			}
			pg.pop();
		}
		pg.endDraw();
		
		// copy drawing to shadow layer
		shadowLayer.beginDraw();
		shadowLayer.clear();
		ImageUtil.copyImage(pg, shadowLayer);
		shadowLayer.endDraw();
		
		// convert opaque pixels to black, then blur
		ColorizeOpaquePixelsFilter.instance(p).setColor(0f, 0f, 0f);
		ColorizeOpaquePixelsFilter.instance(p).applyTo(shadowLayer);
		BlurProcessingFilter.instance(p).setBlurSize(10);
		BlurProcessingFilter.instance(p).setSigma(10);
		for (int i = 0; i < 7; i++) {
			BlurProcessingFilter.instance(p).applyTo(shadowLayer);
		}
		
		// knock out original pixels from map
		if(Mouse.xNorm > 0.5f) {
			// knock out original 
			SubtractOpacityFromMapFilter.instance(p).setMap(pg);
			SubtractOpacityFromMapFilter.instance(p).applyTo(shadowLayer);
			
			// then blur again
			for (int i = 0; i < 1; i++) {
				BlurProcessingFilter.instance(p).applyTo(shadowLayer);
			}
		}

		p.perspective();
		
		// maybe tilt
		PG.setDrawFlat2d(p.g, true);
		PG.setDrawCenter(p.g);
		PG.setCenterScreen(p.g);
		p.translate(0, pg.height * -0.1f, 0);
		p.rotateX(0.4f);
		p.scale(1.1f);
		
		// draw shadow buffer to screen
		p.push();
		PG.setPImageAlpha(p, 0.4f);
		p.scale(1.2f);
		p.image(shadowLayer, 6, 6);
		PG.resetPImageAlpha(p);
		p.pop();
		// main image
		p.translate(0, 0, pg.height * 0.15f);
		p.image(pg, 0, 0);
	}

}

