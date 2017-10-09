package com.haxademic.sketch.shader;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.filters.shaders.GlowFilter;
import com.haxademic.core.draw.filters.shaders.InvertFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class GlowShader
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage img;
	PShape shape;
	PGraphics pg;
	PShader glowShader;
	int frames = 120;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 * 2 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 * 2 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, frames );
	}

	public void setup() {
		super.setup();
		img = p.loadImage(FileUtil.getFile("images/the-black-box-white.png"));
		shape = p.loadShape(FileUtil.getFile("svg/cacheflowe-logotype-new.svg"));
		shape = p.loadShape(FileUtil.getFile("svg/ello-centered.svg"));
//		img = p.loadImage(FileUtil.getFile("images/halloween.png"));
//		img = p.loadImage(FileUtil.getFile("images/bread-large.png"));
		pg = p.createGraphics(p.width, p.height, P.P3D);
		pg.smooth(8);
		OpenGLUtil.setTextureQualityHigh(pg);
//		pg = ImageUtil.imageToGraphics(p, img);
		glowShader = loadShader(FileUtil.getFile("shaders/filters/glow.glsl"));
	}

	public void drawApp() {
		background(0);
		
		float progress = (float) (p.frameCount % frames) / (float) frames;
		
		pg.beginDraw();
		pg.pushMatrix();
		pg.clear();
//		pg.image(img, 0, 0);
		pg.translate(pg.width/2, pg.height/2);
		DrawUtil.setDrawCenter(pg);
		float shapeScale = (pg.height / shape.height) * 0.7f;
		shape.disableStyle();
		pg.fill(0);
		pg.shape(shape, 0, 0, shape.width * shapeScale, shape.height * shapeScale);
//		GlowFilter.instance(p).setSize(30f + 20f * P.sin((float)p.frameCount * 0.05f));
//		GlowFilter.instance(p).setRadialSamples(P.map(p.mouseX, 0, p.width, 8f, 128f));
		GlowFilter.instance(p).setSize(26f + 14f * P.sin(progress * P.TWO_PI));
		GlowFilter.instance(p).setRadialSamples(256f);
		GlowFilter.instance(p).setGlowColor(1f, 1f, 1f, 0.6f);
		GlowFilter.instance(p).applyTo(pg);
		pg.popMatrix();
		pg.endDraw();
		
		p.image(pg, 0, 0);
//		InvertFilter.instance(p).applyTo(p);
	}

}

