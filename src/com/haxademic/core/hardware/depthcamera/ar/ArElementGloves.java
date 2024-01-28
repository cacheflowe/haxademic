package com.haxademic.core.hardware.depthcamera.ar;

import com.haxademic.core.app.P;

public class ArElementGloves
extends ArElementCompoundBase {

  public ArElementGloves(String glovePathLeft, String glovePathRight) {
    this(glovePathLeft, glovePathRight, 0.15f, null);
  }

  public ArElementGloves(String glovePathLeft, String glovePathRight, float scale) {
    this(glovePathLeft, glovePathRight, scale, null);
  }

  public ArElementGloves(String glovePathLeft, String glovePathRight, float scale, IArElement extraElement) {
    super();
    {
      ArElementImage arAsset = new ArElementImage(glovePathLeft, 0.15f, BodyTrackType.HAND_POINT_RIGHT);
      arAsset.setRotationOffset(0, 0, P.HALF_PI);
      // arAsset.setPositionOffset(0.5f, 0, 0);
      elements.add(arAsset);
    }
    {
      ArElementImage arAsset = new ArElementImage(glovePathRight, 0.15f, BodyTrackType.HAND_POINT_LEFT);
      arAsset.setRotationOffset(0, 0, P.HALF_PI);
      // arAsset.setPositionOffset(0.5f, 0, 0);
      elements.add(arAsset);
    }
    if (extraElement != null) {
      elements.add(extraElement);
    }
  }

}
