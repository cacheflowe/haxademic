package com.haxademic.demo.media.audio.interphase.viz;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.playback.WaveformScroller;

import processing.core.PGraphics;
import processing.core.PImage;

public class InterphaseVizScrollingWaveform 
implements IAppStoreListener, IInterphaseViz {

  protected WaveformScroller waveformScroller;

  public InterphaseVizScrollingWaveform() {
    // waveformScroller = new WaveformScroller();
    
    P.store.addListener(this);
  }

  public void update(PGraphics pg) {
    // draw results
    PImage cameraImg = DepthCamera.instance(DepthCameraType.Realsense).camera.getRgbImage();
    pg.beginDraw();
    pg.background(0);
    ImageUtil.cropFillCopyImage(cameraImg, pg, true);
    pg.endDraw();

    // glitchSuite.applyTo(pg);
  }

  /////////////////////////////////////////////////////////////////
  // IAppStoreListener
  /////////////////////////////////////////////////////////////////
  
  public void updatedNumber(String key, Number val) {
    if (key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) {
      // if(val.intValue() == 0) glitchSuite.newGlitchMode();
    }
  }
  public void updatedString(String key, String val) {}
  public void updatedBoolean(String key, Boolean val) {}
  public void updatedImage(String key, PImage val) {}
  public void updatedBuffer(String key, PGraphics val) {}

}
