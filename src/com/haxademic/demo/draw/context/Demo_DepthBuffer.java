package com.haxademic.demo.draw.context;


import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DepthBuffer;


public class Demo_DepthBuffer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DepthBuffer depthBuffer;

	protected void config() {
		// Config.setAppSize(800, 600);
	}

	public void setup() {
		super.setup();
		
		// build custom frame buffer...
		// this has to happen in setup(), probably because of something I'm doing in the settings/setup process
		depthBuffer = new DepthBuffer(1920, 1080);
	}

	protected void firstFrame() {
	}

	protected void drawApp() {
		// Draw some spheres
		pg.beginDraw();
		pg.background(0);
		pg.lights();
		pg.fill(0xff1E58F5);
		pg.noStroke();
		int numSpheres = 24;
		for (int i = 0; i < numSpheres; i++) {
			pg.pushMatrix();
			pg.translate(width * i / (float) numSpheres, height / 2 + 100 * sin(frameCount * 0.02f + i), -400 * sin(frameCount * 0.01f + i));
			pg.sphere(100);
			pg.popMatrix();
		}
		pg.endDraw();

		// Copy depth buffer from `pg`
		depthBuffer.copyDepthToPG(pg);

		// Draw original scene
		image(depthBuffer.image(), 0, 0);
		image(pg, 0, 0, width / 4, height / 4);
	}

}
