package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.Fluid;

import processing.core.PGraphics;
import processing.core.PVector;

public class Demo_Fluid 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Fluid fluid;
	protected PGraphics fluidBuff;

	public void setupFirstFrame() {
		int w = 50;
		int h = 50;
		int scale = 10;
		fluid = new Fluid(w, h, 0f, 0.000001f, 0.01f);
		fluid.scale(scale);
		
		fluidBuff = p.createGraphics(w * scale, h * scale, PRenderers.P3D);
	}

	public void drawApp() {
		// set up context
		p.background(100);
		p.noStroke();

		if (mousePressed) {
			for (int i = 0; i < 5; i++) {
				PVector v = new PVector(mouseX - pmouseX, mouseY-pmouseY);
				v.mult(2);
				int x = p.mouseX/fluid.scale() + P.round(p.random(-2, 3));
				int y = p.mouseY/fluid.scale() + P.round(p.random(-2, 3));
				fluid.addVelocity(x, y, v.x, v.y);
			}

			for (int x = mouseX-2; x < mouseX+2; x++) {
				for (int y = mouseY-2; y < mouseY+2; y++) {
					fluid.addDensity(x/fluid.scale(), y/fluid.scale(), random(10, 25));
				}
			}
		}

		// calc & draw fluid
		fluid.step();
		fluid.fadeAmp(0.1f);
		fluidBuff.beginDraw();
		//fluid.renderV(fluidBuff);
		fluid.renderD(fluidBuff);
		fluidBuff.endDraw();
		fluid.fadeD();
		
		// postprocessing
		BlurHFilter.instance(p).setBlurByPercent(2f, fluidBuff.width);
		BlurVFilter.instance(p).setBlurByPercent(2f, fluidBuff.height);
		BlurHFilter.instance(p).applyTo(fluidBuff);
		BlurVFilter.instance(p).applyTo(fluidBuff);
		BlurHFilter.instance(p).applyTo(fluidBuff);
		BlurVFilter.instance(p).applyTo(fluidBuff);
		
		// draw fluid buffer to screen
		p.image(fluidBuff, 0, 0);
	}

}
