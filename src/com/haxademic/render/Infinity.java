package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;
import processing.core.PVector;

public class Infinity 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PShape infinity;
	protected PVector pos = new PVector();
	protected float angleBack = P.QUARTER_PI * 5f;
	protected float angleForward = P.QUARTER_PI * -1f;
	protected float angleDifference = P.QUARTER_PI * 6f;
	protected float speed = 0.5f;
	protected float dir = 0;
	protected float rot = angleForward;
	
	protected int FRAMES = 60 * 6;
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.LOOP_TICKS, 4);
		Config.setProperty(AppSettings.RENDERING_MOVIE, true);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 3);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 4);
	}
	
	protected void firstFrame() {
		// context
		p.background(0);
		p.noStroke();
		
		infinity = p.loadShape(FileUtil.getPath("svg/infinity.svg")).getTessellation();
		PShapeUtil.centerShape(infinity);
		PShapeUtil.scaleShapeToWidth(infinity, p.width * 0.75f);
	}
	
	protected void drawApp() {
		// set context
//		p.background(0);
		PG.setCenterScreen(p.g);
		
		// leave a trail
//		PG.feedback(p.g, 4);
		BrightnessStepFilter.instance(p).setBrightnessStep(-0.5f/255f);
//		BrightnessStepFilter.instance(p).applyTo(p.g);
		VignetteFilter.instance(p).setDarkness(0.175f);
		VignetteFilter.instance(p).applyTo(p.g);
		
		FeedbackRadialFilter.instance(p).setAmp(0.005f);
		FeedbackRadialFilter.instance(p).setWaveAmp(2f + 0.5f * P.sin(FrameLoop.progressRads()));
		FeedbackRadialFilter.instance(p).setWaveFreq(7f + 4.5f * P.cos(FrameLoop.progressRads()));
		FeedbackRadialFilter.instance(p).applyTo(p.g);
		
		BlurProcessingFilter.instance(p).applyTo(p.g);
		BlurProcessingFilter.instance(p).applyTo(p.g);
		BlurProcessingFilter.instance(p).applyTo(p.g);
		
		// svg option
		infinity.disableStyle();
//		p.fill(P.floor(FrameLoop.loopCurFrame()/30) % 2 < 1 ? 255 : 0);
		p.fill(
			180f + 55f * P.sin(FrameLoop.progressRads() * 1f + 1f),
			55f + 55f * P.sin(FrameLoop.progressRads() * 2f + 2f),
			55f + 55f * P.sin(FrameLoop.progressRads() * 3f + 0f)
		);
		p.noStroke();
		p.rotate(P.QUARTER_PI + 0.f + P.sin(FrameLoop.progressRads()) * P.QUARTER_PI * 0.99f);
		p.shape(infinity);
		
		////////////////////////////////
		/*
		// generative option...
		PG.setDrawCenter(p.g);
		// update angles
		rot += dir;
		if(rot > angleBack) rot = angleBack;
		if(rot < angleForward) rot = angleForward;
		float rotPerFrame = 0.037f;
		if(FrameLoop.isTick() && FrameLoop.curTick() == 2) {
//			rot = angleBack;
			dir = rotPerFrame;
		}
		if(FrameLoop.isTick() && FrameLoop.curTick() == 0) {
//			rot = angleForward;
			dir = -1f * rotPerFrame;
		}
		
		// move particle 
		pos.add(P.cos(rot) * speed, P.sin(rot) * speed);
		
		// draw particle
		p.fill(255);
		p.noStroke();
		p.circle(pos.x, pos.y, 10);
		// change directions
//		FrameLoop.loopCurFrame()
		*/
		/////////////////////////////////////
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			pos.set(0, 0);
			speed = 2f;
		}
	}
	
}