package com.haxademic.demo.draw.color;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Gradients_RadialBg
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics pgBG;
	protected PImage pgBGImg;
	
	protected void config() {
		Config.setAppSize(960, 540);
	}

	protected void firstFrame() {
		// draw background plate in 1:1 aspect ratio by calculating surrounding circle for app size
		pgBG = PG.newPG(p.width, p.height);
		float circleSize = MathUtil.circleSizeAroundRect(pgBG.width, pgBG.height);
		pgBG.beginDraw();
		pgBG.background(255,0,0);
		pgBG.translate(pgBG.width/2, pgBG.height/2);
		Gradients.radial(pgBG, circleSize, circleSize, 0xff330000, 0x99ffffff, 120);
		pgBG.endDraw();
		pgBGImg = pgBG.get();
	}

	protected void drawApp() {
		// set background image
		p.background(pgBGImg);
		PG.setCenterScreen(p.g);
		
		// set rect size & calculate surrounding circle
		float rectWidth = 300;
		float rectHeight = 70;
		float circleSize = MathUtil.circleSizeAroundRect(rectWidth, rectHeight);
		
		// draw gradient to surround rect, just for fun
		PG.setDrawCenter(p);
		Gradients.radial(p.g, circleSize, circleSize, 0xff000000, 0xffffff00, 120);

		// draw outline around gradient & rect
		p.stroke(0);
		p.noFill();
		p.ellipse(0, 0, circleSize, circleSize);
		
		// draw rect
		p.rotate(FrameLoop.count(0.01f));
		p.fill(255, 127, 127);
		p.rect(0, 0, rectWidth, rectHeight);
		

	}
}
