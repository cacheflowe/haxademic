package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.OpticalFlow;
import com.haxademic.core.draw.textures.Fluid;

import processing.core.PGraphics;

public class Demo_OpticalFlowFluid 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int w = 480; 
	protected int h = 480; 
	protected OpticalFlow opticalFlow;

	protected Fluid fluid;
	protected PGraphics fluidBuff;
	protected int scale = 10;

	protected void firstFrame() {
		// set buffer size
		pg = p.createGraphics(w, h, PRenderers.P3D);
		
		// build optical flow
		float detectionScaleDown = 0.15f;
		opticalFlow = new OpticalFlow(pg, detectionScaleDown);
		
		// build fluid
		fluid = new Fluid(w/scale, h/scale);
		fluid.scale(scale);
		fluidBuff = p.createGraphics(w, h, PRenderers.P3D);
	}

	protected void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();
		
		// draw mouse point to offscreen buffer
		pg.beginDraw();
		pg.background(0);
		pg.noStroke();
		pg.fill(255);
		PG.setDrawCenter(pg);
		pg.ellipse(p.mouseX, p.mouseY, 20, 20);
		pg.endDraw();

		// update optical flow 
		opticalFlow.smoothing(0.01f);
		opticalFlow.update(pg);

		// draw input view to screen
		p.image(pg, 0, 0);
		// draw debug flow results
		opticalFlow.debugDraw(p.g, false);
		
		// check vector getter for a specific position
		updateFluid();
	}
	
	protected void updateFluid() {
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				float xNorm = (float) x / (float) w;
				float yNorm = (float) y / (float) h;
				float[] vecResult = opticalFlow.getVectorAt(xNorm, yNorm);
				fluid.addVelocity(x/fluid.scale(), y/fluid.scale(), vecResult[0] * 0.0015f, vecResult[1] * 0.0015f);
				fluid.addDensity(x/fluid.scale(), y/fluid.scale(), P.abs(vecResult[0]) * 0.0075f + P.abs(vecResult[1]) * 0.0075f);
			}
		}

		// calc & draw fluid
		fluid.diffusion(0.003f);
		fluid.viscosity(0.0001f);
		fluid.dt(0.001f);
		fluid.step();
		
		fluidBuff.beginDraw();
		fluidBuff.background(255,0,0);
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
		BlurHFilter.instance(p).applyTo(fluidBuff);
		BlurVFilter.instance(p).applyTo(fluidBuff);
		
		// draw fluid buffer to screen
		p.image(fluidBuff, 0, 0);
	}

}
