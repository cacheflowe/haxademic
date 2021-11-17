package com.haxademic.core.hardware.depthcamera.ar;

import java.util.ArrayList;

import KinectPV2.KJoint;
import processing.core.PGraphics;

public class ArElementCompound 
extends ArObjectBase
implements IArElement {

	protected ArrayList<IArElement> elements;

	public ArElementCompound() {
		super(1, BodyTrackType.CUSTOM);
		
		elements = new ArrayList<IArElement>();
		{
			ArElementImage arAsset = new ArElementImage("../../afi-magic-of-lights/data/ar-assets/snow-swirl/", 0.65f, BodyTrackType.WAIST, 30);
			arAsset.setPositionOffset(0, 0, 0f);
			elements.add(arAsset);
		}
		{
			ArElementImage arAsset = new ArElementImage("../../afi-magic-of-lights/data/ar-assets/snowglobe/", 0.375f, BodyTrackType.HEAD, 2);
			arAsset.setPositionOffset(0, 0.075f, 0f);
			arAsset.addFadeIn();
			elements.add(arAsset);
		}
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
		ArElementImage snowImage = ((ArElementImage) elements.get(0));
		for (int i = 0; i < elements.size(); i++) {
			// special logic to start a second ar element after the first is mostly finished playing a sequence
			if(i == 0 || snowImage.progress() > 0.8f || snowImage.isPlaying() == false) {
				elements.get(i).draw(pg);
			}
		}
	}
	
	public void setActive(boolean isActive) {
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).setActive(isActive);
		}
		
		// special scenario for restarting child animation
		if(isActive) {
			((ArElementImage) elements.get(0)).play();
		}
	}
	
}
