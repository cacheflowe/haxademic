package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.image.OpticalFlowCPU;

import processing.core.PImage;

public class UVGridOpticalFlow
extends BaseVideoFilter {
	
	protected OpticalFlowCPU opticalFlow;
	float detectionScaleDown = 0.15f;

	public UVGridOpticalFlow(int width, int height) {
		super(width, height);
	}

	public void newFrame(PImage frame) {
		super.newFrame(frame);
		
		// use copied sourceBuffer instead of frame
		// lazy-init optical flow
		if(opticalFlow == null) {
			opticalFlow = new OpticalFlowCPU(sourceBuffer, detectionScaleDown);
		}
	}
	

	public void update() {
		// set up context
		destBuffer.beginDraw();
		destBuffer.background(0);
		destBuffer.noStroke();
		destBuffer.beginDraw();

		// draw debug flow results
		if(opticalFlow != null) {
			// update OF settings
			opticalFlow.smoothing(0.04f);
			opticalFlow.update(sourceBuffer);
			float[] ofResult;
			
			// TODO: remove debug layer
//			opticalFlow.debugDraw(destBuffer, false);
			
			// draw image on top for a visual check. remove this too!
//			destBuffer.image(sourceBuffer, 0, 0);

			destBuffer.textureMode(P.IMAGE); 
			PG.setTextureRepeat(destBuffer, true);

			// draw input view to screen
			float offsetAmp = 5;
			float cellSize = 12;// + 40f * MathUtil.saw(P.p.frameCount * 0.001f);
			int cols = P.floor(destBuffer.width / cellSize);
			int rows = P.floor(destBuffer.height / cellSize);
			float colSize = destBuffer.width / cols;
			float rowSize = destBuffer.height / rows;
			
			// P.println("Shapes.createSheet() setting textureMode is weird to do here... Maybe should be PAppletHax default?");
			destBuffer.beginShape(P.QUADS);
			destBuffer.noStroke();
			destBuffer.texture(sourceBuffer);

			for(int i = 0; i < cols; i++) {
				for(int j = 0; j < rows; j++) {
					float x = i * colSize;
					float y = j * rowSize;
					
					
					// augment UV here from optical flow
					ofResult = opticalFlow.getVectorAt(x / destBuffer.width, y / destBuffer.height);
					float xU = x + ofResult[0] * offsetAmp;
					float yV = y + ofResult[1] * offsetAmp;
					float z = 0;
					
					destBuffer.normal(x, y, z);
					destBuffer.vertex(x, y, z, 						xU, yV);
					destBuffer.vertex(x + colSize, y, z, 			xU + colSize, yV);    
					destBuffer.vertex(x + colSize, y + rowSize, z, 	xU + colSize, yV + rowSize);    
					destBuffer.vertex(x, y + rowSize, z, 			xU, yV + rowSize);
				}	
			}
			
			destBuffer.endShape(); 
		}
		
		// check vector getter for a specific position
		destBuffer.endDraw();
	}
	
}
