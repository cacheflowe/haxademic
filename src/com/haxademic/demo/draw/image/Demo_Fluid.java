package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.textures.Fluid;

import processing.core.PGraphics;
import processing.core.PVector;

public class Demo_Fluid 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Fluid fluid;
	protected PGraphics fluidBuff;

	public void firstFrame() {
		int w = 50;
		int h = 50;
		int scale = 10;
		fluid = new Fluid(w, h);
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
		fluid.diffusion(0.001f);
		fluid.viscosity(0.0001f);
		fluid.dt(0.001f);
		fluid.step();

		fluidBuff.beginDraw();
		//fluid.renderV(fluidBuff);
		fluid.renderD(fluidBuff);
		fluidBuff.endDraw();
		
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
