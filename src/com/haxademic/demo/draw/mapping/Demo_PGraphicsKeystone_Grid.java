package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_PGraphicsKeystone_Grid
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PGraphicsKeystone[] keystoneQuads;
	
	protected boolean testPattern = false;
	protected PShader shaderPattern;
	protected PShader shaderPattern2;
	protected PImage overlayImage;
	protected int quadIndex = 0;
	protected boolean debug = true;
	protected int rows = 4;
	protected int cols = 5;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 700 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
	}

	public void setupFirstFrame() {
		shaderPattern = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-scrolling-dashed-lines.glsl"));
		shaderPattern2 = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-op-wavy-rotate.glsl"));
		overlayImage = DemoAssets.particle();
		buildCanvas();
	}

	protected void buildCanvas() {
		keystoneQuads = new PGraphicsKeystone[rows * cols];
		for (int i = 0; i < keystoneQuads.length; i++) {
			PGraphics pg = p.createGraphics( p.width / 2, p.height / 2, P.P3D );
			pg.smooth(OpenGLUtil.SMOOTH_HIGH);
			keystoneQuads[i] = new PGraphicsKeystone(p, pg, 12, FileUtil.getFile("text/keystoning/grid-demo"+i+".txt"));
		}
	}
	
	protected void resetQuads() {
		// get coordinates based on random indexes across a grid
		for (int i = 0; i < keystoneQuads.length; i++) {
			float col = i % cols;
			float row = P.floor((float) i / cols);
			float x = P.map(col, 0, cols - 1, 0.2f * p.width, 0.8f * p.width);
			float y = P.map(row, 0, rows - 1, 0.2f * p.height, 0.8f * p.height);
			keystoneQuads[i].setPosition(x, y, 100, 50);
		}
	}

	public void drawApp() {
		p.background(0);
		
		// update textures
		shaderPattern.set("time", p.frameCount * 0.01f);
		shaderPattern2.set("time", p.frameCount * 0.01f);

		// update buffers
		for (int i = 0; i < keystoneQuads.length; i++) {
			PGraphics pg = keystoneQuads[i].pg();
			pg.beginDraw();
			if(i % 2 == 1) pg.filter(shaderPattern);
			else pg.filter(shaderPattern2);
			pg.image(overlayImage, 0, 0, pg.width, pg.height);
			pg.endDraw();
		}
	
		// draw test patterns
		if(testPattern == true) {
			for (int i = 0; i < keystoneQuads.length; i++) {
				keystoneQuads[i].drawTestPattern();
			}
		}
		
		// draw to screen 
		for (int i = 0; i < keystoneQuads.length; i++) {
			keystoneQuads[i].update(p.g);
			keystoneQuads[i].fillSolidColor(p.g, p.color(255, 0, 0, 127));
		}

	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') debug = !debug;
		if(p.key == 't') testPattern = !testPattern;
		if(p.key == 'r') resetQuads();
		if(p.key == ']') {
			quadIndex++;
			if(quadIndex >= keystoneQuads.length) quadIndex = 0;
			setActiveRect();
		}
		if(p.key == '[') {
			quadIndex--;
			if(quadIndex < 0) quadIndex = keystoneQuads.length - 1;
			setActiveRect();
		}
	}
	
	public void mouseMoved() {
		super.mouseMoved();
		for (int i = 0; i < keystoneQuads.length; i++) {
			if(keystoneQuads[i].isHovered()) {
				quadIndex = i;
			}
		}
		setActiveRect();
	}
	
	protected void setActiveRect() {
		for (int i = 0; i < keystoneQuads.length; i++) {
			keystoneQuads[i].setActive(i == quadIndex && debug);
		}
	}

}
