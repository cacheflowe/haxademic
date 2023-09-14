package com.haxademic.demo.draw.shadow;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeOpaquePixelsFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import javafx.scene.effect.Shadow;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_DropShadowGenerator
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage img;
	protected DropShadowGenerator[] shadows;
	
	protected void firstFrame() {
		img = DemoAssets.smallTexture();
		generateShadows();
	}

	protected void generateShadows() {
		int numShadows = 8;
		shadows = new DropShadowGenerator[numShadows];
		for(int i=0; i < numShadows; i++) {
			int blurSize = (int) P.map(i, 0, numShadows-1, 1, 14);
			float blurSigma = P.map(i, 0, numShadows-1, 1, 20);
			int blurSteps = (int) P.map(i, 0, numShadows-1, 1, 15);
			float shadowAlpha = P.map(i, 0, numShadows-1, 1, 0.4f);
			shadows[i] = new DropShadowGenerator(img, blurSize, blurSigma, blurSteps, shadowAlpha);
		}
	}
	
	protected void drawApp() {
		// draw shadow and originating graphic on top
		p.background(255);
		
		// draw a bunch of shapes overlapped
		for (int i = 0; i < shadows.length; i++) {
			PG.setDrawCenter(p);
			float x = 150;
			float y = 50 + i * 80;
			p.image(shadows[i].image(), x + 10, y + 10);
			p.image(img, x, y);
		}
	}


	public class DropShadowGenerator {

		protected PGraphics shadow;
		protected int blurSize;
		protected float blurSigma;
		protected int blurSteps;
		protected float shadowAlpha;

		public DropShadowGenerator(PImage img) {
			this(img, 20, 20, 10, 0.9f);
		}

		public DropShadowGenerator(PImage img, int blurSize, float blurSigma, int blurSteps, float shadowAlpha) {
			buildBuffer(img);
			this.blurSize = blurSize;
			this.blurSigma = blurSigma;
			this.blurSteps = blurSteps;
			this.shadowAlpha = shadowAlpha;
			updateShadow(img);
		}

		protected void buildBuffer(PImage img) {
			shadow = PG.newPG(img.width * 2, img.height * 2);
		}

		public PImage image() {
			return shadow;
		}

		public void updateShadow(PImage img) {
			shadow.beginDraw();
			shadow.clear();
			PG.setDrawCenter(shadow);

			shadow.image(img, shadow.width/2, shadow.height/2);

			BlurProcessingFilter.instance().setBlurSize(blurSize);
			BlurProcessingFilter.instance().setSigma(blurSigma);
			for (int i = 0; i < blurSteps; i++) {
				BlurProcessingFilter.instance().applyTo(shadow);
			}

			ColorizeOpaquePixelsFilter.instance().setColor(0f, 0f, 0f, 1f);
			ColorizeOpaquePixelsFilter.instance().applyTo(shadow);

			shadow.endDraw();
		}

	}
}
