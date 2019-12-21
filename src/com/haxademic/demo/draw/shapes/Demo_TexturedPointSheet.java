package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_TexturedPointSheet
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape pointsShape;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	public void setupFirstFrame() {
		// build points shape
		pointsShape = p.createShape();
		pointsShape.beginShape(PConstants.POINTS);
		pointsShape.noFill();
		float spread = 5f; 
		pointsShape.strokeWeight(spread * 0.75f);
		PImage img = DemoAssets.smallTexture();
		for (int x = 0; x < img.width; x++) {
			for (int y = 0; y < img.height; y++) {
				int pixelColor = ImageUtil.getPixelColor(img, x, y);
				pointsShape.stroke(pixelColor);
				pointsShape.vertex(x * spread, y * spread);
			}
		}
		pointsShape.endShape();
	}

	public void drawApp() {
		// set context
		p.background(127);
		p.pushMatrix();
		
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);
		
		// draw shape
		p.scale(1f + 2f * Mouse.yNorm);
		p.shape(pointsShape, 0, 0);
		p.popMatrix();
	}

}
