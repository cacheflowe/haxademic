package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_DropShadowBlur
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage img;
	protected PGraphics shadowOrig;
	protected PGraphics shadow;
	protected PShader colorTransformShader;
	protected boolean shadowSolidColor = true;
	protected String BLUR_SIZE = "BLUR_SIZE";
	protected String BLUR_SIGMA = "BLUR_SIGMA";
	protected String BLUR_ALPHA = "BLUR_ALPHA";
	protected String BLUR_STEPS = "BLUR_STEPS";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SHOW_SLIDERS, true );
	}
	
	protected void setupFirstFrame() {
		img = DemoAssets.smallTexture();
		shadowOrig = imageToImageWithPadding(img, 2f);
		shadow = imageToImageWithPadding(img, 2f);
		colorTransformShader = p.loadShader(FileUtil.getFile("haxademic/shaders/filters/opaque-pixels-to-color.glsl"));
		
		p.ui.addSlider(BLUR_SIZE, 12, 1, 20, 1, false);
		p.ui.addSlider(BLUR_SIGMA, 6, 1, 20, 0.1f, false);
		p.ui.addSlider(BLUR_ALPHA, 0.6f, 0, 1, 0.01f, false);
		p.ui.addSlider(BLUR_STEPS, 9, 1, 20, 1, false);
	}
	
	public PGraphics imageToImageWithPadding(PImage img, float scaleCanvasUp) {
		PGraphics pg = PG.newPG(P.ceil((float) img.width * scaleCanvasUp), P.ceil((float) img.height * scaleCanvasUp));
		pg.beginDraw();
		PG.setDrawCenter(pg);
		pg.clear();
		pg.translate(pg.width/2, pg.height/2);
		pg.image(img, 0, 0);
		pg.endDraw();
		return pg;
	}  
	
	public void drawApp() {
		shadow.beginDraw();
		shadow.clear();
		shadow.image(shadowOrig, shadow.width/2, shadow.height/2);
		BlurProcessingFilter.instance(p).setBlurSize(p.ui.valueInt(BLUR_SIZE));
		BlurProcessingFilter.instance(p).setSigma(p.ui.value(BLUR_SIGMA));
		for (int i = 0; i < (p.ui.valueInt(BLUR_STEPS)); i++) {
			BlurProcessingFilter.instance(p).applyTo(shadow);
		}
		if(shadowSolidColor) {
			colorTransformShader.set("color", 0f, 0f, 0f);
			shadow.filter(colorTransformShader);
		}
		shadow.endDraw();

		p.background(255);
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);
		PG.setPImageAlpha(p, p.ui.value(BLUR_ALPHA));
		p.image(shadow, 0, 0);
		PG.setPImageAlpha(p, 1f);
		p.image(img, -3f + 3f * P.sin(p.frameCount * 0.03f), -10f + 10f * P.sin(p.frameCount * 0.03f));
	}

}
