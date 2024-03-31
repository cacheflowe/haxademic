package com.haxademic.demo.media.audio.interphase.viz;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.AppState;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.interphase.Interphase;

import processing.core.PGraphics;
import processing.core.PImage;

public class InterphaseVizTextLog 
implements IAppStoreListener, IInterphaseViz {

  protected StringBufferLog log = new StringBufferLog(50);

  public InterphaseVizTextLog() {
    P.store.addListener(this);
  }

  public void update(PGraphics pg) {
    pg.beginDraw();
    pg.background(0);
    FontCacher.setFontOnContext(pg, DemoAssets.font8px(), 0xffffffff, 1.5f, PTextAlign.LEFT, PTextAlign.TOP);
    log.printToScreen(pg, 30, 30, false);
    pg.endDraw();
  }

  /////////////////////////////////////////////////////////////////
  // IAppStoreListener
  /////////////////////////////////////////////////////////////////
  
  public void updatedNumber(String key, Number val) {
    if(!key.contains(AppState.ANIMATION_FRAME) && !key.contains("beatgrid") && !key.contains("DRAW_P") && !key.equals(Interphase.LOOP_PROGRESS)) {
      log.update("[" + key + "] | " + val);
    }
    // if (key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) log.update("SEQUENCER_TRIGGER: " + val);
  }
  public void updatedString(String key, String val) {
    log.update("[" + key + "] | " + val);
  }
  public void updatedBoolean(String key, Boolean val) {
    log.update("[" + key + "] | " + val);
  }
  public void updatedImage(String key, PImage val) {}
  public void updatedBuffer(String key, PGraphics val) {}

}
