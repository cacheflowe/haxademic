package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_DropShadowBlur
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage img;
	protected PGraphics shadowOrig;
	protected PGraphics shadow;
	protected PShader colorTransformShader;
	protected boolean shadowSolidColor = true;
	protected String BLUR_SIZE = "BLUR_SIZE";
	protected String BLUR_SIGMA = "BLUR_SIGMA";
	protected String BLUR_ALPHA = "BLUR_ALPHA";
	protected String BLUR_STEPS = "BLUR_STEPS";
	
	protected void config() {
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.LOOP_FRAMES, 900 );
	}
	
	protected void firstFrame() {
		img = DemoAssets.smallTexture();
		shadowOrig = ImageUtil.imageToPGWithPadding(img, 2f);
		shadow = ImageUtil.imageToPGWithPadding(img, 2f);
		colorTransformShader = p.loadShader(FileUtil.getPath("haxademic/shaders/filters/opaque-pixels-to-color.glsl"));
		
		UI.addSlider(BLUR_SIZE, 12, 1, 20, 1, false);
		UI.addSlider(BLUR_SIGMA, 6, 1, 20, 0.1f, false);
		UI.addSlider(BLUR_ALPHA, 0.6f, 0, 1, 0.01f, false);
		UI.addSlider(BLUR_STEPS, 9, 1, 20, 1, false);
	}
	
	
	protected void drawApp() {
		// update shadow graphic
		shadow.beginDraw();
		shadow.clear();
		shadow.image(shadowOrig, shadow.width/2, shadow.height/2);
		BlurProcessingFilter.instance(p).setBlurSize(UI.valueInt(BLUR_SIZE));
		BlurProcessingFilter.instance(p).setSigma(UI.value(BLUR_SIGMA));
		for (int i = 0; i < (UI.valueInt(BLUR_STEPS)); i++) {
			BlurProcessingFilter.instance(p).applyTo(shadow);
		}
		if(shadowSolidColor) {
			colorTransformShader.set("color", 0f, 0f, 0f);
			shadow.filter(colorTransformShader);
		}
		shadow.endDraw();

		
		// draw shadow and originating graphic on top
		p.background(0);
		PG.setDrawCenter(p);
		
		// draw single image & shadow in center
		p.push();
		PG.setCenterScreen(p);
		PG.setPImageAlpha(p, UI.value(BLUR_ALPHA));
		p.image(shadow, 0, 0);
		PG.setPImageAlpha(p, 1f);
		p.image(img, -3f + 3f * P.sin(p.frameCount * 0.03f), -10f + 10f * P.sin(p.frameCount * 0.03f));
		p.pop();
		
		// draw a bunch of shapes overlapped
		for (int i = 0; i < 100; i++) {
			float x = FrameLoop.noiseLoop(i * 0.01f, i) * p.width;
			float y = FrameLoop.noiseLoop(i * 0.01f, i * 2) * p.height;
			PG.setPImageAlpha(p, UI.value(BLUR_ALPHA));
			p.image(shadow, x, y);
			PG.setPImageAlpha(p, 1f);
			p.image(img, x-3f + 3f * P.sin(p.frameCount * 0.03f), y-10f + 10f * P.sin(p.frameCount * 0.03f));
		}
	}

}
