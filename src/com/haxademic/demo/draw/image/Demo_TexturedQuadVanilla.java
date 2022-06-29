package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class Demo_TexturedQuadVanilla 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PImage img;
	
	protected void firstFrame() {

		img = DemoAssets.justin();
		OpenGLUtil.setTextureQualityHigh(p.g);
	}
	
	protected void drawApp() {
		if(p.frameCount == 1) background(0);
		PG.feedback(p.g, 0, 0.1f, 10f);

		float f = (float) frameCount * 0.1f;
		beginShape(QUADS);
		texture(img);
		vertex(50+ 90f*sin(f/10f), 70+ 50f*sin(f/9f),  0f*sin(f/6f), 			0, 0);
		vertex(450+ 90f*sin(f/7f), 100+ 50f*sin(f/7f), 0f*sin(f/7f), 			img.width, 0);
		vertex(500+ 90f*sin(f/3f), 550+ 50f*sin(f/4f), 0f*sin(f/3f), 			img.width, img.height);
		vertex(90+ 90f*sin(f/9f),  600+ 60f*sin(f/6f), 0f*sin(f/8f), 			0, img.height);
		endShape();
	}
}
