package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.GlowFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_GlowShader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage img;
	PShape shape;
	PGraphics pg;
	int frames = 120;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, frames );
	}

	protected void firstFrame() {

		img = DemoAssets.smallTexture();
		shape = DemoAssets.shapeX().getTessellation();
		shape.disableStyle();
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.7f);
		pg = p.createGraphics(p.width, p.height, P.P3D);
		pg.smooth(8);
	}

	protected void drawApp() {
		background(255);
		
		float progress = (float) (p.frameCount % frames) / (float) frames;
		
		// update shadow buffer
		pg.beginDraw();
		pg.pushMatrix();
		pg.clear();
		pg.fill(0);
//		pg.image(img, 0, 0);
		pg.translate(pg.width/2, pg.height/2);
		pg.rotate(Mouse.xNorm * P.TWO_PI);
		pg.shape(shape, 0, 0);
//		GlowFilter.instance(p).setSize(30f + 20f * P.sin((float)p.frameCount * 0.05f));
//		GlowFilter.instance(p).setRadialSamples(P.map(p.mouseX, 0, p.width, 8f, 128f));
		GlowFilter.instance(p).setSize(26f + 14f * P.sin(progress * P.TWO_PI));
		GlowFilter.instance(p).setRadialSamples(32f);
		GlowFilter.instance(p).setGlowColor(0f, 0f, 0f, 0.6f);
		GlowFilter.instance(p).applyTo(pg);
		pg.popMatrix();
		pg.endDraw();
		
		// draw shadow buffer to screen
		p.image(pg, 0, 0);
		InvertFilter.instance(p).applyTo(p);
	}

}

