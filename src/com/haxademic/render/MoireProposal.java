package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class MoireProposal
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics stripesBuffer2;
	protected PGraphics box1;
	protected PGraphics box2;
	protected PGraphics box3;
	protected PGraphics box4;
	protected PGraphics boxFloor;
	protected PGraphics boxCeiling;
	protected PShader stripes;
	protected PShader twist;
	protected PFont font;
	protected PShape person;
	protected float boxW = 900;
	protected float boxH = 500;
	protected float boxD = 900;

	protected void config() {
		int FRAMES = 1200;
//		FRAMES = 600;
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 600 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES );
	}

	public void firstFrame() {

		
		font = DemoAssets.fontBitlow(100);
		stripesBuffer2 = p.createGraphics(p.width * 4, p.height, P.P3D);
		
		box1 = p.createGraphics(p.width, p.height, P.P3D);
		box2 = p.createGraphics(p.width, p.height, P.P3D);
		box3 = p.createGraphics(p.width, p.height, P.P3D);
		box4 = p.createGraphics(p.width, p.height, P.P3D);
		boxFloor = p.createGraphics(p.width, p.width, P.P3D);
		boxCeiling = p.createGraphics(p.width, p.width, P.P3D);
		
		stripes = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-rotating-stripes.glsl"));
		twist = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-concentric-rectwist.glsl"));
		
		person = DemoAssets.objHumanoid();
		PShapeUtil.scaleShapeToExtent(person, boxH / 2);

	}

	public void drawNumberToTexture(String str, PGraphics tex) {
		tex.beginDraw();
		tex.fill(0);
		tex.textFont(font);
		tex.textAlign(P.CENTER, P.TOP);
		tex.text(str, 0, p.height * 0.2f, p.width, p.height * 0.8f);
		tex.endDraw();
	}
	
	public void drawDoorOnTexture(PGraphics tex) {
		float doorW = tex.width * 0.2f;
		float doorH = tex.height * 0.6f;
		tex.beginDraw();
		tex.fill(0);
		tex.rect(tex.width / 2 - doorW / 2, tex.height - doorH, doorW, doorH);
		tex.endDraw();
	}
	
	public void drawPointOnFloor(PGraphics tex) {
		float circleSize = tex.width * 0.1f * (1f + 0.2f * P.sin(8f * FrameLoop.progressRads()));
		tex.beginDraw();
		PG.setDrawCenter(p);
		tex.fill(255);
		tex.stroke(0);
		tex.strokeWeight(7);
		tex.ellipse(tex.width / 2, tex.height * 0.785f, circleSize, circleSize);
		tex.endDraw();
		PG.setDrawCorner(p);
	}
	
	public void drawApp() {
		p.background(0);
		p.lights();
//		PG.setBetterLights(p);
		p.fill(255);
		p.noStroke();
		
		// stripes
		stripes.set("amp", 350.0f + 200f * P.cos(P.PI + P.QUARTER_PI + FrameLoop.progressRads()));
		stripes.set("time", FrameLoop.progress() * 20f);
		stripes.set("rot", P.HALF_PI + 0.15f * P.sin(FrameLoop.progressRads()));
		stripesBuffer2.filter(stripes);
		
		// floor
		twist.set("time", 20f * FrameLoop.progress());
		boxFloor.filter(twist);
		
		// ceiling
		boxCeiling.beginDraw();
		boxCeiling.background(0);
		boxCeiling.endDraw();
		
		// spread across box sides
		box1.copy(stripesBuffer2, p.width * 0, 0, p.width, p.height, 0, 0, p.width, p.height);
		// drawNumberToTexture("1", box1);
		box2.copy(stripesBuffer2, p.width * 1, 0, p.width, p.height, 0, 0, p.width, p.height);
		// drawNumberToTexture("2", box2);
		box3.copy(stripesBuffer2, p.width * 2, 0, p.width, p.height, 0, 0, p.width, p.height);
		// drawNumberToTexture("3", box3);
		box4.copy(stripesBuffer2, p.width * 3, 0, p.width, p.height, 0, 0, p.width, p.height);
		// drawNumberToTexture("4", box4);
		// drawNumberToTexture("5", boxFloor);

		// draw doors
		drawDoorOnTexture(box2);
		drawDoorOnTexture(box4);

		// draw kinect detection
		drawPointOnFloor(boxFloor);
		
		// vignette
		VignetteFilter.instance(p).setDarkness(0.75f);
		VignetteFilter.instance(p).applyTo(box1);
		VignetteFilter.instance(p).applyTo(box2);
		VignetteFilter.instance(p).applyTo(box3);
		VignetteFilter.instance(p).applyTo(box4);
		VignetteFilter.instance(p).applyTo(boxFloor);
		VignetteFilter.instance(p).applyTo(boxCeiling);
		
		// draw box
		p.translate(p.width/2, 0); // -p.height * 0.25f);
		p.translate(0, 0, -p.height * 0.2f);
		p.rotateY(FrameLoop.progressRads());
		Shapes.drawTexturedCubeInside(p.g, boxW, boxH, boxD, box1, box2, box3, box4, boxFloor, boxCeiling);
		
		// draw human
		person.disableStyle();
		p.fill(0);
		p.translate(0, boxH/2, -500);
		p.rotateY(P.PI);
		p.shape(person);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
		}
	}

}
