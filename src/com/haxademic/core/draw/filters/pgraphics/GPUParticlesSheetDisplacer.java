package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.BufferMotionDetectionMap;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class GPUParticlesSheetDisplacer
extends BaseVideoFilter {
	
	protected PGraphics displacementMap;
	protected BufferMotionDetectionMap motionDetectionMap;

	protected PShape shape;
	protected PShader pointsTexturedShader;

	public GPUParticlesSheetDisplacer(int width, int height, float scaleDown) {
		super(width, height);
		
		// load & set texture
		displacementMap = PG.newPG(width, height);
//		colorMap = webcamBuffer;//DemoAssets.textureNebula();  // p.loadImage(FileUtil.getFile("images/_sketch/logo.png"));

		// count vertices for debugView
		int scaledW = P.round(width * scaleDown);
		int scaledH = P.round(height * scaleDown);
		int vertices = scaledW * scaledH; 
		DebugView.setValue("GPUParticlesSheetDisplacer Vertices", vertices);
		
		// build points vertices
		shape = P.p.createShape();
		shape.beginShape(PConstants.POINTS);
		shape.stroke(255);
		shape.strokeWeight(1);
		shape.noFill();
		for (int i = 0; i < vertices; i++) {
			float x = i % scaledW;
			float y = P.floor(i / scaledW);
			if(y % 2 == 1) x = scaledW - x - 1;
			shape.vertex(x/scaledW, y/scaledH, 0); // x/y coords are used as UV coords (0-1), and multplied by `spread` uniform
		}
		shape.endShape();
		shape.setTexture(sourceBuffer);
		
		// load points displacer shader
		pointsTexturedShader = P.p.loadShader(
			FileUtil.getPath("haxademic/shaders/point/displacement-frag.glsl"), 
			FileUtil.getPath("haxademic/shaders/point/displacement-vert.glsl")
		);
	}
	
	public void newFrame(PImage frame) {
		// store (and crop fill) frame into `sourceBuffer`
		super.newFrame(frame);
		
		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(sourceBuffer, 0.5f);
			DebugView.setTexture("sourceBuffer", sourceBuffer);
		}

		// run motion detection
		motionDetectionMap.setBlendLerp(0.2f);
		motionDetectionMap.setDiffThresh(0.05f);
		motionDetectionMap.setFalloffBW(0.2f);
		motionDetectionMap.setThresholdCutoff(0.5f);
		motionDetectionMap.setBlur(1f);
		motionDetectionMap.updateSource(sourceBuffer);
	}
	
	public void update() {
		if(motionDetectionMap == null) return;

		// update lerped motion detection buffer as the displacementMap 
		BlendTowardsTexture.instance().setBlendLerp(0.2f);
		BlendTowardsTexture.instance().setSourceTexture(motionDetectionMap.bwBuffer());
		BlendTowardsTexture.instance().applyTo(displacementMap);
		DebugView.setTexture("displacementMap", displacementMap);

		// clear background & move to center
		destBuffer.beginDraw();
		destBuffer.clear();
		
		// draw camera under
		PG.setDrawFlat2d(destBuffer, true);
		destBuffer.image(sourceBuffer, 0, 0);

		// draw vertex points. strokeWeight w/disableStyle works here for point size
		destBuffer.translate(destBuffer.width/2f, destBuffer.height/2f, 0);
		shape.disableStyle();
		destBuffer.strokeWeight(1.0f);
		destBuffer.blendMode(PBlendModes.SCREEN);

		// run shader & draw points shape
		pointsTexturedShader.set("displacementMap", displacementMap);
		pointsTexturedShader.set("colorMap", sourceBuffer);
		pointsTexturedShader.set("pointSize", 3.f);//0.5f + Mouse.xNorm * 2f); // 2.5f + 1.5f * P.sin(P.TWO_PI * percentComplete));
		pointsTexturedShader.set("width", (float) width);
		pointsTexturedShader.set("height", (float) height);
		pointsTexturedShader.set("flipY", 1);
//		pointsTexturedShader.set("spread", 1f);//0.5f + Mouse.yNorm * 2f);//2.5f + 0.5f * P.sin(P.PI + 2f * AnimationLoop.progressRads()));
		pointsTexturedShader.set("displaceStrength", 80f);//130f + 130f * P.sin(P.PI + P.TWO_PI * percentComplete));
		destBuffer.shader(pointsTexturedShader);
		destBuffer.shape(shape);
		destBuffer.resetShader();
		destBuffer.blendMode(PBlendModes.BLEND);
		destBuffer.endDraw();
	}
}
