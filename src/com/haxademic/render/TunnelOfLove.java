package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;

public class TunnelOfLove 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float modelHeight;
	protected PShape svg;

	protected void config() {
		int FRAMES = 320;
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
//		svg = PShapeUtil.svgToUniformPointsShape(FileUtil.getFile("svg/ello-filled.svg"), 13);
//		svg = PShapeUtil.svgToUniformPointsShape(FileUtil.getFile("haxademic/svg/x.svg"), 15);
		svg = PShapeUtil.svgToUniformPointsShape(FileUtil.getFile("haxademic/svg/heart.svg"), 15);
		svg.disableStyle();
		PShapeUtil.centerShape(svg);
		PShapeUtil.scaleShapeToHeight(svg, p.height * 0.6f);
	}

	public void drawApp() {		
		background(0);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, 0);
		p.translate(0, p.height * 0.02f + (p.height * 0.02f) * P.sin(FrameLoop.progressRads()));
//		PG.basicCameraFromMouse(p.g);
//		p.rotateX(P.QUARTER_PI * -3f);
//		p.rotateX(P.QUARTER_PI * -3.5f + P.sin(AnimationLoop.progressRads()) * 0.1f);
//		p.rotateZ(AnimationLoop.progressRads());
//		p.rotateZ(P.PI + P.sin(AnimationLoop.progressRads()) * 0.1f);
		
		// draw mesh with texture or without
		p.fill(255);
		float numShapes = 40f;
		float colorFade = 127f / numShapes;
		float thickness = 500f + 498f * P.sin(FrameLoop.progressRads());
		float spacing = thickness / numShapes;
		p.translate(0, 0, -thickness/2f);
		for (int i = 0; i < numShapes; i++) {
			p.strokeWeight(4 + (2f * P.sin(P.PI + FrameLoop.progressRads())) - 0.005f * i);
			p.stroke(255, 127 + colorFade * i, 127 + colorFade * i);
			p.pushMatrix();
			p.translate(0, 0, spacing * i);
			float rotAmp = i * 0.04f;
			p.rotateZ(rotAmp * thickness/280f * 0.1f * P.sin(FrameLoop.progressRads() * 4f + i * (P.TWO_PI / numShapes)));
			p.shape(svg);
			p.popMatrix();
		}
	}
		
}