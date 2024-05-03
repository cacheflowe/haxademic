package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.pshader.PShapeAlphaStepFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;

public class Demo_PShapeUtil_svgWithAlpha 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		// build shape and assign texture
		shape = p.loadShape(FileUtil.getPath("svg/numbers/nine.svg"));
	}
		
	protected void drawApp() {
		// clear the screen
		background(40);
		p.noStroke();
		PG.setCenterScreen(p);
		PG.setDrawCorner(p.g);

		// draw shape
		p.scale(10);
		PShapeAlphaStepFilter.instance().setAlphaStep(FrameLoop.osc(0.03f, 0, -1));
		PShapeAlphaStepFilter.instance().setOnContext(p.g);
		p.shape(shape);
		p.resetShader();
	}
	
}