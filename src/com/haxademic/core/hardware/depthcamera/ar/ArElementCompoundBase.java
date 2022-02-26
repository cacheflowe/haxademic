package com.haxademic.core.hardware.depthcamera.ar;

import java.util.ArrayList;

import com.haxademic.core.hardware.depthcamera.ar.ArObjectBase;
import com.haxademic.core.hardware.depthcamera.ar.IArElement;

import KinectPV2.KJoint;
import processing.core.PGraphics;

public class ArElementCompoundBase 
extends ArObjectBase
implements IArElement {

	protected ArrayList<IArElement> elements = new ArrayList<IArElement>();

	public ArElementCompoundBase() {
		super(1, BodyTrackType.CUSTOM);
	}
	
	/////////////////////////////////////////
	// Pass-through to children
	/////////////////////////////////////////
	
	public IArElement setJoints(KJoint[] joints2d, KJoint[] joints3d) {
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).setJoints(joints2d, joints3d);
		}
		return this;
	}

	public IArElement setScale(float scale) {
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).setScale(scale);
		}
		return this;
	}
	
	public IArElement setBaseScale(float scale) {
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).setBaseScale(scale);
		}
		return this;
	}
	
	public void updatePre(PGraphics pg) {
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).updatePre(pg);
		}
	}

	public boolean isActive() {
		return elements.get(0).isActive();
	}
	
	/////////////////////////////////////////
	// Customized behavior
	/////////////////////////////////////////
	
	public void draw(PGraphics pg) {
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).draw(pg);
		}
	}
	
	public void setActive(boolean isActive) {
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).setActive(isActive);
		}
	}
	
}
