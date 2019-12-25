package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_MaskConcentric
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics[] masks;
	protected PGraphics[] images;
	protected PGraphics[] shadows;
	protected PShader colorTransformShader;
	protected int numCircles = 10;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 1000 );
	}

	public void firstFrame() {
		// build masks
		masks = new PGraphics[numCircles];
		for (int i = 0; i < numCircles; i++) {
			PGraphics mask = PG.newPG(p.width, p.height);
			masks[i] = mask;
			float radius = mask.width * 0.9f;
			radius = radius - radius * (float) i / (float) numCircles;
			mask.beginDraw();
			mask.background(0);
			PG.setCenterScreen(mask);
			PG.setDrawCenter(mask);
			mask.noStroke();
			mask.fill(255);
			mask.circle(0, 0, radius);
			mask.endDraw();
			// if(i < 4) DebugView.setTexture("mask-"+i, mask);
		}
		
		// build images
		images = new PGraphics[numCircles];
		for (int i = 0; i < numCircles; i++) {
			images[i] = PG.newPG(p.width, p.height);
		}
		
		// build shadows
		shadows = new PGraphics[numCircles];
		for (int i = 0; i < numCircles; i++) {
			shadows[i] = PG.newPG(p.width, p.height);
		}
		colorTransformShader = p.loadShader(FileUtil.getPath("haxademic/shaders/filters/opaque-pixels-to-color.glsl"));
	}

	public void updateMaskedImages(PImage source) {
		for (int i = 0; i < numCircles; i++) {
			PGraphics image = images[i];
			image.beginDraw();
			image.background(255);
			image.noStroke();
			image.fill(255);
			ImageUtil.drawImageCropFill(source, image, true);
			image.endDraw();
			image.mask(masks[i]);
			// if(i < 4) DebugView.setTexture("image-"+i, image);
		}
	}

	public void updateShadows() {
		for (int i = 0; i < numCircles; i++) {
			PGraphics shadow = shadows[i];
			shadow.beginDraw();
			shadow.clear();
			shadow.image(images[i], 0, 0);
			BlurProcessingFilter.instance(p).setBlurSize(12);
			BlurProcessingFilter.instance(p).setSigma(2.4f);
			for (int j = 0; j < 6; j++) {
				BlurProcessingFilter.instance(p).applyTo(shadow);
			}
			colorTransformShader.set("color", 0f, 0f, 0f);
			shadow.filter(colorTransformShader);
			shadow.endDraw();
		}
	}
	
	public void drawApp() {
		// user interaction
		float offsetX = -P.TWO_PI * 0.2f + P.TWO_PI * 0.4f * Mouse.xEasedNorm;
		float offsetY = -P.TWO_PI + P.TWO_PI * 2f * Mouse.yEasedNorm;

		// context reset
		p.background(0);
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		p.g.rotateX(offsetY);
		
		// lazy init images - not sure why this has to happen a couple times before stopping?!
		if(p.frameCount < 3) {
			updateMaskedImages(DemoAssets.justin());
			updateShadows();
		} /* else {
			updateMaskedImages(WebCam.instance().image());
		} */
		

		
		for (int i = 0; i < numCircles; i++) {
			PGraphics image = images[i];
			PGraphics shadow = shadows[i];
			p.pushMatrix();
			p.translate(0, 0, (float) i * P.abs(offsetY) * 30f);
			p.rotate(offsetX + (float) i * offsetX);
			PG.setPImageAlpha(p, P.abs(offsetX) / 1f);
			p.image(shadow, 0, 0);
			p.image(shadow, 0, 0);
			PG.resetPImageAlpha(p);
			p.image(image, 0, 0);
			p.popMatrix();
		}
	}
}
