package com.haxademic.core.hardware.depthcamera.ar;

import KinectPV2.KJoint;
import processing.core.PGraphics;
import processing.core.PVector;

public interface IArElement {
	public enum BodyTrackType {
		CUSTOM,
		HEAD,
		HAND,
		HAND_POINT,
		HAND_FLAG,
		HANG_ON_SHOULDERS,
	}
	
	public void setActive(boolean isActive);
	public boolean isActive();
	public PVector position();
	public IArElement setPosition(float x, float y, float z);
	public IArElement setPositionOffset(float x, float y, float z);
	public IArElement setPivotOffset(float x, float y, float z);
	public IArElement setRotation(float x, float y, float z);
	public IArElement setRotationOffset(float x, float y, float z);
	public IArElement setBaseScale(float scale);
	public IArElement setScale(float scale);
	public IArElement setJoints(KJoint[] joints2d, KJoint[] joints3d);
	public void updatePre(PGraphics pg);
	public void draw(PGraphics pg);
	public void drawOrigin(PGraphics pg);
}