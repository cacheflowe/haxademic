package com.haxademic.demo.draw.shadow;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ShadowFromImage;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;

public class Demo_ShadowFromImage
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage img;
	protected ShadowFromImage[] shadows;
	protected ShadowFromImage shadowAnimated;
	
	protected void firstFrame() {
		img = DemoAssets.smallTexture();
		generateShadows();
	}

	protected void generateShadows() {
		int numShadows = 8;
		shadows = new ShadowFromImage[numShadows];
		for(int i=0; i < numShadows; i++) {
			int blurSize = (int) P.map(i, 0, numShadows-1, 1, 14);
			float blurSigma = P.map(i, 0, numShadows-1, 1, 20);
			int blurSteps = (int) P.map(i, 0, numShadows-1, 1, 15);
			float shadowAlpha = P.map(i, 0, numShadows-1, 0.4f, 0.9f);
			shadows[i] = new ShadowFromImage(img, blurSize, blurSigma, blurSteps, shadowAlpha);
		}

		shadowAnimated = new ShadowFromImage(img);
	}
	
	protected void drawApp() {
		// draw shadow and originating graphic on top
		p.background(255);
		
		// draw shadows with increasing params
		for (int i = 0; i < shadows.length; i++) {
			PG.setDrawCenter(p);
			float x = 150;
			float y = 100 + i * 110;
			p.image(shadows[i].image(), x + 10, y + 10);
			p.image(img, x, y);

			DemoAssets.setDemoFont(p.g);
			p.fill(0);
			p.text(shadows[i].debugText(), x + 120, y - 40);
		}

		// draw animated shadow & debug info
		shadowAnimated.blurSigma(FrameLoop.osc(0.01f, 1, 20))
									.blurSize((int) FrameLoop.osc(0.013f, 1, 12))
									.blurSteps((int) FrameLoop.osc(0.005f, 1, 15))
									.shadowAlpha(FrameLoop.osc(0.02f, 0.4f, 0.9f))
									.shadowRed(FrameLoop.osc(0.01f, 0, 1))
									.shadowGreen(FrameLoop.osc(0.013f, 0, 1))
									.shadowBlue(FrameLoop.osc(0.016f, 0, 1))
									.update(img);
		p.image(shadowAnimated.image(), 710, 110);
		p.image(img, 700, 100);

		p.fill(0);
		p.text(shadowAnimated.debugText(), 820, 100 - 40);
	}

}
