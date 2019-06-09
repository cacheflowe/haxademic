package com.haxademic.core.hardware.depthcamera.cameras;

import processing.core.PGraphics;
import processing.core.PImage;

public interface IDepthCamera {
	public abstract void update();
	public abstract PImage getDepthImage();
	public abstract PImage getIRImage();	// should be removed? not common
	public abstract PImage getRgbImage();
	public abstract int[] getDepthData();	// should be removed? not common
	public abstract int rgbWidth();
	public abstract int rgbHeight();
	public abstract boolean isActive();
	public abstract void setMirror(boolean mirrored);
	public abstract boolean isMirrored();
	public abstract void stop();
	public abstract int getDepthAt(int x, int y);
	
	public static void drawPointCloudForRect(PGraphics pg, IDepthCamera camera, boolean mirrored, int pixelSkip, float alpha, float scale, float depthClose, float depthFar, int top, int right, int bottom, int left ) {
		pg.pushMatrix();

		// Translate and rotate
		int curZ;
		
		// Scale up by 200
		float scaleFactor = scale;
		
		pg.noStroke();
		
		for (int x = left; x < right; x += pixelSkip) {
			for (int y = top; y < bottom; y += pixelSkip) {
				curZ = camera.getDepthAt(x, y);
				// draw a point within the specified depth range
				if( curZ > depthClose && curZ < depthFar ) {
					pg.fill( 255, alpha * 255f );
				} else {
					pg.fill( 255, 0, 0, alpha * 255f );
				}
				pg.pushMatrix();
				pg.translate( x * scaleFactor, y * scaleFactor, scaleFactor * curZ/40f );
				// Draw a point
				pg.point(0, 0);
				pg.rect(0, 0, 4, 4);
				pg.popMatrix();
			}
		}
		pg.popMatrix();
	}

}