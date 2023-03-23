package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;

public class Demo_PShapeUtil_svgToUniformPointsShape 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float modelHeight;
	protected PShape svg;

	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, 240);
	}
	
	protected void firstFrame() {
//		svg.setStroke(false);

		svg = PShapeUtil.svgToUniformPointsShape(FileUtil.getPath("haxademic/svg/hexagon.svg"), 20);
		svg.disableStyle();
		PShapeUtil.centerShape(svg);
		PShapeUtil.scaleShapeToExtent(svg, p.height * 0.8f);
	}

	protected void drawApp() {		
		if(p.frameCount == 1) background(0);
		for(int i=0; i < 10; i++) PG.feedback(p.g, 2f);
		BlurProcessingFilter.instance().setBlurSize(6);
		BlurProcessingFilter.instance().applyTo(p.g);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		p.rotateX(P.QUARTER_PI);
		p.rotateZ(FrameLoop.progressRads()); /// -P.HALF_PI +
		p.scale(1f + 0.2f * P.sin(FrameLoop.progressRads()));
		// draw mesh with texture or without
		p.stroke(
				255, 
				127 + 127 * P.sin(FrameLoop.progressRads() * 3f),
				127 + 127 * P.sin(P.PI + FrameLoop.progressRads()));
		p.strokeWeight(10 + 8f * P.sin(P.PI + FrameLoop.progressRads()));
		p.shape(svg);
	}
		
}