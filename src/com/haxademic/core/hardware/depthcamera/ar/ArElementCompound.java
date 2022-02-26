package com.haxademic.core.hardware.depthcamera.ar;

public class ArElementCompound 
extends ArElementCompoundBase
implements IArElement {

	public ArElementCompound() {
		super();
		
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
		{
			ArElementImage arAsset1 = new ArElementImage("../../afi-magic-of-lights/data/ar-assets/ski-poles/poles/", 0.2f, BodyTrackType.HAND_LEFT, 2);
			arAsset1.setPivotOffset(0f, 0.3f, 0);
			elements.add(arAsset1);
		}
		{
			ArElementImage arAsset2 = new ArElementImage("../../afi-magic-of-lights/data/ar-assets/ski-poles/poles/", 0.2f, BodyTrackType.HAND_RIGHT, 2);
			arAsset2.setPivotOffset(0f, 0.3f, 0);
			elements.add(arAsset2);
		}
	}
	
}
