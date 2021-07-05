package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.joystick.BaseJoysticksCollection;
import com.haxademic.core.hardware.joystick.IJoystickCollection;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class DepthCameraOverheadGrid
extends BaseJoysticksCollection
implements IJoystickCollection {

	protected static DepthSilhouetteSmoothed silhouette;
	protected static PGraphics debugBuffer;
	protected int depthNear = 0;
	protected int depthFar = 0;
	protected int depthDepth = 0;

	// debug drawing helpers
	protected int rows = 1;


	public DepthCameraOverheadGrid(int cols, int rows, int depthNear, int depthFar, float padding, float depthLeft, float depthRight, float depthTop, float depthBottom, int depthPixelSkip, int minPixels) {
		super();
		
		// build silhouette buffer without smoothing operations
		silhouette = new DepthSilhouetteSmoothed(DepthCamera.instance().camera, depthPixelSkip, depthNear, depthFar);
		silhouette.setSmoothing(0);
		PGraphics depthBuffer = silhouette.depthBuffer();
		
		// calculate sizes to depth buffer
		this.rows = rows;
		this.depthNear = depthNear;
		this.depthFar = depthFar;
		this.depthDepth = depthFar - depthNear;
		
		int leftX = P.floor(depthBuffer.width * depthLeft);
		int rightX = P.floor(depthBuffer.width - depthBuffer.width * depthRight);
		int topY = P.floor(depthBuffer.height * depthTop);
		int botY = P.floor(depthBuffer.height - depthBuffer.height * depthBottom);
		int padSize = P.floor(depthBuffer.width * padding);

		// set up rectangles (regions) for position detection
		// scale to the mini depth silhouette buffer
		int paddingPixels = P.floor(padding * depthBuffer.width);
		int boundsW = rightX - leftX;
		int boundsH = botY - topY;
		int colW = (boundsW - padSize * (cols-1)) / cols;
		int rowH = (boundsH - padSize * (rows-1)) / rows;
//		int depthDepth = depthFar - depthNear;
		int startX = leftX;
		int startY = topY;

		for ( int x = 0; x < cols; x++ ) {
			for ( int y = 0; y < rows; y++ ) {
				DepthCameraOverheadRegion region = new DepthCameraOverheadRegion(
						depthBuffer,
						startX + colW * x + paddingPixels * x,
						startX + colW * x + paddingPixels * x + colW - 1,
						startY + rowH * y + paddingPixels * y,
						startY + rowH * y + paddingPixels * y + rowH - 1,
						depthPixelSkip,
						minPixels,
						P.p.color( MathUtil.randRange(130,255), MathUtil.randRange(130,255), MathUtil.randRange(130,255) )
				);
				_joysticks.add( region );
			}
		}
	}
	
	public PGraphics depthBuffer() {
		return silhouette.depthBuffer();
	}

	public void update() {
		silhouette.update();
		updateRegions();
	}
	
	public void update(boolean debug) {
		update();
		if(debug == true) {
			if(debugBuffer == null) debugBuffer = PG.newPG(silhouette.depthBuffer().width, silhouette.depthBuffer().height);
			updateDebug();
		}
	}

	public void updateRegions() {
		for( int i=0; i < _joysticks.size(); i++ ) {
			_joysticks.get(i).update(debugBuffer); // debugBuffer
		}
	}

	public void updateDebug() {
		debugBuffer.beginDraw();
		debugBuffer.background(0);
		
		// copy base silhouette buffer
		debugBuffer.image(silhouette.depthBuffer(), 0, 0);

		// draw regions' rectangles ----------------------------
		for( int i=0; i < _joysticks.size(); i++ ) {
			_joysticks.get(i).drawDebug(debugBuffer);
		}

		debugBuffer.endDraw();
	}
	
	public PImage debugImage() {
		return debugBuffer;
	}

}