package com.haxademic.demo.media.audio.interphase.viz;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.audio.interphase.Interphase;

import processing.core.PGraphics;
import processing.core.PImage;

@SuppressWarnings("rawtypes")
public class InterphaseVizBasicLines 
implements IAppStoreListener, IInterphaseViz {

  protected ParticleSystem particles;

  public InterphaseVizBasicLines() {
    particles = new ParticleSystem(ParticleCustomPolygons.class);
    P.store.addListener(this);
  }

  public void update(PGraphics pg) {
    // draw results
    pg.beginDraw();
    pg.background(0);
    // PG.setDrawCenter(pg);
    // PG.setCenterScreen(pg);
    particles.updateAndDrawParticles(pg, P.BLEND);
    pg.endDraw();
  }

  protected ParticleCustomPolygons launchParticleSize(int size, int lifespan, int partiColor) {
    // set reasonable particle defaults, overridden by type of Interphase channel/instrument
    ParticleCustomPolygons particle = (ParticleCustomPolygons) particles.launchParticle(0, 0, 0);
    particle
        .setSpeed(25, 0, -0.01f) // z-speed ensures proper z-stacking
        .setAcceleration(0.99f, 1, 1)
        .setGravity(0, 0, 0)
        .setLifespan(lifespan)
        .setLifespanSustain(0)
        .setRotation(0, 0, 0, 0, 0, 0)
        .setColor(partiColor)
        .setSize(size);
    return particle;
  }

  protected void triggerParticles(int index) {
    int partiColor = ColorsHax.colorFromGroupAt(8, index);
    if(index == 0) {
      launchParticleSize(80, 140, partiColor);
    } else if(index == 1) {
      launchParticleSize(80, 140, partiColor);
    } else if(index == 2) {
      launchParticleSize(80, 140, partiColor);
    } else if(index == 3) {
      launchParticleSize(80, 140, partiColor);
    } else if(index == 4) {
      launchParticleSize(80, 140, partiColor);
    } else if(index == 5) {
      launchParticleSize(80, 140, partiColor);
    } else if(index == 6) {
      launchParticleSize(80, 140, partiColor);
    } else if(index == 7) {
      launchParticleSize(80, 140, partiColor);
    }
  }

  /////////////////////////////////////////////////////////////////
  // IAppStoreListener
  /////////////////////////////////////////////////////////////////
  
  public void updatedNumber(String key, Number val) {
    if (key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) {
      triggerParticles(val.intValue());
    }
  }
  public void updatedString(String key, String val) {}
  public void updatedBoolean(String key, Boolean val) {}
  public void updatedImage(String key, PImage val) {}
  public void updatedBuffer(String key, PGraphics val) {}




  /////////////////////////////////////////////////////////////////
  // Custom Particle
  /////////////////////////////////////////////////////////////////
  
  public static class ParticleCustomPolygons<T>
  extends Particle {
    
    public ParticleCustomPolygons() {
      super();
    }
    
    protected void drawParticle(PGraphics pg) {
      // common props
      float progress = Penner.easeOutQuad(this.ageProgress());
      float progressAlpha = Penner.easeInQuad(this.ageProgress());
      float partiSize = P.map(progress, 0, 1, size, 0);
      partiSize = sizeMax;
      float angle = P.map(progress, 0, 1, P.QUARTER_PI, 0);
      pg.fill(color, (255 - 255 * progressAlpha));
      
      pg.push();
      pg.translate(0, pg.height / 2, 0);
      pg.rotate(angle);
      pg.rect(0, 0, partiSize, pg.height);
      pg.pop();

      pg.push();
      pg.translate(0, pg.height / 2, 0);
      pg.rotate(P.PI - angle);
      pg.translate(-partiSize, 0);
      pg.rect(0, 0, partiSize, pg.height);
      pg.pop();
    }
  
  }
}



