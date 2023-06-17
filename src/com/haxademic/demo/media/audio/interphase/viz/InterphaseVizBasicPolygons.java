package com.haxademic.demo.media.audio.interphase.viz;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;

@SuppressWarnings("rawtypes")
public class InterphaseVizBasicPolygons 
implements IAppStoreListener, IInterphaseViz {

  protected ParticleSystem particles;

  public InterphaseVizBasicPolygons() {
    particles = new ParticleSystem(ParticleCustomPolygons.class);
    P.store.addListener(this);
  }

  public void update(PGraphics pg) {
    // draw results
    pg.beginDraw();
    pg.background(0);
    PG.setDrawCenter(pg);
    PG.setCenterScreen(pg);
    particles.updateAndDrawParticles(pg, P.BLEND);
    pg.endDraw();

    // postprocessing
    BloomFilter.instance().setStrength(5f);
    BloomFilter.instance().setBlurIterations(5);
    BloomFilter.instance().setBlendMode(BloomFilter.BLEND_SCREEN);
    // BloomFilter.instance().applyTo(pg);

    GrainFilter.instance().setTime(FrameLoop.count(0.01f));
    GrainFilter.instance().setCrossfade(0.11f);
    // GrainFilter.instance().applyTo(pg);
  }

  protected ParticleCustomPolygons launchParticleType(ParticleCustomPolygons.ParticleType type, int lifespan, int partiColor) {
    // set reasonable particle defaults, overridden by type of Interphase channel/instrument
    ParticleCustomPolygons particle = (ParticleCustomPolygons) particles.launchParticle(0, 0, 0);
    particle
        .setType(type)
        .setSpeed(0, 0, -0.1f) // z-speed ensures proper z-stacking
        .setAcceleration(1, 1, 1)
        .setLifespan(lifespan)
        .setLifespanSustain(0)
        .setRotation(0, 0, 0, 0, 0, 0)
        .setColor(partiColor);
    return particle;
  }

  protected void triggerParticles(int index) {
    int partiColor = ColorsHax.colorFromGroupAt(9, index);
    if(index == 0) {
      launchParticleType(ParticleCustomPolygons.ParticleType.KICK, 30, partiColor);
    } else if(index == 1) {
      float rotInit = P.PI / 3f / 2f;
      launchParticleType(ParticleCustomPolygons.ParticleType.SNARE, 25, partiColor)
          .setGravity(0, 0.0f, 0)
          .setRotation(0, 0, rotInit, 0, 0, 0);
      // launchParticleType(ParticleCustom.ParticleType.SNARE, 25, partiColor)
      // 		.setGravity(0, 0.3f, 0)
      // 		.setRotation(0, 0, rotInit + P.PI, 0, 0, 0);
    } else if(index == 2) {
      launchParticleType(ParticleCustomPolygons.ParticleType.HAT, 20, partiColor)
          .setSpeed(0, -2, -0.1f) // z-speed ensures proper z-stacking
          .setGravity(0, 0.2f, 0)
          .setRotation(0, 0, 0, 0, 0, 0);
    } else if(index == 3) {
      float numParticles = 32;
      float speedAmp = 40;
      float decel = 0.95f;
      float segmentRads = P.TWO_PI / numParticles;
      for (int i = 0; i < numParticles; i++) {
        float curRads = segmentRads * i;
        float speedX = speedAmp * P.cos(curRads);
        float speedY = speedAmp * P.sin(curRads);
        launchParticleType(ParticleCustomPolygons.ParticleType.PERC, 40, partiColor)
            .setSpeed(speedX, speedY, -0.1f) // z-speed ensures proper z-stacking
            .setAcceleration(decel, decel, 1)
            .setRotation(0, 0, curRads - P.PI + segmentRads, 0, 0, 0);
      }
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
  
    public static enum ParticleType {
      KICK,
      SNARE,
      HAT,
      PERC,
      SFX,
      BASS,
      KEYS,
      LEAD,
    }
    protected ParticleType type;
  
    public ParticleCustomPolygons() {
      super();
    }
  
    protected ParticleCustomPolygons setType(ParticleType type) {
      this.type = type;
      return this;
    }
  
    protected void drawParticle(PGraphics pg) {
      // common props
      float minDim = P.min(pg.width, pg.height);
      float progress = Penner.easeOutQuad(this.ageProgress());
      float progressAlpha = Penner.easeInQuad(this.ageProgress());
  
      // draw different types of shapes
      if (type == ParticleType.KICK) {
        pg.fill(color, (255 - 255 * progressAlpha));
        float partiSize = P.map(progress, 0, 1, minDim * 0.1f, minDim * 0.65f);
        float thickness = minDim * 0.05f;
        Shapes.drawDisc(pg, partiSize, partiSize - thickness, 6);
      } else if (type == ParticleType.SNARE) {
        pg.fill(color, (255 - 255 * progressAlpha));
        float partiSize = P.map(progress, 0, 1, minDim * 0.15f, minDim * 0.6f);
        float thickness = minDim * 0.1f;
        Shapes.drawDisc(pg, partiSize, partiSize - thickness, 30);
        // Shapes.drawPolygon(pg, P.map(progress, 0, 1, minDim * 0.5f, minDim * 0.3f), 3);
      } else if (type == ParticleType.HAT) {
        pg.fill(color, (255 - 255 * progressAlpha));
        float partiSize = P.map(progress, 0, 1, minDim * 0.1f, minDim * 0.3f);
        float thickness = minDim * 0.1f;
        Shapes.drawDisc(pg, partiSize, partiSize - thickness, 4);
      } else if (type == ParticleType.PERC) {
        pg.fill(color);
        float partiSize = P.map(progress, 0, 1, minDim * 0.05f, 0);
        Shapes.drawPolygon(pg, partiSize, 3);
      }
    }
  
  }
}



