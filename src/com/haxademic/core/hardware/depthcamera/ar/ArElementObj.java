package com.haxademic.core.hardware.depthcamera.ar;

import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.hardware.depthcamera.KinectV2SkeletonsAR;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class ArElementObj 
extends ArObjectBase {

	protected PShape shape;
	protected float shapeWidth;
	protected float shapeHeight;

	public ArElementObj(PShape shape, float baseHeight, BodyTrackType bodyTrackType) {
		super(baseHeight, bodyTrackType);
		this.shape = shape;
		PVector shapeSize = PShapeUtil.getBounds(shape); 
		shapeWidth = shapeSize.x; 
		shapeHeight = shapeSize.y; 
	}
	
	protected void setLights(PGraphics pg) {
		pg.lights();
		pg.ambient(127);
		pg.lightSpecular(130, 130, 130); 
		pg.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		pg.directionalLight(200, 200, 200, 0.0f, 0.0f, -1);
	}

	protected void resetLights(PGraphics pg) {
		pg.noLights();
	}

	public void draw(PGraphics pg) {
		pg.push();
		setLights(pg);
		pg.translate(position.x, position.y);
		setRotationOnContext(pg);
		float responsiveHeight = KinectV2SkeletonsAR.CAMERA_HEIGHT * baseScale;
		float shapeScale = MathUtil.scaleToTarget(shapeHeight, responsiveHeight);
		pg.translate(
				positionOffset.x * userScale * responsiveHeight, 
				positionOffset.y * userScale * responsiveHeight, 
				positionOffset.z * userScale * responsiveHeight
		);
		pg.scale(userScale * shapeScale);
		pg.shape(shape, 0, 0);
		resetLights(pg);
		pg.pop();
		
		// once we've drawn, we're lerping
		isReset = false;
	}
	
}
