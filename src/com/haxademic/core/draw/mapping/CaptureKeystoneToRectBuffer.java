package com.haxademic.core.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;

import processing.core.PGraphics;

public class CaptureKeystoneToRectBuffer
extends BaseSavedQuadUI {
	
	protected PGraphics sourceBuffer;
	protected PGraphics capturedBuffer;
	
	public CaptureKeystoneToRectBuffer(PGraphics source, int destW, int destH, String mapFilePath) {
		super(source.width, source.height, mapFilePath);
		// destination buffer for mapping result
		sourceBuffer = source;
		capturedBuffer = P.p.createGraphics(destW, destH, PRenderers.P3D);
	}
	
	public PGraphics mappedBuffer() {
		return capturedBuffer;
	}
	
	// RE-DRAW ///////////////////////////////////////////////
		
	public void update() {
		// DRAW CAPTURED QUAD TO LOCAL BUFFER w/UV COORDINATES
		capturedBuffer.beginDraw();
		capturedBuffer.beginShape(P.QUADS);
		capturedBuffer.texture(sourceBuffer);
		capturedBuffer.vertex(0, 0, 0, 											topLeft.x, topLeft.y);
		capturedBuffer.vertex(capturedBuffer.width, 0, 0, 						topRight.x, topRight.y);
		capturedBuffer.vertex(capturedBuffer.width, capturedBuffer.height, 0, 	bottomRight.x, bottomRight.y);
		capturedBuffer.vertex(0, capturedBuffer.height, 0, 						bottomLeft.x, bottomLeft.y);
		capturedBuffer.endShape();
		capturedBuffer.endDraw();
		
		// attempt at shader option:
		// draw mapped capture to buffer
		//		quadMapper.set("sourceTexture", sourceBuffer);
		//		quadMapper.set("topLeft", (float) _topLeft.x / (float) sourceBuffer.width, (float) _topLeft.y / (float) sourceBuffer.height);
		//		quadMapper.set("botLeft", (float) _bottomLeft.x / (float) sourceBuffer.width, (float) _bottomLeft.y / (float) sourceBuffer.height);
		//		quadMapper.set("topRight", 1f + 1f * (float) _topRight.x / (float) sourceBuffer.width, 1f + 1f * (float) _topRight.y / (float) sourceBuffer.height);
		//		quadMapper.set("botRight", (float) _bottomRight.x / (float) sourceBuffer.width, 1f - (float) _bottomRight.y / (float) sourceBuffer.height);
		//		dest.filter(quadMapper);
	}
	
}
